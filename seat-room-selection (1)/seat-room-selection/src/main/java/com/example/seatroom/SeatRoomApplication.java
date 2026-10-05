package com.example.seatroom;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SeatRoomApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SeatRoomApplication.class)
                .headless(false)
                .run(args);
    }
}
