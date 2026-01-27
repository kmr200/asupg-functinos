package org.asupg.functions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.*;
import org.asupg.functions.repository.CompanyRepository;
import org.asupg.functions.repository.DeviceRepository;
import org.asupg.functions.repository.TransactionRepository;
import org.asupg.functions.util.ParserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final TransactionRepository transactionRepository;
    private final DeviceRepository deviceRepository;

    @Transactional
    public void applyMonthlyCharge(
            String companyInn,
            List<DeviceDTO> devices,
            YearMonth billingMonth
    ) {
        BigDecimal totalCharge =
                devices.stream()
                        .map(DeviceDTO::getMonthlyRate)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        CompanyDTO company = companyRepository.findById(companyInn)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        BigDecimal currentBalance = Optional
                .ofNullable(company.getCurrentBalance())
                .orElse(BigDecimal.ZERO);

        company.setCurrentBalance(currentBalance.subtract(totalCharge));

        company.setBalanceUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        companyRepository.save(company);

        devices.forEach(d -> d.setLastBilledMonth(billingMonth));
        deviceRepository.saveAll(devices);

        transactionRepository.saveAll(
                devices.stream()
                        .map(d -> TransactionDTO.monthlyDeviceCharge(
                                ParserUtil.buildTransactionHash(
                                        TransactionDTO.TransactionType.MONTHLY_CHARGE,
                                        billingMonth,
                                        companyInn,
                                        d.getDeviceId(),
                                        d.getMonthlyRate()
                                ),
                                companyInn,
                                d.getDeviceId(),
                                d.getMonthlyRate(),
                                billingMonth
                        ))
                        .toList()
        );
    }

    @Transactional
    public List<TransactionDTO> applyTransactionsToCompany(String companyInn, List<TransactionDTO> transactions) {
        CompanyDTO company = companyRepository.findById(companyInn)
                .orElseThrow(() -> new RuntimeException("Company with INN: " + companyInn + " not found"));

        BigDecimal balance = company.getCurrentBalance() == null
                ? BigDecimal.ZERO
                : company.getCurrentBalance();

        for (TransactionDTO transaction : transactions) {
            if (transaction.getAmount() != null) {
                balance = balance.add(transaction.getAmount());
            }
            transaction.setReconciliation(new ReconciliationDTO(ReconciliationStatus.MATCHED));
        }

        company.setCurrentBalance(balance);
        company.setBalanceUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        companyRepository.save(company);
        transactionRepository.saveAll(transactions);

        return transactions;
    }
}
