package com.vox.api;

import com.vox.TestApplication;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.vox.api.data.Film;
import com.vox.api.data.repo.FilmRepo;
import com.vox.service.FilmService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestApplication.class)

@Import(FilmService.class)
 class FilmServiceTest {

    private final FilmService service;
    private final FilmRepo repository;

    public FilmServiceTest(@Autowired FilmService service,
                              @Autowired FilmRepo repository) {
        this.service = service;
        this.repository = repository;
    }

    @Test
     void getAll() {
        Flux<Film> saved = repository.saveAll(Flux.just(new Film(UUID.randomUUID().toString(), 
				"sholay",  ": A film ",
				LocalDateTime.now().minusYears(20L), 5, 25.20F, "INDIA",
				List.of("THRILLER", "ACTION", "COMEDY"), "sholay", List.of()),
        		new Film(UUID.randomUUID().toString(), 
        				"godfather",  ": A film ",
        				LocalDateTime.now().minusYears(20L), 5, 25.20F, "USA",
        				List.of("THRILLER", "ACTION", "COMEDY"), "godfather", List.of()))
				);

        Flux<Film> composite = service.all().thenMany(saved);

        Predicate<Film> match = film -> saved.any(saveItem -> saveItem.equals(film)).block();

        StepVerifier
            .create(composite)
            .expectNextMatches(match)
            .expectNextMatches(match)
            .verifyComplete();
    }

    @Test
     void save() {
    	Film film = new Film(UUID.randomUUID().toString(), 
				"test", ": A film ",
				LocalDateTime.now().minusYears(20L), 5, 25.20F, "INDIA",
				List.of("THRILLER", "ACTION", "COMEDY"), "test", List.of());
        Mono<Film> filmMono = this.service.create(film);
        StepVerifier
            .create(filmMono)
            .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
            .verifyComplete();
    }

}