package org.example.lab1.model;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final List<SseEmitter> emitters;

    private static final MediaType TEXT_PLAIN_UTF8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    public NotificationService() {
        this.emitters = new CopyOnWriteArrayList<>();
    }

    public NotificationService(List<SseEmitter> emitters) {
        this.emitters = emitters;
    }

    public void sendEvent(String message) {
        for (SseEmitter currEmitter : this.emitters) {
            try {
                currEmitter.send(SseEmitter.event().name(message).data(message, TEXT_PLAIN_UTF8));
            } catch (Exception e) {
                try {
                    currEmitter.completeWithError(e);
                } catch (Exception ignored) {
                }
                unregisterSseEmitter(currEmitter);
            }
        }
    }

    public void sendEventWithMessage(String event, String message) {
        for (SseEmitter currEmitter : this.emitters) {
            try {
                currEmitter.send(SseEmitter.event().name(event).data(message, TEXT_PLAIN_UTF8));
            } catch (Exception e) {
                try {
                    currEmitter.completeWithError(e);
                } catch (Exception ignored) {
                }
                unregisterSseEmitter(currEmitter);
            }
        }
    }

    public void registerSseEmitter(SseEmitter emitter) {
        this.emitters.add(emitter);
    }

    public void unregisterSseEmitter(SseEmitter emitter) {
        this.emitters.remove(emitter);
    }
}
