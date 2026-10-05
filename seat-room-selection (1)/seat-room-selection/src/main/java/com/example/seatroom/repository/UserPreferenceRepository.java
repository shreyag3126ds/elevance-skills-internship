package com.example.seatroom.repository;

import com.example.seatroom.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, String> {
}
