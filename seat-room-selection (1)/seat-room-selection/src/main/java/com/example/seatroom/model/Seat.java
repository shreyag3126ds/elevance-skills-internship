package com.example.seatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightId;
    private int rowNumber;
    private String letter;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.AVAILABLE;

    private String bookedBy;

    /** Optimistic locking: prevents two users booking the same seat at once. */
    @Version
    private Long version;

    public Seat(String flightId, int rowNumber, String letter, SeatType seatType, BigDecimal price) {
        this.flightId = flightId;
        this.rowNumber = rowNumber;
        this.letter = letter;
        this.seatType = seatType;
        this.price = price;
    }
}
