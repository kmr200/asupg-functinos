package org.asupg.functions.service;

import org.asupg.functions.config.BankClientConfig;
import org.asupg.functions.model.AuthDTO;
import org.asupg.functions.model.SessionDTO;
import org.asupg.functions.util.ConstantsUtil;
import org.asupg.functions.util.ExtractorUtil;
import org.asupg.functions.util.FormBodyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class AuthenticatorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatorService.class);

    private final BankClientConfig bankClientConfig;
    private final ExternalApiService externalApiService;

    @Inject
    public AuthenticatorService(BankClientConfig bankClientConfig, ExternalApiService externalApiService) {
        this.bankClientConfig = bankClientConfig;
        this.externalApiService = externalApiService;
    }

    public AuthDTO authenticate(SessionDTO session) {

        String host = bankClientConfig.getHost() + ConstantsUtil.AUTH_ENDPOINT + session.getJsessionId();

        Map<String, String> formBody = FormBodyUtil.authFormBody(session, bankClientConfig);

        String responseBody = externalApiService.performPost(host, formBody);

        String statementButtonUuid = ExtractorUtil.extractStatementButtonUuid(responseBody, bankClientConfig.getAccount());

        AuthDTO authDTO = new AuthDTO(statementButtonUuid);

        logger.info("Authenticated to the service: {}", authDTO.toString());

        return authDTO;

    }

}
