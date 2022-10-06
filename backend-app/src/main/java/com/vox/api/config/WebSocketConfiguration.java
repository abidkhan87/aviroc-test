package com.vox.api.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vox.api.data.Film;
import com.vox.events.FilmEvent;
import com.vox.events.FilmEventPublisher;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

/**
 * @author abidkhan
 */
@Log4j2
@Configuration
public class WebSocketConfiguration {

	/**
	 * 
	 * Create the executor for handling the async  event in thread
	 * @return
	 */
	@Bean
	public Executor executor() {
		return Executors.newSingleThreadExecutor();
	}

	// <2>
	/**
	 * configure the websocket handler
	 * 
	 * @param wsh
	 * @return
	 */
	@Bean
	public HandlerMapping handlerMapping(WebSocketHandler wsh) {

		SimpleUrlHandlerMapping urlMapping =  new SimpleUrlHandlerMapping(); 
		urlMapping.setUrlMap(Collections.singletonMap("/ws/films", wsh));
		urlMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return urlMapping;
	}

	@Bean
	public WebSocketHandlerAdapter webSocketHandlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

	/**
	 * Create the web socket handler to post the event on websockets to be consumed by subscribers 
	 * of the websockets
	 * 
	 * @param objectMapper
	 * @param eventPublisher
	 * @return
	 */
	@Bean
	public WebSocketHandler webSocketHandler(ObjectMapper objectMapper, //
			FilmEventPublisher eventPublisher) {

		Flux<FilmEvent> publish = Flux.create(eventPublisher).share(); // <7>

		return session -> {

			Flux<WebSocketMessage> messageFlux = publish.map(evt -> {
				try {
					Film film = (Film) evt.getSource(); // <1>
					Map<String, Film> data = new HashMap<>(); // <2>
					data.put(film.getSlugLine(), film);
					return objectMapper.writeValueAsString(film);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}).map(str -> {
				log.info("sending " + str);
				return session.textMessage(str);
			});

			return session.send(messageFlux);
		};
	}

}
