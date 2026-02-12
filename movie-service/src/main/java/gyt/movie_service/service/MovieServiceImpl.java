package gyt.movie_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gyt.movie_service.dto.ApiResponse;
import gyt.movie_service.dto.MovieCreateRequest;
import gyt.movie_service.dto.MovieDto;
import gyt.movie_service.exception.CustomRuntimeException;
import gyt.movie_service.mapper.MovieMapper;
import gyt.movie_service.model.Movie;
import gyt.movie_service.repository.DirectorRepository;
import gyt.movie_service.repository.GenreRepository;
import gyt.movie_service.repository.MovieRepository;
import gyt.movie_service.repository.StarRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final StarRepository starRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public ApiResponse.PagedResponse<MovieDto> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepository.findByIsActiveTrue(pageable);

        List<MovieDto> content = moviePage.getContent().stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<MovieDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(moviePage.getNumber());
        response.setPageSize(moviePage.getSize());
        response.setTotalElements(moviePage.getTotalElements());
        response.setTotalPages(moviePage.getTotalPages());
        response.setLast(moviePage.isLast());
        return response;
    }

    @Override
    public MovieDto getMovieByUuid(String uuid) {
        Movie movie = movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        "Movie not found with uuid: " + uuid, HttpStatus.NOT_FOUND));

        if (!movie.getIsActive()) {
            throw new CustomRuntimeException("Movie is not active", HttpStatus.NOT_FOUND);
        }

        return movieMapper.toDto(movie);
    }

    @Override
    public ApiResponse.PagedResponse<MovieDto> searchMoviesByTitle(String title, int page, int size) {
        if (title == null || title.trim().isEmpty()) {
            throw new CustomRuntimeException("Search title cannot be empty", HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepository.searchByTitle(title, pageable);

        List<MovieDto> content = moviePage.getContent().stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<MovieDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(moviePage.getNumber());
        response.setPageSize(moviePage.getSize());
        response.setTotalElements(moviePage.getTotalElements());
        response.setTotalPages(moviePage.getTotalPages());
        response.setLast(moviePage.isLast());
        return response;
    }

    @Override
    public ApiResponse.PagedResponse<MovieDto> filterMovies(String genre, String director, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage;

        if (genre != null && !genre.trim().isEmpty()) {
            moviePage = movieRepository.findByGenre(genre, pageable);
        } else if (director != null && !director.trim().isEmpty()) {
            moviePage = movieRepository.findByDirector(director, pageable);
        } else {
            throw new CustomRuntimeException(
                    "At least one filter parameter (genre or director) is required",
                    HttpStatus.BAD_REQUEST);
        }

        List<MovieDto> content = moviePage.getContent().stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<MovieDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(moviePage.getNumber());
        response.setPageSize(moviePage.getSize());
        response.setTotalElements(moviePage.getTotalElements());
        response.setTotalPages(moviePage.getTotalPages());
        response.setLast(moviePage.isLast());
        return response;
    }

    @Override
    public ApiResponse.PagedResponse<MovieDto> advancedSearch(
            String title,
            String description,
            String language,
            String genre,
            String director,
            String star,
            Integer minDuration,
            Integer maxDuration,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepository.searchMovies(
                title, description, language, genre, director, star,
                minDuration, maxDuration, pageable);

        List<MovieDto> content = moviePage.getContent().stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<MovieDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(moviePage.getNumber());
        response.setPageSize(moviePage.getSize());
        response.setTotalElements(moviePage.getTotalElements());
        response.setTotalPages(moviePage.getTotalPages());
        response.setLast(moviePage.isLast());
        return response;
    }

    @Override
    @Transactional
    public MovieDto createMovie(MovieCreateRequest request, org.springframework.web.multipart.MultipartFile poster) {
        validateMovieRequest(request);

        String posterUrl = cloudinaryService.uploadImage(poster, "cinemas/movies/posters");

        Movie movie = movieMapper.toEntity(request);
        movie.setPosterUrl(posterUrl);

        if (request.getGenreUuids() != null && !request.getGenreUuids().isEmpty()) {
            movie.setGenres(request.getGenreUuids().stream()
                    .map(uuid -> genreRepository.findByUuid(uuid)
                            .orElseThrow(() -> new CustomRuntimeException(
                                    "Genre not found with uuid: " + uuid, HttpStatus.NOT_FOUND)))
                    .collect(Collectors.toList()));
        }

        if (request.getDirectorUuids() != null && !request.getDirectorUuids().isEmpty()) {
            movie.setDirectors(request.getDirectorUuids().stream()
                    .map(uuid -> directorRepository.findByUuid(uuid)
                            .orElseThrow(() -> new CustomRuntimeException(
                                    "Director not found with uuid: " + uuid, HttpStatus.NOT_FOUND)))
                    .collect(Collectors.toList()));
        }

        if (request.getStarUuids() != null && !request.getStarUuids().isEmpty()) {
            movie.setStars(request.getStarUuids().stream()
                    .map(uuid -> starRepository.findByUuid(uuid)
                            .orElseThrow(() -> new CustomRuntimeException(
                                    "Star not found with uuid: " + uuid, HttpStatus.NOT_FOUND)))
                    .collect(Collectors.toList()));
        }

        Movie savedMovie = movieRepository.save(movie);

        return movieMapper.toDto(savedMovie);
    }

    @Override
    @Transactional
    public MovieDto updateMovie(String uuid, MovieCreateRequest request) {
        Movie movie = movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        "Movie not found with uuid: " + uuid, HttpStatus.NOT_FOUND));

        validateMovieRequest(request);

        if (request.getTitle() != null) {
            movie.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            movie.setDescription(request.getDescription());
        }
        if (request.getDurationMinutes() != null) {
            movie.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getReleaseDate() != null) {
            movie.setReleaseDate(request.getReleaseDate());
        }
        if (request.getTrailerUrl() != null) {
            movie.setTrailerUrl(request.getTrailerUrl());
        }
        if (request.getLanguage() != null) {
            movie.setLanguage(request.getLanguage());
        }
        if (request.getGenreUuids() != null && !request.getGenreUuids().isEmpty()) {
            movie.setGenres(request.getGenreUuids().stream()
                    .map(genreUuid -> genreRepository.findByUuid(genreUuid)
                            .orElseThrow(() -> new CustomRuntimeException(
                                    "Genre not found with uuid: " + genreUuid, HttpStatus.NOT_FOUND)))
                    .collect(Collectors.toList()));
        }
        if (request.getDirectorUuids() != null && !request.getDirectorUuids().isEmpty()) {
            movie.setDirectors(request.getDirectorUuids().stream()
                    .map(directorUuid -> directorRepository.findByUuid(directorUuid)
                            .orElseThrow(() -> new CustomRuntimeException(
                                    "Director not found with uuid: " + directorUuid, HttpStatus.NOT_FOUND)))
                    .collect(Collectors.toList()));
        }
        if (request.getStarUuids() != null && !request.getStarUuids().isEmpty()) {
            movie.setStars(request.getStarUuids().stream()
                    .map(starUuid -> starRepository.findByUuid(starUuid)
                            .orElseThrow(() -> new CustomRuntimeException(
                                    "Star not found with uuid: " + starUuid, HttpStatus.NOT_FOUND)))
                    .collect(Collectors.toList()));
        }

        Movie updatedMovie = movieRepository.save(movie);
        return movieMapper.toDto(updatedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(String uuid) {
        Movie movie = movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        "Movie not found with uuid: " + uuid, HttpStatus.NOT_FOUND));

        movie.getGenres().clear();
        movie.getDirectors().clear();
        movie.getStars().clear();
        
        movie.setIsActive(false);
        movieRepository.save(movie);
    }

    @Override
    public boolean movieExists(String uuid) {
        return movieRepository.findByUuid(uuid)
                .map(Movie::getIsActive)
                .orElse(false);
    }

    private void validateMovieRequest(MovieCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new CustomRuntimeException("Movie title is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getDurationMinutes() != null && request.getDurationMinutes() <= 0) {
            throw new CustomRuntimeException("Duration must be positive", HttpStatus.BAD_REQUEST);
        }
    }
}
