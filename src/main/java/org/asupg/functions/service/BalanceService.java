package org.asupg.functions.service;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.*;
import org.asupg.functions.repository.CompanyRepository;
import org.asupg.functions.repository.TransactionRepository;
import org.asupg.functions.util.ParserUtil;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final TransactionService transactionService;
    private final CompanyService companyService;

    public List<TransactionDTO> bulkUpdateBalance(List<TransactionDTO> transactions) {

        // Extract inn from transactions
        Set<String> listOfCompanyInn = transactions
                .stream()
                .map(TransactionDTO::getCounterpartyInn)
                .collect(Collectors.toSet());

        // Lookup companies
        List<CompanyDTO> foundCompanies = companyService.getAllIn(listOfCompanyInn);

        // If company was found update company balance
        Set<String> foundCompaniesInn = extractCompanyInns(foundCompanies);

        Map<String, List<TransactionDTO>> transactionsByInn =
                transactions.stream()
                        .filter(t -> t.getCounterpartyInn() != null)
                        .collect(Collectors.groupingBy(TransactionDTO::getCounterpartyInn));


        for (CompanyDTO company : foundCompanies) {
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
        Set<String> failedToUpdateCompaniesInn = extractCompanyInns(
                companyService.bulkUpdateCompanies(
                        foundCompanies
                )
        );

        Set<String> notFoundCompaniesInn = new HashSet<>(listOfCompanyInn);
        notFoundCompaniesInn.removeAll(foundCompaniesInn);

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

        transactionService.bulkUpdateTransactions(transactions);

        return transactions;
    }

    public List<CompanyDTO> applyMonthlyCharge() {

        List<CompanyDTO> companiesToUpdate = companyService.getCompaniesToUpdate();

        log.debug("Companies to update: {}", companiesToUpdate);
        List<CompanyDTO> failedToUpdateCompanies = companyService.bulkUpdateCompanies(companiesToUpdate);

        companiesToUpdate.removeAll(failedToUpdateCompanies);

        return companiesToUpdate;
    }


    private Set<String> extractTransactionInns(List<TransactionDTO> transactions) {
        return transactions.stream()
                .map(TransactionDTO::getCounterpartyInn)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> extractCompanyInns(List<CompanyDTO> transactions) {
        return transactions.stream()
                .map(CompanyDTO::getInn)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


}
