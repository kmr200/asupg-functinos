package org.asupg.functions.repository;

import org.asupg.functions.model.TransactionDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionDTO, String> {
}
