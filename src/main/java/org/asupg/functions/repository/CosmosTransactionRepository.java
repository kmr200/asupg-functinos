package org.asupg.functions.repository;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.asupg.functions.model.CompanyDto;
import org.asupg.functions.model.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                    ObjectNode document = buildDocument(transaction, true);
                    return CosmosBulkOperations.getCreateItemOperation(
                            document,
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
                .map(transaction -> {
                    ObjectNode document = buildDocument(transaction, false);
                    return CosmosBulkOperations.getReplaceItemOperation(
                            transaction.getTransactionId(),
                            document,
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

    private ObjectNode buildDocument(TransactionDTO transaction, boolean isCreateOperation) {
        ObjectNode document = objectMapper.createObjectNode();

        document.put("id", transaction.getTransactionId());

        // Partition key
        document.put("date", transaction.getDate().toString());

        // Keep transactionId for clarity/querying
        document.put("transactionId", transaction.getTransactionId());

        // Other fields
        if (transaction.getCounterpartyName() != null) {
            document.put("counterpartyName", transaction.getCounterpartyName());
        }
        if (transaction.getCounterpartyInn() != null) {
            document.put("counterpartyInn", transaction.getCounterpartyInn());
        }
        if (transaction.getAccountNumber() != null) {
            document.put("accountNumber", transaction.getAccountNumber());
        }
        if (transaction.getMfo() != null) {
            document.put("mfo", transaction.getMfo());
        }
        if (transaction.getAmount() != null) {
            document.put("amount", transaction.getAmount());
        }
        if (transaction.getDescription() != null) {
            document.put("description", transaction.getDescription());
        }

        if (transaction.getReconciliation() != null) {
            document.set("reconciliation", objectMapper.valueToTree(transaction.getReconciliation()));
        }

        // Metadata
        if (isCreateOperation) {
            document.put("createdAt", System.currentTimeMillis());
        }

        return document;
    }

}
