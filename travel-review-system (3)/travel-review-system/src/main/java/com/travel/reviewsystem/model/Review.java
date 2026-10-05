package com.travel.reviewsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** HOTEL or FLIGHT */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    /** id of the Hotel or Flight being reviewed */
    @Column(nullable = false)
    private Long targetId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    @NotBlank
    @Column(length = 4000, nullable = false)
    private String text;

    /** URLs/paths of photos uploaded with this review */
    @ElementCollection
    @CollectionTable(name = "review_photos", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    @Column(nullable = false)
    private int helpfulVotes = 0;

    /** true once a moderator has removed this review after a flag */
    @Column(nullable = false)
    private boolean removed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewReply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewFlag> flags = new ArrayList<>();
}
