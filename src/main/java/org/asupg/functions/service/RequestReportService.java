package org.asupg.functions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.config.BankClientConfig;
import org.asupg.functions.model.AuthDTO;
import org.asupg.functions.model.SessionDTO;
import org.asupg.functions.util.ConstantsUtil;
import org.asupg.functions.util.ExtractorUtil;
import org.asupg.functions.util.FormBodyUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.asupg.functions.util.ExtractorUtil.extractPatternFromBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestReportService {

    private final ExternalApiService externalApiService;
    private final BankClientConfig bankClientConfig;
    private final ObjectMapper objectMapper;

    public String requestReport(SessionDTO session, AuthDTO auth) {

        //Open the report window
        log.info("Sending 'open report windows' request");
        Map<String, String> reportFormBody = FormBodyUtil.openReportFormBody(session.getDtUuid(), auth.getStatementBtnUuid());

        String responseBody = externalApiService.performPost(
                bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT,
                reportFormBody
        );

        String reportWindowUuid = extractPatternFromBody(responseBody, ConstantsUtil.REPORT_WINDOW_PATTERN, "report window uuid");
        String startDateUuid = extractPatternFromBody(responseBody, ConstantsUtil.START_DATE_PATTERN, "start date uuid");
        String endDateUuid = extractPatternFromBody(responseBody, ConstantsUtil.END_DATE_PATTERN, "end date uuid");
        String excelBtnUuid = extractPatternFromBody(responseBody, ConstantsUtil.EXCEL_BUTTON_PATTERN, "excel button uuid");

        //Set from date
        log.info("Sending 'set from date' request");
        Map<String, String> setFromFormBody = FormBodyUtil.setFromFormBody(session.getDtUuid(), reportWindowUuid, startDateUuid);

        externalApiService.performPost(bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT, setFromFormBody);

        //Set to date
        log.info("Sending 'set end date' request");
        Map<String, String> setToFormBody = FormBodyUtil.setToFormBody(session.getDtUuid(), endDateUuid);

        externalApiService.performPost(bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT, setToFormBody);

        //Request report
        log.info("Sending 'get report download url' request");
        String reportResponseBody = externalApiService.performPost(
                bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT,
                FormBodyUtil.requestReportFormBody(session.getDtUuid(), reportWindowUuid, excelBtnUuid));

        return ExtractorUtil.extractDownloadUrl(reportResponseBody, bankClientConfig.getHost(), objectMapper);
    }

}
