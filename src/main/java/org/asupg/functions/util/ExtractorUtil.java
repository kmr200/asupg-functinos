package org.asupg.functions.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractorUtil {

    public static String extractPatternFromBody(String body, Pattern pattern, String name) {
        Matcher matcher = pattern.matcher(body);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Response body does not contain " + name);
        }
        return matcher.group(1);
    }

    public static String extractStatementButtonUuid(String body, String account) {
        String accountMarker = "label:'" + account + "'";
        int accIndex = body.indexOf(accountMarker);

        if (accIndex == -1) {
            throw new IllegalStateException("Account number not found: " + account);
        }

        int sliceEnd = Math.min(body.length(), accIndex + ConstantsUtil.SAFE_SLICE_LENGTH);
        String slice = body.substring(accIndex, sliceEnd);

        Matcher matcher =
                ConstantsUtil.STATEMENT_BUTTON_PATTERN.matcher(slice);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Выписка за период button not found for account " + account
            );
        }

        return matcher.group(1);
    }

    public static String extractDownloadUrl(
            String responseBody,
            String baseUrl,
            ObjectMapper objectMapper
    ) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode rsNode = root.path("rs");

            if (!rsNode.isArray()) {
                throw new IllegalStateException("Response does not contain 'rs' array");
            }

            for (JsonNode entry : rsNode) {
                if (entry.isArray()
                        && entry.size() >= 2
                        && "download".equals(entry.get(0).asText())
                        && entry.get(1).isArray()
                        && entry.get(1).size() > 0) {

                    String downloadUrl = entry.get(1).get(0).asText();

                    // Build absolute URL if needed
                    if (downloadUrl.startsWith("/")) {
                        downloadUrl = baseUrl + downloadUrl;
                    }

                    return downloadUrl;
                }
            }

            throw new IllegalStateException("Download URL not found in response");

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ZK response", e);
        }
    }

}
