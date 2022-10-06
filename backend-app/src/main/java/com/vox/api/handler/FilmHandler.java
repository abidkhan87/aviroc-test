package com.vox.api.handler;

import com.vox.api.data.Comment;
import com.vox.api.data.Film;
import com.vox.dto.Message;
import com.vox.service.FilmService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author abidkhan
 */
@Component
@CrossOrigin("*")
public class FilmHandler {

    private final FilmService filmService;

    public FilmHandler(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Write the response in db and emit an event to be posted on the Web socket
     *
     * @param films
     * @return
     */
    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Film> films, String message) {
        return Mono
                .from(films)
                .flatMap(f -> ServerResponse
                        .created(URI.create("/films/" + f.getSlugLine()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .build())
                .switchIfEmpty(
                        ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(new Message(message))
                );
    }

    // <4>
    private static Mono<ServerResponse> defaultReadResponse(Publisher<Film> film) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(film, Film.class);
    }

    private static String getSlugName(ServerRequest r) {
        return r.pathVariable("slugName");
    }
    private static String getFilmId(ServerRequest r) {
        return r.pathVariable("filmId");
    }

    /**
     * GET based on slugName
     *
     * @param r
     * @return
     */
    public Mono<ServerResponse> getBySlugName(ServerRequest r) {
        return defaultReadResponse(this.filmService.get(getSlugName(r)));
    }


    public Mono<ServerResponse> findBySlugWithComments(ServerRequest r){
        return defaultReadResponse(this.filmService.findBySlugWithComments(getFilmId(r)));
    }
    /**
     * GET ALL
     *
     * @param r
     * @return
     */
    public Mono<ServerResponse> all(ServerRequest r) {
        return defaultReadResponse(this.filmService.all());
    }

    /**
     * Create new film
     *
     * @param request
     * @return
     */

    @PreAuthorize("hasRole('USER')")
    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<Film> flux = request
                .bodyToFlux(Film.class)
                .flatMap(this.filmService::create);
        return defaultWriteResponse(flux, "unable to create");
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<ServerResponse> addCommentToFilm(ServerRequest request) {
        Flux<Film> flux = request
                .bodyToFlux(Comment.class)
                .flatMap(this.filmService::addComment);
//                .switchIfEmpty(Mono.just(new Film()));
        return defaultWriteResponse(flux,"unable to add comment");
    }

}
