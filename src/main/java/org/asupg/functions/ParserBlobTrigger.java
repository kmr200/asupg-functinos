package org.asupg.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import org.asupg.functions.model.TransactionDTO;
import org.asupg.functions.service.BalanceService;
import org.asupg.functions.service.ExcelParserService;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Azure Functions with Azure Blob trigger.
 */
public class ParserBlobTrigger {

    private final ExcelParserService excelParserService;
    private final BalanceService balanceService;

    @Inject
    public ParserBlobTrigger(ExcelParserService excelParserService, BalanceService balanceService) {
        this.excelParserService = excelParserService;
        this.balanceService = balanceService;
    }

    /**
     * This function will be invoked when a new or updated blob is detected at the specified path. The blob contents are provided as input to this function.
     */
    @FunctionName("ParserBlobTrigger")
    public void run(
            @BlobTrigger(
                    name = "content",
                    path = "reports/{name}",
                    dataType = "binary",
                    connection = "AzureWebJobsStorage"
            ) byte[] content,
            @BindingName("name") String name,
            final ExecutionContext context
    ) {
        context.getLogger().info("Parsing file: " + name);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {

            //Parse transactions
            List<TransactionDTO> transactions = excelParserService.parse(inputStream);
            context.getLogger().info("Successfully parsed file: " + name);

            //Update balance
            balanceService.bulkUpdateBalance(transactions);

        } catch (Exception e) {
            context.getLogger().severe("Error parsing file: " + e.getMessage());
            throw new RuntimeException(e);
        };
    }
}
