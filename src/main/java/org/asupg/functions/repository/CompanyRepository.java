package org.asupg.functions.repository;

import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.model.CompanyStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyDTO, String> {

    List<CompanyDTO> findByInnIn(Set<String> inns);

    List<CompanyDTO> findByStatusAndBillingStartMonthLessThanEqual(
            CompanyStatus status,
            YearMonth billingStartMonth
    );

}
