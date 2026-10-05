package com.internship.bookingrefund.service;

import com.internship.bookingrefund.dto.CancelBookingRequest;
import com.internship.bookingrefund.dto.RefundResponseDto;
import com.internship.bookingrefund.exception.ResourceNotFoundException;
import com.internship.bookingrefund.model.*;
import com.internship.bookingrefund.repository.BookingRepository;
import com.internship.bookingrefund.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RefundRepository refundRepository;
    private final RefundPolicyService refundPolicyService;

    public BookingService(BookingRepository bookingRepository,
                           RefundRepository refundRepository,
                           RefundPolicyService refundPolicyService) {
        this.bookingRepository = bookingRepository;
        this.refundRepository = refundRepository;
        this.refundPolicyService = refundPolicyService;
    }

    @Transactional
    public RefundResponseDto cancelBooking(Long bookingId, CancelBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking " + bookingId + " is already cancelled");
        }

        LocalDateTime now = LocalDateTime.now();

        BigDecimal refundPercentage = refundPolicyService.calculateRefundPercentage(now, booking.getReservationTime());
        BigDecimal refundAmount = refundPolicyService.calculateRefundAmount(booking.getAmount(), refundPercentage);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Refund refund = new Refund();
        refund.setBooking(booking);
        refund.setOriginalAmount(booking.getAmount());
        refund.setRefundPercentage(refundPercentage);
        refund.setRefundAmount(refundAmount);
        refund.setReason(request.getReason());
        refund.setRequestedAt(now);
        refund.setExpectedCompletionAt(refundPolicyService.estimateCompletionDate(now));
        refund.setStatus(refundPercentage.compareTo(BigDecimal.ZERO) == 0
                ? RefundStatus.NOT_APPLICABLE
                : RefundStatus.PENDING);

        Refund saved = refundRepository.save(refund);
        return toDto(saved);
    }

    public static RefundResponseDto toDto(Refund refund) {
        return new RefundResponseDto(
                refund.getId(),
                refund.getBooking().getId(),
                refund.getOriginalAmount(),
                refund.getRefundPercentage(),
                refund.getRefundAmount(),
                refund.getReason(),
                refund.getStatus(),
                refund.getRequestedAt(),
                refund.getExpectedCompletionAt(),
                refund.getCompletedAt()
        );
    }
}
