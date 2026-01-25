package org.asupg.functions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final CloseableHttpClient httpClient;

    public String performGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        addBaseHeaders(httpGet);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            log.debug("Received response body: {}", responseBody);
            return responseBody;
        } catch (Exception e) {
            throw new RuntimeException("HTTP request failed", e);
        }
    }

    public String performPost(String url, Map<String, String> formBody) {
        HttpPost httpPost = new HttpPost(url);
        addBaseHeaders(httpPost);

        List<NameValuePair> params = new ArrayList<>(formBody.size());
        for (Map.Entry<String, String> entry : formBody.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            log.debug("Received response body: {}", responseBody);
            return responseBody;
        } catch (Exception e) {
            throw new RuntimeException("HTTP request failed", e);
        }
    }

    private void addBaseHeaders(HttpUriRequestBase httpUriRequestBase) {
        httpUriRequestBase.addHeader("Accept", "*/*");
        httpUriRequestBase.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpUriRequestBase.addHeader("Connection", "keep-alive");
    }

}
