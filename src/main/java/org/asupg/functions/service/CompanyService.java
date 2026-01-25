package org.asupg.functions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.model.CompanyStatus;
import org.asupg.functions.repository.CompanyRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyDTO> getAllIn(Set<String> companyInn) {
        return companyRepository.findByInnIn(companyInn);
    }

    public List<CompanyDTO> getCompaniesToUpdate() {
        YearMonth currentMonth = YearMonth.now(ZoneOffset.UTC);

        List<CompanyDTO> companies = companyRepository.findByStatusAndBillingStartMonthLessThanEqual(
                CompanyStatus.ACTIVE,
                currentMonth
        );

        List<CompanyDTO> companiesToUpdate = new ArrayList<>();

        for (CompanyDTO company : companies) {
            BigDecimal currentBalance = company.getCurrentBalance();
            BigDecimal monthlyCharge = company.getMonthlyRate();

            if (currentBalance == null || monthlyCharge == null) {
                log.error("Invalid billing data for company {}", company.getInn());
                continue;
            }

            if (company.getLastBilledMonth() != null &&
                    currentMonth.equals(company.getLastBilledMonth())
            ) {
                log.info("Company {} has already been charged for {}", company.getInn(), company.getLastBilledMonth());
                continue;
            }

            company.setCurrentBalance(
                    currentBalance.subtract(monthlyCharge)
            );
            company.setLastBilledMonth(currentMonth);
            company.setBalanceUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            companiesToUpdate.add(company);
        }

        return companiesToUpdate;
    }

    public List<CompanyDTO> bulkUpdateCompanies(List<CompanyDTO> companiesToUpdate) {

        if (companiesToUpdate.isEmpty()) {
            log.info("Company list is empty");
            return List.of();
        }

        List<CompanyDTO> failed = new ArrayList<>();

        for (CompanyDTO company : companiesToUpdate) {
            try {
                companyRepository.save(company);
            } catch (OptimisticLockingFailureException e) {
                log.warn("Optimistic lock failed for company {}", company.getInn());
                failed.add(company);
            } catch (Exception e) {
                log.error("Error while updating company {}", company.getInn(), e);
                failed.add(company);
            }
        }

        return failed;
    }

}
