package com.pilot.corpsite.controller;

import com.pilot.corpsite.model.api.SearchDocument;
import com.pilot.corpsite.model.api.request.SearchRequest;
import com.pilot.corpsite.service.GenerateAISummary;
import com.pilot.corpsite.service.GetChunks;
import com.pilot.corpsite.service.GetSearchResults;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class SearchController {
    private final GetChunks getChunks;

    private final GetSearchResults getSearchResults;

    private final GenerateAISummary generateAISummary;

    public SearchController(GetChunks getChunks,
                            GetSearchResults getSearchResults,
                            GenerateAISummary generateAISummary) {
        this.getChunks = getChunks;
        this.getSearchResults = getSearchResults;
        this.generateAISummary = generateAISummary;
    }

    @PostMapping(value = "/ai-summary", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> aiSummary(@RequestBody SearchRequest request) {
        // Get chunks first
        List<SearchDocument> references = this.getChunks.search(request.getQuery());

        try {
            return this.generateAISummary.execute(request.getQuery(), references)
                    .map(chunk -> {
                        log.info("Emitting chunk: {}", chunk);
                        return ServerSentEvent.<String>builder()
                                .event("data")
                                .data(chunk)
                                .build();
                    });
        } catch (Exception e) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("Error processing request [reason= " + e.getMessage() + "]")
                    .build());
        }
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchDocument> search(@RequestBody SearchRequest request) {
        return this.getSearchResults.search(request.getQuery());
    }
}
