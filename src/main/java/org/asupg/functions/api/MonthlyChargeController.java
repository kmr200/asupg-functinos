package org.asupg.functions.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.ZoneOffset;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/monthly-charge")
public class MonthlyChargeController {

    private final BalanceService balanceService;

    @PostMapping("/execute")
    public ResponseEntity<Object> execute() {
        balanceService.runMonthlyBilling(YearMonth.now(ZoneOffset.UTC));

        return ResponseEntity.ok().build();
    }

}
