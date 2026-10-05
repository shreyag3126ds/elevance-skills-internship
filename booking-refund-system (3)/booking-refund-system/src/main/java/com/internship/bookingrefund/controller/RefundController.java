package com.internship.bookingrefund.controller;

import com.internship.bookingrefund.dto.RefundResponseDto;
import com.internship.bookingrefund.service.RefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefundResponseDto> getRefund(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.getRefundStatus(id));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<RefundResponseDto> getRefundByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(refundService.getRefundStatusByBooking(bookingId));
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<RefundResponseDto> processRefund(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.markProcessed(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<RefundResponseDto> completeRefund(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.markCompleted(id));
    }
}
