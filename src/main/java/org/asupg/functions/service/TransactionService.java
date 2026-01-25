package org.asupg.functions.service;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.model.TransactionDTO;
import org.asupg.functions.repository.TransactionRepository;
import org.asupg.functions.util.ParserUtil;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> bulkSaveTransaction(List<TransactionDTO> transactions) {

        if (transactions.isEmpty()) {
            log.info("Transaction list is empty");
            return List.of();
        }

        log.info("Starting bulk save of {} transactions", transactions.size());

        List<TransactionDTO> successfullySaved = new ArrayList<>();
        int duplicateCount = 0;
        int errorCount = 0;

        for (TransactionDTO transaction : transactions) {
            try {
                transactionRepository.insert(transaction);
                successfullySaved.add(transaction);

            } catch (DuplicateKeyException e) {
                duplicateCount++;

            } catch (Exception e) {
                errorCount++;
                log.error(
                        "Error while saving transaction {}",
                        transaction.getTransactionId(),
                        e
                );
            }
        }

        log.info(
                "Bulk operation complete. Saved: {}, Duplicates: {}, Errors: {}",
                successfullySaved.size(),
                duplicateCount,
                errorCount
        );

        return successfullySaved;
    }

    public List<TransactionDTO> bulkUpdateTransactions(List<TransactionDTO> transactions) {

        if (transactions.isEmpty()) {
            log.info("Transaction list is empty");
            return List.of();
        }

        log.info("Starting bulk update of {} transactions", transactions.size());

        List<TransactionDTO> successfullySaved = new ArrayList<>();
        int errorCount = 0;

        for (TransactionDTO transaction : transactions) {
            try {
                transactionRepository.save(transaction);
                successfullySaved.add(transaction);

            } catch (Exception e) {
                errorCount++;
                log.error(
                        "Error while updating transaction {}",
                        transaction.getTransactionId(),
                        e
                );
            }
        }

        log.info(
                "Bulk operation complete. Updated: {}, Errors: {}",
                successfullySaved.size(),
                errorCount
        );

        return successfullySaved;
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

        bulkSaveTransaction(transactions);
    }

}
