package com.example.seatroom.controller;

import com.example.seatroom.model.UserPreference;
import com.example.seatroom.service.PreferenceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/preferences")
public class PreferenceController {

    private final PreferenceService service;

    public PreferenceController(PreferenceService service) {
        this.service = service;
    }

    @GetMapping
    public UserPreference get(@PathVariable String userId) {
        return service.get(userId);
    }

    @PutMapping
    public UserPreference save(@PathVariable String userId, @RequestBody UserPreference body) {
        return service.save(userId, body);
    }
}
