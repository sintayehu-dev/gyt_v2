package gyt.movie_service.controller;

import gyt.movie_service.dto.MovieDto;
import gyt.movie_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/movies")
@RequiredArgsConstructor
public class InternalMovieController {

    private final MovieService movieService;

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<MovieDto> getMovieByUuid(@PathVariable String uuid) {
        MovieDto movie = movieService.getMovieByUuid(uuid);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/{uuid}/exists")
    public ResponseEntity<Boolean> movieExists(@PathVariable String uuid) {
        return ResponseEntity.ok(movieService.movieExists(uuid));
    }
}
