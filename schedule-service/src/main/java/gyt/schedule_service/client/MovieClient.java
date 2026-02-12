package gyt.schedule_service.client;

import gyt.schedule_service.dto.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service")
public interface MovieClient {
    
    @GetMapping("/internal/movies/uuid/{uuid}")
    MovieDto getMovieByUuid(@PathVariable String uuid);
}
