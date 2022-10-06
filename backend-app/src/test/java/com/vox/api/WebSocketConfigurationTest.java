package com.vox.api;

import com.vox.TestApplication;
import com.vox.api.data.Film;
import com.vox.dto.AuthRequest;
import com.vox.dto.AuthResponse;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestApplication.class)
class WebSocketConfigurationTest {

    @Value("${server.port}")
    private String port;
    private final WebSocketClient socketClient = new ReactorNettyWebSocketClient();

    private final WebClient webClient = WebClient.builder().build();

    private Film generateRandomFilm() {
        return new Film(UUID.randomUUID().toString(), 
				"test", ": A film ",
				LocalDateTime.now().minusYears(20L), 5, 25.20F, "INDIA",
				List.of("THRILLER", "ACTION", "COMEDY"), "test", List.of());
    }

    @Test
    @WithMockUser
    public void testNotificationsOnUpdates() throws Exception {
        int count = 10;
        AtomicLong counter = new AtomicLong();
        URI uri = URI.create("ws://localhost:"+port+"/ws/films");

        socketClient.execute(uri, (WebSocketSession session) -> {
            Mono<WebSocketMessage> out = Mono.just(session.textMessage("test"));
            Flux<String> in = session
                .receive()
                .map(WebSocketMessage::getPayloadAsText);
            return session
                .send(out)
                .thenMany(in)
                .doOnNext(str -> counter.incrementAndGet())
                .then();

        }).subscribe();

        Flux
            .<Film>generate(sink -> sink.next(generateRandomFilm()))
            .take(count)
            .flatMap(this::write)
            .blockLast();

        Thread.sleep(1000);

        Assertions.assertThat(counter.get()).isEqualTo(count);
    }

    private Publisher<Film> write(Film p) {

        Mono r = this.webClient.post().uri("http://localhost:"+port+"/login").bodyValue(new AuthRequest("abidkhan","12345678")).retrieve().bodyToMono(AuthResponse.class);
        return
            this.webClient
                .post()
                .uri("http://localhost:"+port+"/films")
                .body(BodyInserters.fromObject(p))
                .retrieve()
                .bodyToMono(String.class)
                .thenReturn(p);
    }
}