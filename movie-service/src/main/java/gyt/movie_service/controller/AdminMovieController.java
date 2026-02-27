package gyt.movie_service.controller;

import gyt.movie_service.dto.*;
import gyt.movie_service.service.MetadataService;
import gyt.movie_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
@Tag(name = "Admin Movies")
public class AdminMovieController {

    private final MovieService movieService;
    private final MetadataService metadataService;

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new movie")
    public ResponseEntity<ApiResponse<MovieDto>> createMovie(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam("releaseDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate releaseDate,
            @RequestParam("poster") org.springframework.web.multipart.MultipartFile poster,
            @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "genreUuids", required = false) java.util.List<String> genreUuids,
            @RequestParam(value = "directorUuids", required = false) java.util.List<String> directorUuids,
            @RequestParam(value = "starUuids", required = false) java.util.List<String> starUuids
    ) {
        MovieCreateRequest request = MovieCreateRequest.builder()
                .title(title)
                .description(description)
                .durationMinutes(durationMinutes)
                .releaseDate(releaseDate)
                .trailerUrl(trailerUrl)
                .language(language)
                .genreUuids(genreUuids)
                .directorUuids(directorUuids)
                .starUuids(starUuids)
                .build();

        MovieDto movie = movieService.createMovie(request, poster);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(movie, "Movie created successfully")
        );
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update movie")
    public ResponseEntity<ApiResponse<MovieDto>> updateMovie(
            @PathVariable String uuid,
            @RequestBody MovieCreateRequest request) {
        MovieDto movie = movieService.updateMovie(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(movie, "Movie updated successfully")
        );
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete movie")
    public ResponseEntity<ApiResponse<Object>> deleteMovie(@PathVariable String uuid) {
        movieService.deleteMovie(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Movie deleted successfully")
        );
    }

    @PostMapping("/genres")
    @Operation(summary = "Create genre")
    public ResponseEntity<ApiResponse<GenreDto>> createGenre(@RequestBody GenreRequest request) {
        GenreDto genre = metadataService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(genre, "Genre created successfully")
        );
    }

    @PatchMapping("/genres/{uuid}")
    @Operation(summary = "Update genre")
    public ResponseEntity<ApiResponse<GenreDto>> patchGenre(@PathVariable String uuid, @RequestBody GenreRequest request) {
        GenreDto genre = metadataService.patchGenre(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(genre, "Genre updated successfully")
        );
    }

    @DeleteMapping("/genres/{uuid}")
    @Operation(summary = "Delete genre")
    public ResponseEntity<ApiResponse<Object>> deleteGenre(@PathVariable String uuid) {
        metadataService.deleteGenre(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Genre deleted successfully")
        );
    }

    @PostMapping("/directors")
    @Operation(summary = "Create director")
    public ResponseEntity<ApiResponse<DirectorDto>> createDirector(@RequestBody DirectorRequest request) {
        DirectorDto director = metadataService.createDirector(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(director, "Director created successfully")
        );
    }

    @PatchMapping("/directors/{uuid}")
    @Operation(summary = "Update director")
    public ResponseEntity<ApiResponse<DirectorDto>> patchDirector(@PathVariable String uuid, @RequestBody DirectorRequest request) {
        DirectorDto director = metadataService.patchDirector(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(director, "Director updated successfully")
        );
    }

    @DeleteMapping("/directors/{uuid}")
    @Operation(summary = "Delete director")
    public ResponseEntity<ApiResponse<Object>> deleteDirector(@PathVariable String uuid) {
        metadataService.deleteDirector(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Director deleted successfully")
        );
    }

    @PostMapping("/stars")
    @Operation(summary = "Create star")
    public ResponseEntity<ApiResponse<StarDto>> createStar(@RequestBody StarRequest request) {
        StarDto star = metadataService.createStar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(star, "Star created successfully")
        );
    }

    @PatchMapping("/stars/{uuid}")
    @Operation(summary = "Update star")
    public ResponseEntity<ApiResponse<StarDto>> patchStar(@PathVariable String uuid, @RequestBody StarRequest request) {
        StarDto star = metadataService.patchStar(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(star, "Star updated successfully")
        );
    }

    @DeleteMapping("/stars/{uuid}")
    @Operation(summary = "Delete star")
    public ResponseEntity<ApiResponse<Object>> deleteStar(@PathVariable String uuid) {
        metadataService.deleteStar(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Star deleted successfully")
        );
    }
}
