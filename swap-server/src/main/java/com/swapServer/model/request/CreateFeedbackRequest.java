package com.swapServer.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateFeedbackRequest {
    @NotNull
    private String title;

    private String description;
}
