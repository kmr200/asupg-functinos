package org.asupg.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.asupg.functions.service.RequestOrchestratorService;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerJava {

    private RequestOrchestratorService requestOrchestratorService;

    @Inject
    public HttpTriggerJava(RequestOrchestratorService requestOrchestratorService) {
        this.requestOrchestratorService = requestOrchestratorService;
    }

    /**
     * This function listens at endpoint "/api/HttpTriggerJava". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTriggerJava
     * 2. curl {your host}/api/HttpTriggerJava?name=HTTP%20Query
     */
    @FunctionName("HttpTriggerJava")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    ) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        requestOrchestratorService.requestReport();

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .build();
    }
}
