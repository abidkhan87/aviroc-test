package com.vox.api;

import com.vox.TestApplication;
import com.vox.api.data.User;
import com.vox.api.data.repo.UserRepo;
import com.vox.security.Role;
import com.vox.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestApplication.class)
@Import(UserService.class)
 class UserServiceTest {

   private final UserService service;
    private final UserRepo repository;

    public UserServiceTest(@Autowired UserService service,
                           @Autowired UserRepo repository) {
        this.service = service;
        this.repository = repository;
    }

    @Test
     void findByUsername() {
        User newUser = new User();
        newUser.setUsername("abidkhan");
        newUser.setPassword("12345678");
        newUser.setFullname("Abid Khan");
        newUser.setRoles(List.of(Role.ROLE_USER));

        Mono<User> saved = repository.save(newUser);
        Mono<User> composite = service.findByUserName("abidkhan").then(Mono.just(newUser));

        StepVerifier
                .create(composite)
                .expectNextMatches(c -> StringUtils.hasText(newUser.getUsername()))
                .verifyComplete();
    }

    @Test
     void signUpSuccess() {
        User newUser = new User();
        newUser.setUsername("abidkhan");
        newUser.setPassword("12345678");
        newUser.setFullname("Abid Khan");
        newUser.setRoles(List.of(Role.ROLE_USER));

        ServerRequest mockRequest = mock(ServerRequest.class);
        when(mockRequest.bodyToMono(User.class)).thenReturn(Mono.just(newUser));

        Mono userMono = this.service.signUp(mockRequest);
        StepVerifier
            .create(userMono)
            .expectNextMatches(saved -> ((ServerResponse)saved).rawStatusCode()==200)
            .verifyComplete();
    }

   @Test
   void signUpFail() {
      User newUser = new User();
      newUser.setUsername("abidkhan1");
      newUser.setPassword("12345678");
      newUser.setFullname("Abid Khan");
      newUser.setRoles(List.of(Role.ROLE_USER));

      repository.save(newUser).block();

      ServerRequest mockRequest = mock(ServerRequest.class);
      when(mockRequest.bodyToMono(User.class)).thenReturn(Mono.just(newUser));

      Mono userMono = this.service.signUp(mockRequest);
      StepVerifier
              .create(userMono)
              .expectNextMatches(saved ->((ServerResponse)saved).rawStatusCode() ==400)
              .verifyComplete();
   }

}