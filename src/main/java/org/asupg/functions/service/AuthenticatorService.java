package org.asupg.functions.service;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatorService {

    private final BankClientConfig bankClientConfig;
    private final ExternalApiService externalApiService;

    public AuthDTO authenticate(SessionDTO session) {

        String host = bankClientConfig.getHost() + ConstantsUtil.AUTH_ENDPOINT + session.getJsessionId();

        Map<String, String> formBody = FormBodyUtil.authFormBody(session, bankClientConfig);

        String responseBody = externalApiService.performPost(host, formBody);

        String statementButtonUuid = ExtractorUtil.extractStatementButtonUuid(responseBody, bankClientConfig.getAccount());

        AuthDTO authDTO = new AuthDTO(statementButtonUuid);

        log.info("Authenticated to the service: {}", authDTO.toString());

        return authDTO;

    }

}
