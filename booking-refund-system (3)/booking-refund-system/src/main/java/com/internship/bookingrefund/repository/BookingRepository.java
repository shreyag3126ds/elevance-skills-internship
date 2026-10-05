package com.internship.bookingrefund.repository;

import com.internship.bookingrefund.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
