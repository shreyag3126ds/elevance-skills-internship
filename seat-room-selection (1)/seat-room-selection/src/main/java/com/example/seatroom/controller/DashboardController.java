package com.example.seatroom.controller;

import com.example.seatroom.model.BookingStatus;
import com.example.seatroom.model.Room;
import com.example.seatroom.model.Seat;
import com.example.seatroom.service.RoomService;
import com.example.seatroom.service.SeatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private static final String FLIGHT_ID = "FL100";
    private static final String HOTEL_ID = "H1";

    private final SeatService seatService;
    private final RoomService roomService;

    public DashboardController(SeatService seatService, RoomService roomService) {
        this.seatService = seatService;
        this.roomService = roomService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(@RequestParam(defaultValue = "demo-user") String userId) {
        List<Seat> seats = seatService.getSeatMap(FLIGHT_ID);
        List<Room> rooms = roomService.getRooms(HOTEL_ID);

        long seatsFree = seats.stream().filter(s -> s.getStatus() == BookingStatus.AVAILABLE).count();
        long roomsFree = rooms.stream().filter(r -> r.getStatus() == BookingStatus.AVAILABLE).count();

        Map<String, Long> freeSeatsByType = seats.stream()
                .filter(s -> s.getStatus() == BookingStatus.AVAILABLE)
                .collect(Collectors.groupingBy(s -> s.getSeatType().name(), LinkedHashMap::new, Collectors.counting()));
        Map<String, Long> freeRoomsByType = rooms.stream()
                .filter(r -> r.getStatus() == BookingStatus.AVAILABLE)
                .collect(Collectors.groupingBy(r -> r.getRoomType().name(), LinkedHashMap::new, Collectors.counting()));

        List<Map<String, Object>> mine = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Seat s : seats) {
            if (s.getStatus() == BookingStatus.BOOKED && userId.equals(s.getBookedBy())) {
                mine.add(Map.of("kind", "SEAT", "label", "Seat " + s.getRowNumber() + s.getLetter(),
                        "detail", s.getSeatType().name(), "price", s.getPrice()));
                total = total.add(s.getPrice());
            }
        }
        for (Room r : rooms) {
            if (r.getStatus() == BookingStatus.BOOKED && userId.equals(r.getBookedBy())) {
                mine.add(Map.of("kind", "ROOM", "label", "Room " + r.getRoomNumber(),
                        "detail", r.getRoomType().name() + " (per night)", "price", r.getPricePerNight()));
                total = total.add(r.getPricePerNight());
            }
        }

        Map<String, Object> flight = new LinkedHashMap<>();
        flight.put("id", FLIGHT_ID);
        flight.put("total", seats.size());
        flight.put("available", seatsFree);
        flight.put("booked", seats.size() - seatsFree);
        flight.put("availableByType", freeSeatsByType);

        Map<String, Object> hotel = new LinkedHashMap<>();
        hotel.put("id", HOTEL_ID);
        hotel.put("total", rooms.size());
        hotel.put("available", roomsFree);
        hotel.put("booked", rooms.size() - roomsFree);
        hotel.put("availableByType", freeRoomsByType);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("flight", flight);
        result.put("hotel", hotel);
        result.put("myBookings", mine);
        result.put("myTotal", total);
        return result;
    }
}
