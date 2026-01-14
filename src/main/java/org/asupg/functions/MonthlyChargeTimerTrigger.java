package org.asupg.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.service.BalanceService;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Azure Functions with Timer trigger.
 */
public class MonthlyChargeTimerTrigger {

    private final BalanceService balanceService;

    @Inject
    public MonthlyChargeTimerTrigger(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * This function will be invoked periodically according to the specified schedule.
     */
    @FunctionName("MonthlyChargeTimerTrigger")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 0 1 1 * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("Report Timer trigger function executed at: " + LocalDateTime.now());

        List<CompanyDTO> updatedCompanies = balanceService.applyMonthlyCharge();

        balanceService.generateTransactionForMonthlyCharges(updatedCompanies);
    }
}
