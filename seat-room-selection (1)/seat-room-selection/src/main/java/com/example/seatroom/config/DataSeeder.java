package com.example.seatroom.config;

import com.example.seatroom.model.*;
import com.example.seatroom.repository.RoomRepository;
import com.example.seatroom.repository.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SeatRepository seatRepo;
    private final RoomRepository roomRepo;

    public DataSeeder(SeatRepository seatRepo, RoomRepository roomRepo) {
        this.seatRepo = seatRepo;
        this.roomRepo = roomRepo;
    }

    @Override
    public void run(String... args) {
        // Flight FL100: 10 rows x 6 seats (A-F)
        List<Seat> seats = new ArrayList<>();
        String[] letters = {"A", "B", "C", "D", "E", "F"};
        for (int row = 1; row <= 10; row++) {
            SeatType type = row <= 2 ? SeatType.PREMIUM : row <= 4 ? SeatType.EXTRA_LEGROOM : SeatType.STANDARD;
            BigDecimal price = switch (type) {
                case PREMIUM -> new BigDecimal("80.00");
                case EXTRA_LEGROOM -> new BigDecimal("40.00");
                case STANDARD -> new BigDecimal("15.00");
            };
            for (String l : letters) {
                seats.add(new Seat("FL100", row, l, type, price));
            }
        }
        // pre-book a few so the map looks realistic
        seats.get(2).setStatus(BookingStatus.BOOKED);
        seats.get(15).setStatus(BookingStatus.BOOKED);
        seats.get(31).setStatus(BookingStatus.BOOKED);
        seatRepo.saveAll(seats);

        // Hotel H1: 10 rooms
        List<Room> rooms = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            RoomType type = i <= 5 ? RoomType.STANDARD : i <= 8 ? RoomType.DELUXE : RoomType.SUITE;
            BigDecimal price = switch (type) {
                case STANDARD -> new BigDecimal("90.00");
                case DELUXE -> new BigDecimal("140.00");
                case SUITE -> new BigDecimal("260.00");
            };
            String num = String.valueOf(100 + i);
            String imgs = String.join(",",
                    "https://picsum.photos/seed/room" + num + "a/640/400",
                    "https://picsum.photos/seed/room" + num + "b/640/400",
                    "https://picsum.photos/seed/room" + num + "c/640/400");
            // previewUrl: put a real 3D/virtual-tour link here when you have one
            rooms.add(new Room("H1", num, type, price, imgs, null));
        }
        rooms.get(1).setStatus(BookingStatus.BOOKED);
        rooms.get(6).setStatus(BookingStatus.BOOKED);
        roomRepo.saveAll(rooms);
    }
}
