package com.internship.bookingrefund.dto;

import com.internship.bookingrefund.model.CancellationReason;
import com.internship.bookingrefund.model.RefundStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundResponseDto {

    private Long refundId;
    private Long bookingId;
    private BigDecimal originalAmount;
    private BigDecimal refundPercentage;
    private BigDecimal refundAmount;
    private CancellationReason reason;
    private RefundStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime expectedCompletionAt;
    private LocalDateTime completedAt;

    public RefundResponseDto() {}

    public RefundResponseDto(Long refundId, Long bookingId, BigDecimal originalAmount,
                              BigDecimal refundPercentage, BigDecimal refundAmount,
                              CancellationReason reason, RefundStatus status,
                              LocalDateTime requestedAt, LocalDateTime expectedCompletionAt,
                              LocalDateTime completedAt) {
        this.refundId = refundId;
        this.bookingId = bookingId;
        this.originalAmount = originalAmount;
        this.refundPercentage = refundPercentage;
        this.refundAmount = refundAmount;
        this.reason = reason;
        this.status = status;
        this.requestedAt = requestedAt;
        this.expectedCompletionAt = expectedCompletionAt;
        this.completedAt = completedAt;
    }

    public Long getRefundId() { return refundId; }
    public void setRefundId(Long refundId) { this.refundId = refundId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }

    public BigDecimal getRefundPercentage() { return refundPercentage; }
    public void setRefundPercentage(BigDecimal refundPercentage) { this.refundPercentage = refundPercentage; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public CancellationReason getReason() { return reason; }
    public void setReason(CancellationReason reason) { this.reason = reason; }

    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getExpectedCompletionAt() { return expectedCompletionAt; }
    public void setExpectedCompletionAt(LocalDateTime expectedCompletionAt) { this.expectedCompletionAt = expectedCompletionAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
