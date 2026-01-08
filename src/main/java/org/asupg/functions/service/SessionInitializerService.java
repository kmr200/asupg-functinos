package org.asupg.functions.service;

import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.asupg.functions.model.SessionDTO;
import org.asupg.functions.util.ConstantsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;

import static org.asupg.functions.util.ExtractorUtil.extractPatternFromBody;

@Singleton
public class SessionInitializerService {

    private static final Logger logger = LoggerFactory.getLogger(SessionInitializerService.class);

    private final ExternalApiService externalApiService;
    private final CookieStore cookieStore;

    @Inject
    public SessionInitializerService (
            ExternalApiService externalApiService,
            CookieStore cookieStore
    ) {
        this.externalApiService = externalApiService;
        this.cookieStore = cookieStore;
    }

    public SessionDTO requestSession(String host) {
        String responseBody = externalApiService.performGet(host);

        Cookie sessionCookie = cookieStore.getCookies()
                .stream()
                .filter(cookie -> cookie.getName().equals(ConstantsUtil.JSESSIONID_COOKIE_NAME))
                .filter(cookie -> !cookie.isExpired(Instant.now()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Session cookie not found"));

        String sessionId = sessionCookie.getValue();
        String dtUuid = extractPatternFromBody(responseBody, ConstantsUtil.DT_COMPONENT_PATTERN, "dtUuid");
        String usernameUuid = extractPatternFromBody(responseBody, ConstantsUtil.USERNAME_COMPONENT_PATTERN, "usernameUuid");
        String passwordUuid = extractPatternFromBody(responseBody, ConstantsUtil.PASSWORD_COMPONENT_PATTERN, "passwordUuid");
        String loginBtnUuid = extractPatternFromBody(responseBody, ConstantsUtil.LOGIN_BUTTON_PATTERN, "loginBtnUuid");

        SessionDTO sessionDTO = new SessionDTO(sessionId, dtUuid, usernameUuid, passwordUuid, loginBtnUuid);

        logger.info("Extracted session from response: {}", sessionDTO);

        return sessionDTO;
    }

}
