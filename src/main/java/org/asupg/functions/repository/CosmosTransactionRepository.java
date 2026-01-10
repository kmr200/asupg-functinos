package org.asupg.functions.repository;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asupg.functions.model.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CosmosTransactionRepository {

    private static final Logger logger = LoggerFactory.getLogger(CosmosTransactionRepository.class);

    private final CosmosContainer transactionsContainer;
    private final ObjectMapper objectMapper;

    @Inject
    public CosmosTransactionRepository(
            @Named("transactionClient") CosmosContainer transactionsContainer,
            ObjectMapper objectMapper
    ) {
        this.transactionsContainer = transactionsContainer;
        this.objectMapper = objectMapper;
    }

    /**
     * @param transactions Transactions to be saved
     * @return Transactions that were successfully saved
     */
    public List<TransactionDTO> bulkSaveTransaction(List<TransactionDTO> transactions) {
        if (transactions.isEmpty()) {
            logger.info("Transaction list is empty");
            return List.of();
        }

        logger.info("Starting bulk save of {} transactions",  transactions.size());

        List<CosmosItemOperation> operations = transactions.stream()
                .map(transaction -> {
                    return CosmosBulkOperations.getCreateItemOperation(
                            transaction,
                            new PartitionKey(transaction.getDate().toString()),
                            transaction
                    );
                }).toList();

        Iterable<CosmosBulkOperationResponse<Object>> responses = transactionsContainer.executeBulkOperations(operations);

        ArrayList<TransactionDTO> successfullySaved = new ArrayList<>();

        int duplicateCount = 0;
        int errorCount = 0;

        for (CosmosBulkOperationResponse<Object> response : responses) {

            TransactionDTO transaction = response.getOperation().getContext();

            if (response.getResponse() != null && response.getResponse().isSuccessStatusCode()) {
                successfullySaved.add(transaction);
            } else if (response.getResponse() != null && response.getResponse().getStatusCode() == 409) {
                duplicateCount++;
            } else {
                errorCount++;
                if (response.getException() != null) {
                    logger.error("Error while saving transaction: {}",  transaction.getTransactionId(), response.getException());
                } else {
                    logger.error("Error while saving transaction: {}, status: {}",
                            transaction.getTransactionId(),
                            response.getResponse() != null ? response.getResponse().getStatusCode() : "unknown");
                }
            }
        }

        logger.info("Bulk operation complete. Saved: {}, Duplicates: {}, Errors: {}", successfullySaved.size(), duplicateCount, errorCount);
        return successfullySaved;
    }

    public List<TransactionDTO> bulkUpdateTransactions (List<TransactionDTO> transactions) {
        if (transactions.isEmpty()) {
            logger.info("Transaction list is empty");
            return List.of();
        }

        logger.info("Starting bulk update of {} transactions",  transactions.size());

        List<CosmosItemOperation> operations = transactions.stream()
                .map(transaction -> CosmosBulkOperations.getReplaceItemOperation(
                        transaction.getId(),
                        transaction,
                        new PartitionKey(transaction.getDate().toString()),
                        transaction
                )).toList();

        Iterable<CosmosBulkOperationResponse<Object>> responses = transactionsContainer.executeBulkOperations(operations);

        ArrayList<TransactionDTO> successfullySaved = new ArrayList<>();

        int duplicateCount = 0;
        int errorCount = 0;

        for (CosmosBulkOperationResponse<Object> response : responses) {

            TransactionDTO transaction = response.getOperation().getContext();

            if (response.getResponse() != null && response.getResponse().isSuccessStatusCode()) {
                successfullySaved.add(transaction);
            } else if (response.getResponse() != null && response.getResponse().getStatusCode() == 409) {
                duplicateCount++;
            } else {
                errorCount++;
                if (response.getException() != null) {
                    logger.error("Error while saving transaction: {}",  transaction.getTransactionId(), response.getException());
                } else {
                    logger.error("Error while saving transaction: {}, status: {}",
                            transaction.getTransactionId(),
                            response.getResponse() != null ? response.getResponse().getStatusCode() : "unknown");
                }
            }
        }

        logger.info("Bulk operation complete. Saved: {}, Duplicates: {}, Errors: {}", successfullySaved.size(), duplicateCount, errorCount);
        return successfullySaved;
    }

}
