package com.example.seatroom.service;

import com.example.seatroom.exception.ConflictException;
import com.example.seatroom.exception.NotFoundException;
import com.example.seatroom.model.BookingStatus;
import com.example.seatroom.model.Seat;
import com.example.seatroom.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository repo;

    public SeatService(SeatRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Seat> getSeatMap(String flightId) {
        return repo.findByFlightIdOrderByRowNumberAscLetterAsc(flightId);
    }

    @Transactional
    public Seat book(Long seatId, String userId) {
        Seat seat = repo.findById(seatId).orElseThrow(() -> new NotFoundException("Seat not found"));
        if (seat.getStatus() == BookingStatus.BOOKED) {
            throw new ConflictException("Seat " + seat.getRowNumber() + seat.getLetter() + " is already taken");
        }
        seat.setStatus(BookingStatus.BOOKED);
        seat.setBookedBy(userId);
        return repo.saveAndFlush(seat);
    }

    @Transactional
    public Seat release(Long seatId, String userId) {
        Seat seat = repo.findById(seatId).orElseThrow(() -> new NotFoundException("Seat not found"));
        if (seat.getStatus() == BookingStatus.BOOKED && !userId.equals(seat.getBookedBy())) {
            throw new ConflictException("You can only release your own seat");
        }
        seat.setStatus(BookingStatus.AVAILABLE);
        seat.setBookedBy(null);
        return repo.saveAndFlush(seat);
    }
}
