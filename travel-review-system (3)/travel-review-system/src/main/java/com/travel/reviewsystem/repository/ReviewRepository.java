package com.travel.reviewsystem.repository;

import com.travel.reviewsystem.model.Review;
import com.travel.reviewsystem.model.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // removed=false so moderator-removed reviews never show up to normal users;
    // Pageable carries the sort ("newest" / "highest rated" / "most helpful")
    Page<Review> findByTargetTypeAndTargetIdAndRemovedFalse(
            TargetType targetType, Long targetId, Pageable pageable);
}
