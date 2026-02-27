package gyt.api_gateway.config;

import gyt.api_gateway.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                        JwtAuthenticationFilter jwtAuthenticationFilter) {
                http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .cors(ServerHttpSecurity.CorsSpec::disable)
                                .authorizeExchange(exchanges -> exchanges
                                                .pathMatchers("/actuator/**").permitAll()
                                                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", 
                                                                "/webjars/**", "/auth/v3/api-docs/**", "/users/v3/api-docs/**").permitAll()
                                                .pathMatchers("/auth/**").permitAll()
                                                .pathMatchers("/movies", "/movies/**", "/schedules", "/schedules/**",
                                                                "/tickets", "/tickets/**").permitAll()
                                                .pathMatchers("/admin/**").hasRole("ADMIN")
                                                .pathMatchers("/users/**").authenticated()
                                                .anyExchange().authenticated())
                                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                                                .authenticationEntryPoint((exchange, ex) -> {
                                                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                                        exchange.getResponse().getHeaders()
                                                                        .setContentType(MediaType.APPLICATION_JSON);

                                                        String timestamp = LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                                        String errorBody = String.format(
                                                                        "{\"status\": 401, \"error\": \"Unauthorized\", "
                                                                                        +
                                                                                        "\"message\": \"Authentication required. Please provide a valid JWT token in the Authorization header.\", "
                                                                                        +
                                                                                        "\"timestamp\": \"%s\"}",
                                                                        timestamp);

                                                        return exchange.getResponse().writeWith(
                                                                        Mono.just(exchange.getResponse().bufferFactory()
                                                                                        .wrap(errorBody.getBytes())));
                                                })
                                                .accessDeniedHandler((exchange, denied) -> {
                                                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                                        exchange.getResponse().getHeaders()
                                                                        .setContentType(MediaType.APPLICATION_JSON);

                                                        String timestamp = LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                                        String errorBody = String.format(
                                                                        "{\"status\": 403, \"error\": \"Forbidden\", " +
                                                                                        "\"message\": \"Access denied. This endpoint requires ADMIN role. Your current role does not have permission to access this resource.\", "
                                                                                        +
                                                                                        "\"timestamp\": \"%s\"}",
                                                                        timestamp);

                                                        return exchange.getResponse().writeWith(
                                                                        Mono.just(exchange.getResponse().bufferFactory()
                                                                                        .wrap(errorBody.getBytes())));
                                                }));

                return http.build();
        }
}
