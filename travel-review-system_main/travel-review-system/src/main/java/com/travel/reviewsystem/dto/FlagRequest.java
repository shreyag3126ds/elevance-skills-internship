package com.travel.reviewsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlagRequest {

    @NotNull
    private Long reporterId;

    /** short reason, e.g. "spam", "offensive", "fake review" */
    private String reason;
}
