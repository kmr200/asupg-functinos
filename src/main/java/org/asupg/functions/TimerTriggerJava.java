package org.asupg.functions;

import java.time.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.asupg.functions.service.RequestOrchestratorService;

import javax.inject.Inject;

/**
 * Azure Functions with Timer trigger.
 */
public class TimerTriggerJava {

    private RequestOrchestratorService requestOrchestratorService;

    @Inject
    public TimerTriggerJava(RequestOrchestratorService requestOrchestratorService) {
        this.requestOrchestratorService = requestOrchestratorService;
    }

    /**
     * This function will be invoked periodically according to the specified schedule.
     */
    @FunctionName("TimerTriggerJava")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 */30 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("Java Timer trigger function executed at: " + LocalDateTime.now());

        requestOrchestratorService.requestReport();

    }
}
