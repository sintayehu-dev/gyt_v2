package gyt.movie_service.controller;

import gyt.movie_service.dto.MovieDto;
import gyt.movie_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/internal/movies")
@RequiredArgsConstructor
@Tag(name = "Internal Movies")
public class InternalMovieController {

    private final MovieService movieService;

    @GetMapping("/uuid/{uuid}")
    @Operation(summary = "Get movie by UUID (Internal)")
    public ResponseEntity<MovieDto> getMovieByUuid(@PathVariable String uuid) {
        MovieDto movie = movieService.getMovieByUuid(uuid);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/{uuid}/exists")
    @Operation(summary = "Check if movie exists (Internal)")
    public ResponseEntity<Boolean> movieExists(@PathVariable String uuid) {
        return ResponseEntity.ok(movieService.movieExists(uuid));
    }
}
