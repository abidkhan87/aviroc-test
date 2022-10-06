package com.vox.service;

import com.vox.api.data.User;
import com.vox.api.data.repo.UserRepo;
import com.vox.dto.AuthResponse;
import com.vox.dto.Message;
import com.vox.security.JWTUtil;
import com.vox.security.PBKDF2Encoder;
import com.vox.security.Role;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author abidkhan
 */
@Service
@Log4j2
public class UserService {
    private final UserRepo userRepo;
    private final PBKDF2Encoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public UserService(UserRepo userRepo, PBKDF2Encoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<User> findByUserName(String username) {
        return this.userRepo.findByUsername(username);
    }


    public Mono signUp(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);
        return userMono.map(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of(Role.ROLE_USER));
            return user;
        }).flatMap(user ->
                userRepo.findByUsername(user.getUsername())
                        .flatMap(dbUser -> ServerResponse.badRequest().body(BodyInserters.fromObject(new Message("User already exist"))))
                        .switchIfEmpty(userRepo.save(user)
                                .flatMap(savedUser ->
                                        ServerResponse
                                                .ok()
                                                .contentType(APPLICATION_JSON)
                                                .body(BodyInserters.fromObject(
                                                        new AuthResponse(jwtUtil.generateToken(savedUser)))))
                        )
        ).onErrorMap(x -> new RuntimeException("Error Occurred"));

    }
}
