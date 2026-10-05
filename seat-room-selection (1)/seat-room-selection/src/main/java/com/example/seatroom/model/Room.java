package com.example.seatroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hotelId;
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private BigDecimal pricePerNight;

    /** Comma-separated image URLs (room previews). */
    @JsonIgnore
    @Column(length = 1000)
    private String imageUrls;

    /** Optional link to a 3D / virtual tour. */
    private String previewUrl;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.AVAILABLE;

    private String bookedBy;

    @Version
    private Long version;

    public Room(String hotelId, String roomNumber, RoomType roomType, BigDecimal pricePerNight,
                String imageUrls, String previewUrl) {
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.imageUrls = imageUrls;
        this.previewUrl = previewUrl;
    }

    @JsonProperty("images")
    public List<String> getImages() {
        return imageUrls == null || imageUrls.isBlank() ? List.of() : Arrays.asList(imageUrls.split(","));
    }
}
