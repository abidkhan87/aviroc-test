package com.vox.api.data.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.vox.api.data.Film;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author abidkhan
 */
@Repository
public interface FilmRepo extends ReactiveMongoRepository<Film, String>{
	
	Flux<Film> findBySlugLineLike(String slugLine);
	Mono<Film> findById(String id);
}
