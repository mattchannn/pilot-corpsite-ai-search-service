package com.pilot.corpsite.controller;

import com.pilot.corpsite.model.api.SearchDocument;
import com.pilot.corpsite.model.api.request.SearchRequest;
import com.pilot.corpsite.model.api.response.SearchResult;
import com.pilot.corpsite.service.GenerateAISummary;
import com.pilot.corpsite.service.GetSearchResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    private final GetSearchResult getSearchResult;

    private final GenerateAISummary generateAISummary;

    public SearchController(GetSearchResult getSearchResult, GenerateAISummary generateAISummary) {
        this.getSearchResult = getSearchResult;
        this.generateAISummary = generateAISummary;
    }

    @PostMapping(value = "/search", produces = "application/json")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request) {
        SearchResult resp = new SearchResult();
        List<SearchDocument> references = this.getSearchResult.search(request.getQuery());
        String summary = this.generateAISummary.execute(request.getQuery(), references);
        resp.setSummary(summary);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
