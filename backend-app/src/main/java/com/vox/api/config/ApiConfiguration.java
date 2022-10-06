package com.vox.api.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.vox.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.vox.api.handler.FilmHandler;

/**
 * @author abidkhan
 */
@Configuration
public class ApiConfiguration {
	
	/**
	 * 
	 * Router function for the routes 
	 * and mapping with the service
	 * 
	 * 
	 * @param handler
	 * @return 
	 */
	@Bean
    RouterFunction<ServerResponse> routes(FilmHandler handler, UserService userService) {
        return route(GET("/films"), handler::all) //
            .andRoute(GET("/films/{slugName}"), handler::getBySlugName)
            .andRoute(GET("/films/slug/{filmId}"), handler::findBySlugWithComments)
            .andRoute(POST("/films"), handler::create)
            .andRoute(POST("/films/comment"), handler::addCommentToFilm)
            .andRoute(POST("/signup"), userService::signUp)
        .and(resources("/**", new ClassPathResource("static/")));
    }


}
