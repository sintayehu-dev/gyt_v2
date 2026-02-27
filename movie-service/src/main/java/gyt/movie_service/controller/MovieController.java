package gyt.movie_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gyt.movie_service.dto.ApiResponse;
import gyt.movie_service.dto.DirectorDto;
import gyt.movie_service.dto.GenreDto;
import gyt.movie_service.dto.MovieDto;
import gyt.movie_service.dto.StarDto;
import gyt.movie_service.service.MetadataService;
import gyt.movie_service.service.MovieService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Tag(name = "Movies")
public class MovieController {

    private final MovieService movieService;
    private final MetadataService metadataService;

    @GetMapping
    @Operation(summary = "Get all movies")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<MovieDto>>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<MovieDto> pagedResponse = movieService.getAllMovies(page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Movies retrieved successfully")
        );
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get movie by UUID")
    public ResponseEntity<ApiResponse<MovieDto>> getMovieByUuid(@PathVariable String uuid) {
        MovieDto movie = movieService.getMovieByUuid(uuid);
        return ResponseEntity.ok(ApiResponse.ok(movie, "Movie retrieved successfully")
        );
    }

    @GetMapping("/search")
    @Operation(summary = "Search movies by title")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<MovieDto>>> searchMovies(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<MovieDto> pagedResponse = movieService.searchMoviesByTitle(title, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok( ApiResponse.ok(listResponse, "Movies found successfully")
        );
    }

    @GetMapping("/advanced-search")
    @Operation(summary = "Advanced movie search")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<MovieDto>>> advancedSearchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String star,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<MovieDto> pagedResponse = movieService.advancedSearch(
            title, description, language, genre, director, star, 
            minDuration, maxDuration, page, size
        );
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.ok(listResponse, "Movies found successfully"));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter movies")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<MovieDto>>> filterMovies(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String director,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<MovieDto> pagedResponse = movieService.filterMovies(genre, director, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.ok(listResponse, "Movies filtered successfully")
        );
    }

    @GetMapping("/genres")
    @Operation(summary = "Get all genres")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<GenreDto>>> getAllGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<GenreDto> pagedResponse = metadataService.getAllGenres(page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok( ApiResponse.ok(listResponse, "Genres retrieved successfully")
        );
    }

    @GetMapping("/genres/{uuid}")
    @Operation(summary = "Get genre by UUID")
    public ResponseEntity<ApiResponse<GenreDto>> getGenreByUuid(@PathVariable String uuid) {
        GenreDto genre = metadataService.getGenreByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(genre, "Genre retrieved successfully")
        );
    }

    @GetMapping("/directors")
    @Operation(summary = "Get all directors")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<DirectorDto>>> getAllDirectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<DirectorDto> pagedResponse = metadataService.getAllDirectors(page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Directors retrieved successfully")
        );
    }

    @GetMapping("/directors/{uuid}")
    @Operation(summary = "Get director by UUID")
    public ResponseEntity<ApiResponse<DirectorDto>> getDirectorByUuid(@PathVariable String uuid) {
        DirectorDto director = metadataService.getDirectorByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(director, "Director retrieved successfully")
        );
    }

    @GetMapping("/stars")
    @Operation(summary = "Get all stars")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<StarDto>>> getAllStars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<StarDto> pagedResponse = metadataService.getAllStars(page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Stars retrieved successfully")
        );
    }

    @GetMapping("/stars/{uuid}")
    @Operation(summary = "Get star by UUID")
    public ResponseEntity<ApiResponse<StarDto>> getStarByUuid(@PathVariable String uuid) {
        StarDto star = metadataService.getStarByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(star, "Star retrieved successfully")
        );
    }
}
