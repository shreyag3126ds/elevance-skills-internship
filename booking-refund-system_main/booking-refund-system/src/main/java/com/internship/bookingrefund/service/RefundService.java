package com.internship.bookingrefund.service;

import com.internship.bookingrefund.dto.RefundResponseDto;
import com.internship.bookingrefund.exception.ResourceNotFoundException;
import com.internship.bookingrefund.model.Refund;
import com.internship.bookingrefund.model.RefundStatus;
import com.internship.bookingrefund.repository.RefundRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefundService {

    private final RefundRepository refundRepository;

    public RefundService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    public RefundResponseDto getRefundStatus(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with id " + refundId));
        return BookingService.toDto(refund);
    }

    public RefundResponseDto getRefundStatusByBooking(Long bookingId) {
        Refund refund = refundRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No refund found for booking " + bookingId));
        return BookingService.toDto(refund);
    }

    public RefundResponseDto markProcessed(Long refundId) {
        Refund refund = getRequiredRefund(refundId);
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException("Refund must be PENDING to move to PROCESSED");
        }
        refund.setStatus(RefundStatus.PROCESSED);
        refund.setProcessedAt(LocalDateTime.now());
        return BookingService.toDto(refundRepository.save(refund));
    }

    public RefundResponseDto markCompleted(Long refundId) {
        Refund refund = getRequiredRefund(refundId);
        if (refund.getStatus() != RefundStatus.PROCESSED) {
            throw new IllegalStateException("Refund must be PROCESSED to move to COMPLETED");
        }
        refund.setStatus(RefundStatus.COMPLETED);
        refund.setCompletedAt(LocalDateTime.now());
        return BookingService.toDto(refundRepository.save(refund));
    }

    private Refund getRequiredRefund(Long refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with id " + refundId));
    }
}
