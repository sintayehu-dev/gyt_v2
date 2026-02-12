package gyt.movie_service.service;

import gyt.movie_service.dto.ApiResponse;
import gyt.movie_service.dto.MovieCreateRequest;
import gyt.movie_service.dto.MovieDto;

public interface MovieService {

    ApiResponse.PagedResponse<MovieDto> getAllMovies(int page, int size);

    MovieDto getMovieByUuid(String uuid);

    ApiResponse.PagedResponse<MovieDto> searchMoviesByTitle(String title, int page, int size);

    ApiResponse.PagedResponse<MovieDto> filterMovies(String genre, String director, int page, int size);

    ApiResponse.PagedResponse<MovieDto> advancedSearch(
        String title, 
        String description, 
        String language, 
        String genre, 
        String director, 
        String star, 
        Integer minDuration, 
        Integer maxDuration, 
        int page, 
        int size
    );

    MovieDto createMovie(MovieCreateRequest request, org.springframework.web.multipart.MultipartFile poster);

    MovieDto updateMovie(String uuid, MovieCreateRequest request);

    void deleteMovie(String uuid);

    boolean movieExists(String uuid);
}
