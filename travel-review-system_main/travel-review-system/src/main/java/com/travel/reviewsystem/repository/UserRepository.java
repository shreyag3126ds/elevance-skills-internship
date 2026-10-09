package com.travel.reviewsystem.repository;

import com.travel.reviewsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
