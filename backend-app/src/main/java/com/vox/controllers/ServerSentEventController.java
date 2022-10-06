package com.vox.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vox.events.FilmEvent;
import com.vox.events.FilmEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author abidkhan
 */
@RestController
@CrossOrigin("*")
public class ServerSentEventController {
    private final Flux<FilmEvent> events;
    private final ObjectMapper objectMapper;

    public ServerSentEventController(FilmEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.events = Flux.create(eventPublisher).share();
        this.objectMapper = objectMapper;
    }

    /**
     * For every event posted , send it back to the client as Flux in non blocking and async way
     * for the purpose of notifications
     * 
     * @return
     */
    @GetMapping(path = "/events/films", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<String> films() {
        return this.events.map(fce -> {
            try {
                return objectMapper.writeValueAsString(fce) + "\n\n";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}