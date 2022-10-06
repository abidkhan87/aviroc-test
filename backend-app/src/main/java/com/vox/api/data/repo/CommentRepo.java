package com.vox.api.data.repo;

import com.vox.api.data.Comment;
import com.vox.api.data.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author abidkhan
 */
@Repository
public interface CommentRepo extends ReactiveMongoRepository<Comment, String>{
	
	Flux<Comment> findAllByFilmId(String filmId);
}
