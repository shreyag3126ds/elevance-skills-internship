package com.internship.bookingrefund.dto;

import com.internship.bookingrefund.model.CancellationReason;
import jakarta.validation.constraints.NotNull;

public class CancelBookingRequest {

    @NotNull(message = "A cancellation reason must be selected")
    private CancellationReason reason;

    public CancelBookingRequest() {}

    public CancelBookingRequest(CancellationReason reason) {
        this.reason = reason;
    }

    public CancellationReason getReason() { return reason; }
    public void setReason(CancellationReason reason) { this.reason = reason; }
}
