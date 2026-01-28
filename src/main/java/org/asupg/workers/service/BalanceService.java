package org.asupg.workers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.workers.model.*;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final TransactionService transactionService;
    private final CompanyService companyService;
    private final DeviceService deviceService;

    public List<TransactionDTO> processTransactions(List<TransactionDTO> transactions) {

        Map<String, List<TransactionDTO>> byInn =
                transactions.stream()
                        .filter(t -> t.getCounterpartyInn() != null && !t.getCounterpartyInn().isBlank())
                        .collect(Collectors.groupingBy(TransactionDTO::getCounterpartyInn));

        List<TransactionDTO> result = new ArrayList<>();

        for (Map.Entry<String, List<TransactionDTO>> entry : byInn.entrySet()) {
            result.addAll(
                    companyService.applyTransactionsToCompany(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return result;
    }

    public void runMonthlyBilling(YearMonth billingMonth) {

        Map<String, List<DeviceDTO>> billableDevices =
                deviceService.getBillableDevicesAggregatedByCompanyInn(billingMonth);

        for (Map.Entry<String, List<DeviceDTO>> entry : billableDevices.entrySet()) {
            companyService.applyMonthlyCharge(
                    entry.getKey(),
                    entry.getValue(),
                    billingMonth
            );
        }
    }
}
