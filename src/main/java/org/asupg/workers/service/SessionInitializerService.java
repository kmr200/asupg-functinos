package org.asupg.workers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.asupg.workers.model.SessionDTO;
import org.asupg.workers.util.ConstantsUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static org.asupg.workers.util.ExtractorUtil.extractPatternFromBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionInitializerService {

    private final ExternalApiService externalApiService;
    private final CookieStore cookieStore;

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

        log.info("Extracted session from response: {}", sessionDTO);

        return sessionDTO;
    }

}
