package com.internship.bookingrefund.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class RefundPolicyService {

    private static final BigDecimal FULL_REFUND = new BigDecimal("100");
    private static final BigDecimal PARTIAL_REFUND_EARLY = new BigDecimal("75");
    private static final BigDecimal PARTIAL_REFUND_LATE = new BigDecimal("50");
    private static final BigDecimal NO_REFUND = BigDecimal.ZERO;

    public BigDecimal calculateRefundPercentage(LocalDateTime cancellationTime, LocalDateTime reservationTime) {
        if (cancellationTime.isAfter(reservationTime)) {
            return NO_REFUND;
        }

        long hoursUntilReservation = Duration.between(cancellationTime, reservationTime).toHours();

        if (hoursUntilReservation >= 24 * 7) {
            return FULL_REFUND;
        } else if (hoursUntilReservation >= 24) {
            return PARTIAL_REFUND_EARLY;
        } else {
            return PARTIAL_REFUND_LATE;
        }
    }

    public BigDecimal calculateRefundAmount(BigDecimal bookingAmount, BigDecimal refundPercentage) {
        return bookingAmount
                .multiply(refundPercentage)
                .divide(new BigDecimal("100"));
    }

    public LocalDateTime estimateCompletionDate(LocalDateTime requestedAt) {
        return requestedAt.plusDays(5);
    }
}
