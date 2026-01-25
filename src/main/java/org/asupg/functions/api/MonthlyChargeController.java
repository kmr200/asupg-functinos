package org.asupg.functions.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.service.BalanceService;
import org.asupg.functions.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/monthly-charge")
public class MonthlyChargeController {

    private final BalanceService balanceService;
    private final TransactionService transactionService;

    @PostMapping("/execute")
    public ResponseEntity<Object> execute() {
        List<CompanyDTO> updatedCompanies = balanceService.applyMonthlyCharge();
        transactionService.generateTransactionForMonthlyCharges(updatedCompanies);

        return ResponseEntity.ok().build();
    }

}
