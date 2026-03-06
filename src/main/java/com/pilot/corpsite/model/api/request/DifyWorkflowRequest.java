package com.pilot.corpsite.model.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DifyWorkflowRequest {
    private Inputs inputs;

    @JsonProperty("response_mode")
    private String responseMode;

    private String user;

    public static class DifyWorkflowRequestBuilder {
        public DifyWorkflowRequestBuilder inputs(String query, String reference) {
            this.inputs = new Inputs();
            this.inputs.setQuery(query);
            this.inputs.setReference(reference);
            return this;
        }
    }
}

@Getter
@Setter
class Inputs {
    private String query;
    private String reference;
}