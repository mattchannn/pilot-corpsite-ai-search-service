package com.pilot.corpsite.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DifyWorkflowResponse {
    @JsonProperty("workflow_run_id")
    private String workflowRunId;

    @JsonProperty("task_id")
    private String taskId;

    private Data data;

    private String status;

    private String error;

    public String getSummary() {
        return this.data.getOutputs().getSummary();
    }
}

@Getter
@Setter
class Data {
    private Outputs outputs;
}

@Getter
@Setter
class Outputs {
    private String summary;
}