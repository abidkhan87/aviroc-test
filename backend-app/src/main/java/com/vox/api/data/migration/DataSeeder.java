package com.vox.api.data.migration;

import com.vox.api.data.Film;
import com.vox.api.data.User;
import com.vox.api.data.repo.FilmRepo;
import com.vox.api.data.repo.UserRepo;
import com.vox.security.PBKDF2Encoder;
import com.vox.security.Role;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Abid Khan
 * Class to migrate data
 */
@Log4j2
@Component
public class DataSeeder implements ApplicationListener<ApplicationReadyEvent> {

    private final FilmRepo filmRepo;
    private final UserRepo userRepo;
    private final PBKDF2Encoder encoder;

    public DataSeeder(FilmRepo filmRepo, UserRepo userRepo, PBKDF2Encoder encoder) {
        super();
        this.filmRepo = filmRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    /**
     * Initialize some data
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        filmRepo.deleteAll()
                .thenMany(this.addFilms()
                        .flatMap(filmRepo::save))
                .thenMany(filmRepo.findAll())
                .subscribe(film -> log.info("adding " + film.toString()));

        userRepo.deleteAll()
                .then(Mono.just(new User(UUID.randomUUID().toString(),
                        "abidkhan",
                        encoder.encode("1234"),
                        "Abid Khan",true,List.of(Role.ROLE_USER))))
                .flatMap(userRepo::save)
                .thenMany(userRepo.findByUsername("abidkhan"))
                .subscribe(user -> log.info("added user: {}", user));
    }

    private Flux<Film> addFilms() {
        return Flux.just(
                new Film(UUID.randomUUID().toString(),"Batman", "Story of a ship that gets drowned", LocalDateTime.now().minusYears(26), 3, 90.0F, "USA", Arrays.asList("Suspense", "Fiction"), " Batman", null)
                , new Film(UUID.randomUUID().toString(),"Oceans Eleven", "Casino robbery. Also involves action", LocalDateTime.now().minusYears(20), 4, 80.0F, "USA", Arrays.asList("Action", "Suspense", "Thriller"), "Oceans Eleven", null)
                , new Film(UUID.randomUUID().toString(),"Saving Private Ryan", "A movie based on World War 2", LocalDateTime.now().minusYears(21), 5, 100.0F, "USA", Arrays.asList("Action", "War", "Thriller"), "Saving Private Ryan", null)
        );
    }

}
