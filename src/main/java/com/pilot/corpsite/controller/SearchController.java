package com.pilot.corpsite.controller;

import com.pilot.corpsite.model.api.SearchDocument;
import com.pilot.corpsite.model.api.request.SearchRequest;
import com.pilot.corpsite.service.GenerateAISummary;
import com.pilot.corpsite.service.GetSearchResult;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SearchController {
    private final GetSearchResult getSearchResult;

    private final GenerateAISummary generateAISummary;

    public SearchController(GetSearchResult getSearchResult, GenerateAISummary generateAISummary) {
        this.getSearchResult = getSearchResult;
        this.generateAISummary = generateAISummary;
    }

    @PostMapping(value = "/search", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> search(@RequestBody SearchRequest request) {
        // Get search results first
        List<SearchDocument> references = this.getSearchResult.search(request.getQuery());

        try {
            return this.generateAISummary.execute(request.getQuery(), references)
                    .map(chunk -> ServerSentEvent.<String>builder()
                            .event("data")
                            .data(chunk)
                            .build());
        } catch (Exception e) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("Error processing request [reason= " + e.getMessage() + "]")
                    .build());
        }
    }
}
