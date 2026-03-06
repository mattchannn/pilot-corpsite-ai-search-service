package com.pilot.corpsite.service;

import com.pilot.corpsite.model.api.request.DifyWorkflowRequest;
import com.pilot.corpsite.model.api.response.DifyWorkflowResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Log4j2
@Service
public class GenerateAISummary {

    private static final String DIFY_API_URL = "https://api.dify.ai/v1/workflows/run";
    private static final String DIFY_API_KEY = "Bearer ";

    private final RestClient restClient;

    public GenerateAISummary() {
        this.restClient = RestClient.builder()
                .baseUrl(DIFY_API_URL)
                .defaultHeader("Authorization", DIFY_API_KEY)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String execute(String query, List<String> chunks) {
        try {
            // Join chunks into a single reference string
            String reference = String.join("\n", chunks);

            // Build the request
            DifyWorkflowRequest request = DifyWorkflowRequest.builder()
                    .inputs(query, reference)
                    .responseMode("blocking")
                    .user("fake-user-123")
                    .build();

            // Execute the workflow
            DifyWorkflowResponse response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(DifyWorkflowResponse.class);

            if (response != null && response.getData() != null) {
                return response.getSummary();
            } else {
                log.warn("No data received from Dify workflow");
                return "";
            }

        } catch (Exception e) {
            log.error("Error executing Dify workflow", e);
            return "";
        }
    }
}
