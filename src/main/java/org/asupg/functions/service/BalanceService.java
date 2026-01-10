package org.asupg.functions.service;

import org.asupg.functions.model.*;
import org.asupg.functions.repository.CosmosCompanyRepository;
import org.asupg.functions.repository.CosmosTransactionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class BalanceService {

    private final CosmosCompanyRepository cosmosCompanyRepository;
    private final CosmosTransactionRepository cosmosTransactionRepository;

    @Inject
    public BalanceService(
            CosmosCompanyRepository cosmosCompanyRepository,
            CosmosTransactionRepository cosmosTransactionRepository
    ) {
        this.cosmosCompanyRepository = cosmosCompanyRepository;
        this.cosmosTransactionRepository = cosmosTransactionRepository;
    }

    public List<TransactionDTO> bulkUpdateBalance(List<TransactionDTO> transactions) {

        // Extract inn from transactions
        Set<String> listOfCompanyInn = transactions
                .stream()
                .map(TransactionDTO::getCounterpartyInn)
                .collect(Collectors.toSet());

        // Lookup companies
        CompanyLookupResult lookupResult = cosmosCompanyRepository.getCompaniesToUpdate(listOfCompanyInn);

        // If company was found update company balance
        Set<String> foundCompaniesInn = lookupResult.companiesToUpdate()
                .stream()
                .map(CompanyDTO::getInn)
                .collect(Collectors.toSet());

        List<CompanyDTO> companiesToUpdate = lookupResult.companiesToUpdate();

        Map<String, List<TransactionDTO>> transactionsByInn =
                transactions.stream()
                        .filter(t -> t.getCounterpartyInn() != null)
                        .collect(Collectors.groupingBy(TransactionDTO::getCounterpartyInn));


        for (CompanyDTO company : companiesToUpdate) {
            String inn = company.getInn();

            List<TransactionDTO> companyTransactions =
                    transactionsByInn.getOrDefault(inn, List.of());

            BigDecimal total = company.getTotalPaid() == null
                    ? BigDecimal.ZERO
                    : company.getTotalPaid();

            for (TransactionDTO transaction : companyTransactions) {
                if (transaction.getAmount() != null) {
                    total = total.add(transaction.getAmount());
                }
            }

            company.setLastPaymentDate(LocalDate.now());

            company.setTotalPaid(total);
        }

        // Update Transactions accordingly
        Set<String> failedToUpdateCompaniesInn = cosmosCompanyRepository.bulkUpdateCompanies(companiesToUpdate)
                .stream()
                .map(CompanyDTO::getInn)
                .collect(Collectors.toSet());

        Set<String> notFoundCompaniesInn = lookupResult.notFoundCompanies();

        for (TransactionDTO transaction : transactions) {
            String inn = transaction.getCounterpartyInn();

            if (inn == null || inn.isBlank()) {
                transaction.setReconciliation(
                        new ReconciliationDTO(
                                ReconciliationStatus.ERROR,
                                "Missing counterparty INN"
                        )
                );
                continue;
            }

            if (failedToUpdateCompaniesInn.contains(inn)) {
                // If an error occurred while updating Company balance, reflect it in Transaction
                transaction.setReconciliation(new ReconciliationDTO(ReconciliationStatus.ERROR, "Failed to update company balance"));
            }else if (foundCompaniesInn.contains(inn)) {
                // If company balance was updated successfully, reflect it in Transaction
                transaction.setReconciliation(new ReconciliationDTO(ReconciliationStatus.MATCHED));
            } else if (notFoundCompaniesInn.contains(inn)) {
                // If company was not found, reflect it in Transaction
                transaction.setReconciliation(new ReconciliationDTO(ReconciliationStatus.NOT_FOUND));
            }
        }

        cosmosTransactionRepository.bulkUpdateTransactions(transactions);

        return transactions;
    }


}
