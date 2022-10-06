package com.vox.service;

import com.vox.api.data.Comment;
import com.vox.api.data.Film;
import com.vox.api.data.repo.CommentRepo;
import com.vox.api.data.repo.FilmRepo;
import com.vox.events.FilmEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author abidkhan
 */
@Service
@Log4j2
public class FilmService {

    private final ApplicationEventPublisher eventPublisher;
    private final FilmRepo repo;
    private final CommentRepo commentRepo;

    public FilmService(ApplicationEventPublisher eventPublisher, FilmRepo repo, CommentRepo commentRepo) {
//		super();
        this.eventPublisher = eventPublisher;
        this.repo = repo;
        this.commentRepo = commentRepo;
    }

    /**
     * Get all the films
     *
     * @return
     */
    public Flux<Film> all() {
        log.info("Getting all");
        return this.repo.findAll();
    }

    /**
     * Get films based on the slugLines
     *
     * @param slugLine
     * @return
     */
    public Flux<Film> get(String slugLine) {
        log.info("Getting film with slugLine {}", slugLine);
        return this.repo.findBySlugLineLike(slugLine)
                .doOnNext(onNxt -> this.eventPublisher.publishEvent(new FilmEvent(onNxt)));
    }

    /**
     * Create new film
     *
     * @param film
     * @return
     */
    public Mono<Film> create(Film film) {
        log.info("Creating the film: {}", film);
        if (film.getRating() < 1 || film.getRating() > 5) {
            return Mono.error(new RuntimeException("rating value must be between 1 and 5"));
        }
        return this.repo.save(film)
                .doOnSuccess(movie -> this.eventPublisher.publishEvent(new FilmEvent(movie)));
    }

    public Flux<Comment> getAllCommentsForFilm(String id) {
        return this.repo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("not found")))
                .thenMany(commentRepo.findAllByFilmId(id))
                .filter(comment -> comment.getFilmId().equals(id));
    }

    public Mono<Film> findByIdWithComments(String id) {
        return this.repo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("")))
                .flatMap(postFound -> this.getAllCommentsForFilm(postFound.getId())
                        .collectList()
                        .flatMap(comments -> {
                            postFound.setComments(comments);
                            return Mono.just(postFound);
                        })
                );
    }

    public Mono<Film> findBySlugWithComments(String slug) {
        return this.repo.findBySlugLineLike(slug).single()
                .switchIfEmpty(Mono.error(new RuntimeException("")))
                .flatMap(postFound -> this.getAllCommentsForFilm(postFound.getId())
                        .collectList()
                        .flatMap(comments -> {
                            postFound.setComments(comments);
                            return Mono.just(postFound);
                        })
                );
    }

    public Mono<Film> addComment(Comment comment){
        log.info("Adding comment to film {}", comment);
        Mono<Film> filmFound =this.repo.findById(comment.getFilmId());
        return filmFound.flatMap(f->{
            if(f==null)
                return Mono.error(new RuntimeException());
            return commentRepo.save(comment);
        })
                .flatMap(x->this.findByIdWithComments(x.getFilmId()))
                .doOnSuccess(movie-> {
                    if(movie != null)
                            this.eventPublisher.publishEvent(new FilmEvent(movie));
                });
    }
}
