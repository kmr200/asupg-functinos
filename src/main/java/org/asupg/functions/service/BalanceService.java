package org.asupg.functions.service;

import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.*;
import org.asupg.functions.repository.CosmosCompanyRepository;
import org.asupg.functions.repository.CosmosTransactionRepository;
import org.asupg.functions.util.ParserUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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

            BigDecimal total = company.getCurrentBalance() == null
                    ? BigDecimal.ZERO
                    : company.getCurrentBalance();

            for (TransactionDTO transaction : companyTransactions) {
                if (transaction.getAmount() != null) {
                    total = total.add(transaction.getAmount());
                }
            }

            company.setBalanceUpdatedAt(LocalDateTime.now());

            company.setCurrentBalance(total);
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
            } else if (foundCompaniesInn.contains(inn)) {
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

    public List<CompanyDTO> applyMonthlyCharge() {
        List<CompanyDTO> companies = cosmosCompanyRepository.findAllBillable();

        YearMonth currentMonth = YearMonth.now(ZoneOffset.UTC);
        List<CompanyDTO> companiesToUpdate = new ArrayList<>();

        for (CompanyDTO company : companies) {
            BigDecimal currentBalance = company.getCurrentBalance();
            BigDecimal monthlyCharge = company.getMonthlyRate();

            if (currentBalance == null || monthlyCharge == null) {
                log.error("Invalid billing data for company {}", company.getInn());
                continue;
            }

            if (currentMonth.isBefore(company.getBillingStartMonth())) {
                log.info("Billing hasn't started yet for: {}", company.getInn());
                continue;
            }

            if (company.getLastBilledMonth() != null &&
                    currentMonth.equals(company.getLastBilledMonth())
            ) {
                log.info("Company {} has already been charged for {}", company.getInn(), company.getLastBilledMonth());
                continue;
            }

            company.setCurrentBalance(
                    currentBalance.subtract(monthlyCharge)
            );
            company.setLastBilledMonth(currentMonth);
            company.setBalanceUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            companiesToUpdate.add(company);
        }
        log.debug("Companies to update: {}", companiesToUpdate);
        List<CompanyDTO> failedToUpdateCompanies = cosmosCompanyRepository.bulkUpdateCompanies(companiesToUpdate);

        companiesToUpdate.removeAll(failedToUpdateCompanies);

        return companiesToUpdate;
    }

    public void generateTransactionForMonthlyCharges(List<CompanyDTO> chargedCompanies) {
        List<TransactionDTO> transactions = new ArrayList<>();

        for (CompanyDTO company : chargedCompanies) {
            transactions.add(
                    new TransactionDTO(
                            ParserUtil.buildTransactionHash(
                                    TransactionDTO.TransactionType.MONTHLY_CHARGE,
                                    YearMonth.now(ZoneOffset.UTC),
                                    company.getInn(),
                                    company.getMonthlyRate()),
                            company.getName(),
                            company.getInn(),
                            company.getMonthlyRate()
                    )
            );
        }

        log.info("Generated transactions for chargedCompanies: {}", transactions);

        cosmosTransactionRepository.bulkSaveTransaction(transactions);
    }


}
