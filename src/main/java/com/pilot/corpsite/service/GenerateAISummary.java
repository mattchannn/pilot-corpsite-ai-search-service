package com.pilot.corpsite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilot.corpsite.model.api.SearchDocument;
import com.pilot.corpsite.model.api.request.DifyWorkflowRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Log4j2
@Service
public class GenerateAISummary {
    private final WebClient webClient;

    public GenerateAISummary(@Qualifier("difyWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<String> execute(String query, List<SearchDocument> references) {
        try {
            String referencesInJsonStr = new ObjectMapper().writeValueAsString(references);

            // Build the request
            DifyWorkflowRequest request = DifyWorkflowRequest.builder()
                    .inputs(referencesInJsonStr)
                    .query(query)
                    .responseMode("streaming")
                    .user("fake-user-123")
                    .build();

            // Execute the workflow
            return webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .filter(line -> line.contains("\"event\":\"message\""));
        } catch (Exception e) {
            log.error("Error executing Dify workflow", e);
            return Flux.error(e);
        }
    }
}


