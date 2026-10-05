package com.example.seatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserPreference {

    @Id
    private String userId;

    @Enumerated(EnumType.STRING)
    private SeatType preferredSeatType;

    @Enumerated(EnumType.STRING)
    private RoomType preferredRoomType;

    public UserPreference(String userId) {
        this.userId = userId;
    }
}
