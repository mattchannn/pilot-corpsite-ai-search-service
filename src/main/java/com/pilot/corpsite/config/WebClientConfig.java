package com.pilot.corpsite.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @Qualifier("difyWebClient")
    public WebClient difyWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.dify.ai/v1/workflows/run")
                .defaultHeader("Authorization", "Bearer ")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
