package org.asupg.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.service.BalanceService;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Azure Functions with Timer trigger.
 */
public class MonthlyChargeHttpTrigger {

    private final BalanceService balanceService;

    @Inject
    public MonthlyChargeHttpTrigger(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * This function will be invoked periodically according to the specified schedule.
     */
    @FunctionName("MonthlyChargeHttpTrigger")
    public void run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    ) {
        context.getLogger().info("Report Http trigger function executed at: " + LocalDateTime.now());

        List<CompanyDTO> updatedCompanies = balanceService.applyMonthlyCharge();

        balanceService.generateTransactionForMonthlyCharges(updatedCompanies);
    }
}
