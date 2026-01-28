package org.asupg.workers.repository;

import org.asupg.workers.model.TransactionDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionDTO, String> {
}
