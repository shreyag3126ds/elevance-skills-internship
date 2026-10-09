package com.travel.reviewsystem.model;

/**
 * Lifecycle of a flagged (reported) review, as it moves through moderation.
 */
public enum FlagStatus {
    PENDING,    // reported, waiting for a moderator
    DISMISSED,  // moderator looked at it and found no issue
    REMOVED     // moderator removed the review
}
