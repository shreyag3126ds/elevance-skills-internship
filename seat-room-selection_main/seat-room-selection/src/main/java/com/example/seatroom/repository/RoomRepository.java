package com.example.seatroom.repository;

import com.example.seatroom.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelIdOrderByRoomNumberAsc(String hotelId);
}
