package com.boardgo.common.config;

import com.boardgo.common.exception.ExternalException;
import com.boardgo.common.exception.advice.dto.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@AllArgsConstructor
public class RestClientConfig {
    private final RestClient restClient;

    @Autowired
    public RestClientConfig(RestClient.Builder builder) {
        this.restClient =
                builder.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .defaultHeader(
                                HttpHeaders.CONTENT_TYPE,
                                String.valueOf(MediaType.APPLICATION_FORM_URLENCODED))
                        .defaultStatusHandler(
                                HttpStatusCode::is5xxServerError,
                                ((request, response) -> {
                                    throw new ExternalException(
                                            request.getURI().toString(),
                                            ErrorCode.INTERNAL_SERVER_ERROR);
                                }))
                        .build();
    }
}
