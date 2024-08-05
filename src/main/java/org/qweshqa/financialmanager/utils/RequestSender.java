package org.qweshqa.financialmanager.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class RequestSender {

    private final String accessToken;

    @Autowired
    public RequestSender(@Value("${currency.api.access.token}") String accessToken) {
        this.accessToken = accessToken;
    }

    public HttpResponse<String> sendCurrencyConvertRequest(String from, String to) {
        String requestUrl = "https://api.freecurrencyapi.com/v1/latest";
        String requestBody = "apikey=" + accessToken +
                "&base_currency=" + from +
                "&currencies=" + to;

        HttpResponse<String> response = null;
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl + "?" + requestBody))
                    .GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e){
            return response;
        }

        return response;
    }
}
