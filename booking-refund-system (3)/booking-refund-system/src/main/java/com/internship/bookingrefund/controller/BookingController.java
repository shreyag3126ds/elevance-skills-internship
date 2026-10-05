package com.internship.bookingrefund.controller;

import com.internship.bookingrefund.dto.CancelBookingRequest;
import com.internship.bookingrefund.dto.RefundResponseDto;
import com.internship.bookingrefund.model.Booking;
import com.internship.bookingrefund.repository.BookingRepository;
import com.internship.bookingrefund.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public BookingController(BookingService bookingService, BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<RefundResponseDto> cancelBooking(@PathVariable Long id,
                                                             @Valid @RequestBody CancelBookingRequest request) {
        RefundResponseDto refund = bookingService.cancelBooking(id, request);
        return ResponseEntity.ok(refund);
    }
}
