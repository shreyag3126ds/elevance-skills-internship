package com.example.seatroom.controller;

import com.example.seatroom.model.Seat;
import com.example.seatroom.service.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeatController {

    private final SeatService service;

    public SeatController(SeatService service) {
        this.service = service;
    }

    @GetMapping("/flights/{flightId}/seats")
    public List<Seat> seatMap(@PathVariable String flightId) {
        return service.getSeatMap(flightId);
    }

    @PostMapping("/seats/{id}/book")
    public Seat book(@PathVariable Long id, @RequestParam String userId) {
        return service.book(id, userId);
    }

    @PostMapping("/seats/{id}/release")
    public Seat release(@PathVariable Long id, @RequestParam String userId) {
        return service.release(id, userId);
    }
}
