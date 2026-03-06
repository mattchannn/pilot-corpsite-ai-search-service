package com.pilot.corpsite.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDocument {
    private String id;

    @JsonProperty("parent_id")
    private String parentId;

    private String chunk;

    private String language;
}