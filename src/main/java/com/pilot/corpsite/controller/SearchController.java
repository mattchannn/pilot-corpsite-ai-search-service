package com.pilot.corpsite.controller;

import com.pilot.corpsite.model.api.response.SearchResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @PostMapping(value = "/search", produces = "application/json")
    public ResponseEntity<SearchResult> search() {
        SearchResult resp = new SearchResult();
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
