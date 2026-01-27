package org.asupg.functions.repository;

import org.asupg.functions.model.CompanyDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyDTO, String> {

    List<CompanyDTO> findByInnIn(Set<String> inns);

}
