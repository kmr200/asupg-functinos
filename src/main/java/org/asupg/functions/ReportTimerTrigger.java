package org.asupg.functions;

import java.time.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.asupg.functions.service.RequestOrchestratorService;

import javax.inject.Inject;

/**
 * Azure Functions with Timer trigger.
 */
public class ReportTimerTrigger {

    private RequestOrchestratorService requestOrchestratorService;

    @Inject
    public ReportTimerTrigger(RequestOrchestratorService requestOrchestratorService) {
        this.requestOrchestratorService = requestOrchestratorService;
    }

    /**
     * This function will be invoked periodically according to the specified schedule.
     */
    @FunctionName("ReportTimerTrigger")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("Report Timer trigger function executed at: " + LocalDateTime.now());

        requestOrchestratorService.requestReport();

    }
}
