package com.travel.reviewsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_reply")
@Data
@NoArgsConstructor
public class ReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @NotBlank
    @Column(length = 2000, nullable = false)
    private String text;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
