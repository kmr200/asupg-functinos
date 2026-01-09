package org.asupg.functions.service;

import org.asupg.functions.model.*;
import org.asupg.functions.repository.CosmosCompanyRepository;
import org.asupg.functions.repository.CosmosTransactionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
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

        // If company was found update company balance and update status to FOUND
        Set<String> foundCompaniesInn = lookupResult.companiesToUpdate()
                .stream()
                .map(CompanyDto::getInn)
                .collect(Collectors.toSet());

        List<CompanyDto> companiesToUpdate = lookupResult.companiesToUpdate();

        Map<String, List<TransactionDTO>> transactionsByInn =
                transactions.stream()
                        .filter(t -> t.getCounterpartyInn() != null)
                        .collect(Collectors.groupingBy(TransactionDTO::getCounterpartyInn));


        for (CompanyDto company : companiesToUpdate) {
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

            company.setTotalPaid(total);
        }

        cosmosCompanyRepository.bulkUpdateCompanies(companiesToUpdate);

        transactions.stream()
                .filter(transaction -> foundCompaniesInn.contains(transaction.getCounterpartyInn()))
                .forEach(transaction -> transaction.setReconciliation(new ReconciliationDto(ReconciliationStatus.MATCHED)));

        // If company was NOT found update status to NOT_FOUND
        Set<String> notFoundCompaniesInn = new HashSet<>(lookupResult.notFoundCompanies());

        transactions.stream()
                .filter(transaction -> notFoundCompaniesInn.contains(transaction.getCounterpartyInn()))
                .forEach(transaction -> transaction.setReconciliation(new  ReconciliationDto(ReconciliationStatus.NOT_FOUND)));

        cosmosTransactionRepository.bulkUpdateTransactions(transactions);

        return transactions;
    }


}
