package com.travel.reviewsystem.dto;

import com.travel.reviewsystem.model.Review;
import com.travel.reviewsystem.model.TargetType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponse {
    private Long id;
    private TargetType targetType;
    private Long targetId;
    private String authorName;
    private int rating;
    private String text;
    private List<String> photoUrls;
    private int helpfulVotes;
    private int replyCount;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review r) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(r.getId());
        dto.setTargetType(r.getTargetType());
        dto.setTargetId(r.getTargetId());
        dto.setAuthorName(r.getAuthor().getDisplayName());
        dto.setRating(r.getRating());
        dto.setText(r.getText());
        dto.setPhotoUrls(r.getPhotoUrls());
        dto.setHelpfulVotes(r.getHelpfulVotes());
        dto.setReplyCount(r.getReplies().size());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
