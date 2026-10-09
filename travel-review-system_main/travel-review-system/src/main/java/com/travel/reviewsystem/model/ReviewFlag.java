package com.travel.reviewsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_flag")
@Data
@NoArgsConstructor
public class ReviewFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    /** why the reporter flagged it, e.g. "spam", "offensive language" */
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlagStatus status = FlagStatus.PENDING;

    /** moderator who resolved the flag, null while PENDING */
    @ManyToOne
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    private String moderatorNote;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
