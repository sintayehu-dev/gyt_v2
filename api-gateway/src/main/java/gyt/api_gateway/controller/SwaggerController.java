package gyt.api_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SwaggerController {

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @GetMapping("/v3/api-docs/swagger-config")
    public Mono<ResponseEntity<Map<String, Object>>> swaggerConfig() {
        return routeDefinitionLocator.getRouteDefinitions()
            .collectList()
            .map(routeDefinitions -> {
                List<Map<String, String>> urls = new ArrayList<>();
                
                // Add Auth Service
                Map<String, String> authService = new HashMap<>();
                authService.put("name", "Auth Service");
                authService.put("url", "/auth/v3/api-docs");
                urls.add(authService);
                
                // Add User Service
                Map<String, String> userService = new HashMap<>();
                userService.put("name", "User Service");
                userService.put("url", "/users/v3/api-docs");
                urls.add(userService);
                
                Map<String, Object> config = new HashMap<>();
                config.put("urls", urls);
                config.put("urls.primaryName", "Auth Service");
                
                return ResponseEntity.ok(config);
            });
    }
}
