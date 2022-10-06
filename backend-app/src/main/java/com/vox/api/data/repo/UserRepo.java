package com.vox.api.data.repo;

import com.vox.api.data.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author abidkhan
 */
@Repository
public interface UserRepo extends ReactiveMongoRepository<User, String>{
	
	Mono<User> findByUsername(String username);
}
