package com.example.seatroom.repository;

import com.example.seatroom.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightIdOrderByRowNumberAscLetterAsc(String flightId);
}
