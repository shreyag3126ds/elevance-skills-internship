package com.travel.reviewsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModerationDecisionRequest {

    @NotNull
    private Long moderatorId;

    /** true = remove the review, false = dismiss the flag and keep the review */
    @NotNull
    private Boolean remove;

    private String note;
}
