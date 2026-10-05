package com.example.seatroom.controller;

import com.example.seatroom.model.Room;
import com.example.seatroom.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @GetMapping("/hotels/{hotelId}/rooms")
    public List<Room> rooms(@PathVariable String hotelId) {
        return service.getRooms(hotelId);
    }

    @PostMapping("/rooms/{id}/book")
    public Room book(@PathVariable Long id, @RequestParam String userId) {
        return service.book(id, userId);
    }

    @PostMapping("/rooms/{id}/release")
    public Room release(@PathVariable Long id, @RequestParam String userId) {
        return service.release(id, userId);
    }
}
