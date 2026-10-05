package com.example.seatroom.service;

import com.example.seatroom.model.UserPreference;
import com.example.seatroom.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferenceService {

    private final UserPreferenceRepository repo;

    public PreferenceService(UserPreferenceRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public UserPreference get(String userId) {
        return repo.findById(userId).orElseGet(() -> new UserPreference(userId));
    }

    @Transactional
    public UserPreference save(String userId, UserPreference incoming) {
        UserPreference pref = repo.findById(userId).orElseGet(() -> new UserPreference(userId));
        if (incoming.getPreferredSeatType() != null) pref.setPreferredSeatType(incoming.getPreferredSeatType());
        if (incoming.getPreferredRoomType() != null) pref.setPreferredRoomType(incoming.getPreferredRoomType());
        return repo.save(pref);
    }
}
