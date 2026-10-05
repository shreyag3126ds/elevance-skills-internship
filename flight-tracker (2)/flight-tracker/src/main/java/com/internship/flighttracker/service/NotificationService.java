package com.internship.flighttracker.service;

import com.internship.flighttracker.model.StatusUpdate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles "push notifications" to the dashboard/app.
 *
 * For a real mobile app you'd plug this into FCM/APNs; for a web dashboard,
 * Server-Sent Events (SSE) give the same "live update" experience with far
 * less setup than WebSockets, which is why it's used here. Every connected
 * client (browser tab / dashboard session) gets an emitter that stays open
 * and receives events as soon as a flight's status changes.
 */
@Service
public class NotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout - stays open
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    public void broadcast(StatusUpdate update) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("flight-status-update")
                        .data(update));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
