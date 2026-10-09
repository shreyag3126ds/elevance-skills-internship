package com.example.seatroom.service;

import com.example.seatroom.exception.ConflictException;
import com.example.seatroom.exception.NotFoundException;
import com.example.seatroom.model.BookingStatus;
import com.example.seatroom.model.Room;
import com.example.seatroom.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository repo;

    public RoomService(RoomRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Room> getRooms(String hotelId) {
        return repo.findByHotelIdOrderByRoomNumberAsc(hotelId);
    }

    @Transactional
    public Room book(Long roomId, String userId) {
        Room room = repo.findById(roomId).orElseThrow(() -> new NotFoundException("Room not found"));
        if (room.getStatus() == BookingStatus.BOOKED) {
            throw new ConflictException("Room " + room.getRoomNumber() + " is already booked");
        }
        room.setStatus(BookingStatus.BOOKED);
        room.setBookedBy(userId);
        return repo.saveAndFlush(room);
    }

    @Transactional
    public Room release(Long roomId, String userId) {
        Room room = repo.findById(roomId).orElseThrow(() -> new NotFoundException("Room not found"));
        if (room.getStatus() == BookingStatus.BOOKED && !userId.equals(room.getBookedBy())) {
            throw new ConflictException("You can only release your own room");
        }
        room.setStatus(BookingStatus.AVAILABLE);
        room.setBookedBy(null);
        return repo.saveAndFlush(room);
    }
}
