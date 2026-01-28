package org.asupg.workers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.workers.model.TransactionDTO;
import org.asupg.workers.repository.TransactionRepository;
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
