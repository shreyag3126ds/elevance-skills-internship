package com.travel.reviewsystem.dto;

import com.travel.reviewsystem.model.TargetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReviewRequest {

    @NotNull
    private TargetType targetType;

    @NotNull
    private Long targetId;

    @NotNull
    private Long authorId;

    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    private String text;

    /** optional photo URLs (already uploaded to storage) attached to the review */
    private List<String> photoUrls;
}
