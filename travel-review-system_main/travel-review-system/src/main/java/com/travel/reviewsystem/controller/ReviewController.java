package com.travel.reviewsystem.controller;

import com.travel.reviewsystem.dto.*;
import com.travel.reviewsystem.model.Review;
import com.travel.reviewsystem.model.ReviewFlag;
import com.travel.reviewsystem.model.ReviewReply;
import com.travel.reviewsystem.model.TargetType;
import com.travel.reviewsystem.service.ModerationService;
import com.travel.reviewsystem.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ModerationService moderationService;

    /** Create a review with a 1-5 star rating, text, and optional photos. */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        Review review = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.from(review));
    }

    /**
     * List reviews for a hotel or flight, with sorting and paging.
     * Example: GET /api/reviews?targetType=HOTEL&targetId=5&sort=MOST_HELPFUL&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> listReviews(
            @RequestParam TargetType targetType,
            @RequestParam Long targetId,
            @RequestParam(defaultValue = "NEWEST") ReviewService.SortOption sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Review> reviews = reviewService.listReviews(targetType, targetId, sort, page, size);
        return ResponseEntity.ok(reviews.map(ReviewResponse::from));
    }

    /** Mark a review as helpful (used for the "most helpful" sort). */
    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<ReviewResponse> markHelpful(@PathVariable Long reviewId) {
        Review review = reviewService.markHelpful(reviewId);
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    /** Reply to a review to keep the conversation/engagement going. */
    @PostMapping("/{reviewId}/replies")
    public ResponseEntity<ReviewReply> addReply(@PathVariable Long reviewId,
                                                 @Valid @RequestBody ReplyRequest request) {
        ReviewReply reply = reviewService.addReply(reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    /** Flag a review as inappropriate; it goes into the moderator queue. */
    @PostMapping("/{reviewId}/flags")
    public ResponseEntity<ReviewFlag> flagReview(@PathVariable Long reviewId,
                                                  @Valid @RequestBody FlagRequest request) {
        ReviewFlag flag = moderationService.flagReview(reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(flag);
    }
}
