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

    @JsonProperty("external_link")
    private String externalLink;

    private String chunk;

    private String language;
}