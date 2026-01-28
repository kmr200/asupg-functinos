package org.asupg.workers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.workers.config.BankClientConfig;
import org.asupg.workers.model.AuthDTO;
import org.asupg.workers.model.SessionDTO;
import org.asupg.workers.model.TransactionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestOrchestratorService {

    private final BankClientConfig bankClientConfig;
    private final SessionInitializerService sessionInitializerService;
    private final AuthenticatorService authenticatorService;
    private final RequestReportService requestReportService;
    private final FileDownloadService fileDownloadService;
    private final ExcelParserService excelParserService;
    private final BalanceService balanceService;

    public void requestReport() {

        SessionDTO session = sessionInitializerService.requestSession(bankClientConfig.getHost());

        AuthDTO authDTO = authenticatorService.authenticate(session);

        String downloadUrl = requestReportService.requestReport(session, authDTO);
        log.info("Extracted download url: {}", downloadUrl);

        try {
            var download =
                    fileDownloadService.download(downloadUrl);

            List<TransactionDTO> transactions = excelParserService.parse(download);
            log.info("Successfully parsed file: {}", downloadUrl);

            balanceService.processTransactions(transactions);

        } catch (Exception e) {
            log.error("Failed to parse file: {}", downloadUrl);
            throw new RuntimeException("Failed to download/upload report", e);
        }
    }

}
