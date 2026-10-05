package com.travel.reviewsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReplyRequest {

    @NotNull
    private Long authorId;

    @NotBlank
    private String text;
}
