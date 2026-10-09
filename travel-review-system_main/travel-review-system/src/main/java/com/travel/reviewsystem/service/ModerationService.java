package com.travel.reviewsystem.service;

import com.travel.reviewsystem.dto.FlagRequest;
import com.travel.reviewsystem.dto.ModerationDecisionRequest;
import com.travel.reviewsystem.exception.ResourceNotFoundException;
import com.travel.reviewsystem.model.FlagStatus;
import com.travel.reviewsystem.model.Review;
import com.travel.reviewsystem.model.ReviewFlag;
import com.travel.reviewsystem.model.User;
import com.travel.reviewsystem.repository.ReviewFlagRepository;
import com.travel.reviewsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final ReviewFlagRepository flagRepository;
    private final UserRepository userRepository;
    private final ReviewService reviewService;

    @Transactional
    public ReviewFlag flagReview(Long reviewId, FlagRequest request) {
        Review review = reviewService.getReviewOrThrow(reviewId);
        User reporter = userRepository.findById(request.getReporterId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getReporterId()));

        ReviewFlag flag = new ReviewFlag();
        flag.setReview(review);
        flag.setReporter(reporter);
        flag.setReason(request.getReason());
        flag.setStatus(FlagStatus.PENDING);
        return flagRepository.save(flag);
    }

    /** The moderation queue: every flag still waiting for a decision. */
    public List<ReviewFlag> getPendingFlags() {
        return flagRepository.findByStatus(FlagStatus.PENDING);
    }

    @Transactional
    public ReviewFlag resolveFlag(Long flagId, ModerationDecisionRequest request) {
        ReviewFlag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new ResourceNotFoundException("Flag not found: " + flagId));

        User moderator = userRepository.findById(request.getModeratorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getModeratorId()));
        if (!moderator.isModerator()) {
            throw new IllegalStateException("User " + moderator.getId() + " is not a moderator");
        }

        flag.setResolvedBy(moderator);
        flag.setModeratorNote(request.getNote());

        if (Boolean.TRUE.equals(request.getRemove())) {
            flag.setStatus(FlagStatus.REMOVED);
            flag.getReview().setRemoved(true);
        } else {
            flag.setStatus(FlagStatus.DISMISSED);
        }
        return flagRepository.save(flag);
    }
}
