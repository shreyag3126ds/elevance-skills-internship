package com.travel.reviewsystem.service;

import com.travel.reviewsystem.dto.ReplyRequest;
import com.travel.reviewsystem.dto.ReviewRequest;
import com.travel.reviewsystem.exception.ResourceNotFoundException;
import com.travel.reviewsystem.model.*;
import com.travel.reviewsystem.repository.ReviewReplyRepository;
import com.travel.reviewsystem.repository.ReviewRepository;
import com.travel.reviewsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository replyRepository;
    private final UserRepository userRepository;

    /** How the review list can be sorted, driven by a "sort" query param. */
    public enum SortOption {
        NEWEST, HIGHEST_RATED, MOST_HELPFUL
    }

    @Transactional
    public Review createReview(ReviewRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getAuthorId()));

        Review review = new Review();
        review.setTargetType(request.getTargetType());
        review.setTargetId(request.getTargetId());
        review.setAuthor(author);
        review.setRating(request.getRating());
        review.setText(request.getText());
        if (request.getPhotoUrls() != null) {
            review.setPhotoUrls(new ArrayList<>(request.getPhotoUrls()));
        }
        return reviewRepository.save(review);
    }

    /**
     * List reviews for a hotel/flight with sorting and paging.
     * Filtering by "not removed" happens here so moderator-removed reviews
     * never reach normal users.
     */
    public Page<Review> listReviews(TargetType targetType, Long targetId, SortOption sort, int page, int size) {
        Sort sorting = switch (sort) {
            case HIGHEST_RATED -> Sort.by(Sort.Direction.DESC, "rating").and(Sort.by(Sort.Direction.DESC, "createdAt"));
            case MOST_HELPFUL -> Sort.by(Sort.Direction.DESC, "helpfulVotes").and(Sort.by(Sort.Direction.DESC, "createdAt"));
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(page, size, sorting);
        return reviewRepository.findByTargetTypeAndTargetIdAndRemovedFalse(targetType, targetId, pageable);
    }

    @Transactional
    public Review markHelpful(Long reviewId) {
        Review review = getReviewOrThrow(reviewId);
        review.setHelpfulVotes(review.getHelpfulVotes() + 1);
        return reviewRepository.save(review);
    }

    @Transactional
    public ReviewReply addReply(Long reviewId, ReplyRequest request) {
        Review review = getReviewOrThrow(reviewId);
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getAuthorId()));

        ReviewReply reply = new ReviewReply();
        reply.setReview(review);
        reply.setAuthor(author);
        reply.setText(request.getText());
        return replyRepository.save(reply);
    }

    public Review getReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
    }
}
