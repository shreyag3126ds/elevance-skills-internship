package com.travel.reviewsystem.repository;

import com.travel.reviewsystem.model.FlagStatus;
import com.travel.reviewsystem.model.ReviewFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewFlagRepository extends JpaRepository<ReviewFlag, Long> {

    List<ReviewFlag> findByStatus(FlagStatus status);
}
