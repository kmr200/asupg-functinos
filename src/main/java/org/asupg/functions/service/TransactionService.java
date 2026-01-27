package org.asupg.functions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.TransactionDTO;
import org.asupg.functions.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> saveTransactions(List<TransactionDTO> transactions) {

        if (transactions.isEmpty()) {
            log.info("Transaction list is empty");
            return List.of();
        }

        log.info("Starting bulk save of {} transactions", transactions.size());

        return transactionRepository.saveAll(transactions);
    }
}
