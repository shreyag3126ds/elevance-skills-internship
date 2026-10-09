package com.travel.reviewsystem.controller;

import com.travel.reviewsystem.dto.ModerationDecisionRequest;
import com.travel.reviewsystem.model.ReviewFlag;
import com.travel.reviewsystem.service.ModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    /** All reviews currently flagged and waiting on a moderator decision. */
    @GetMapping("/flags/pending")
    public List<ReviewFlag> getPendingFlags() {
        return moderationService.getPendingFlags();
    }

    /** Moderator resolves a flag: remove the review, or dismiss the report. */
    @PostMapping("/flags/{flagId}/resolve")
    public ReviewFlag resolveFlag(@PathVariable Long flagId,
                                   @Valid @RequestBody ModerationDecisionRequest request) {
        return moderationService.resolveFlag(flagId, request);
    }
}
