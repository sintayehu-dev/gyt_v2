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
import gyt.movie_service.dto.DirectorDto;
import gyt.movie_service.dto.DirectorRequest;
import gyt.movie_service.dto.GenreDto;
import gyt.movie_service.dto.GenreRequest;
import gyt.movie_service.dto.StarDto;
import gyt.movie_service.dto.StarRequest;
import gyt.movie_service.exception.CustomRuntimeException;
import gyt.movie_service.mapper.DirectorMapper;
import gyt.movie_service.mapper.GenreMapper;
import gyt.movie_service.mapper.StarMapper;
import gyt.movie_service.model.Director;
import gyt.movie_service.model.Genre;
import gyt.movie_service.model.Star;
import gyt.movie_service.repository.DirectorRepository;
import gyt.movie_service.repository.GenreRepository;
import gyt.movie_service.repository.MovieRepository;
import gyt.movie_service.repository.StarRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final StarRepository starRepository;
    private final MovieRepository movieRepository;
    private final GenreMapper genreMapper;
    private final DirectorMapper directorMapper;
    private final StarMapper starMapper;

    @Override
    @Transactional
    public GenreDto createGenre(GenreRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new CustomRuntimeException("Genre name is required", HttpStatus.BAD_REQUEST);
        }

        if (genreRepository.existsByName(request.getName())) {
            throw new CustomRuntimeException("Genre already exists: " + request.getName(), HttpStatus.CONFLICT);
        }

        Genre genre = genreMapper.toEntity(request);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toDto(savedGenre);
    }

    @Override
    public ApiResponse.PagedResponse<GenreDto> getAllGenres(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Genre> genrePage = genreRepository.findAll(pageable);

        List<GenreDto> content = genrePage.getContent().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<GenreDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(genrePage.getNumber());
        response.setPageSize(genrePage.getSize());
        response.setTotalElements(genrePage.getTotalElements());
        response.setTotalPages(genrePage.getTotalPages());
        response.setLast(genrePage.isLast());
        return response;
    }

    @Override
    @Transactional
    public GenreDto updateGenre(String uuid, GenreRequest request) {
        Genre genre = genreRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Genre not found", HttpStatus.NOT_FOUND));

        if (request.getName() != null && !request.getName().equals(genre.getName())
                && genreRepository.existsByName(request.getName())) {
            throw new CustomRuntimeException("Genre name already exists", HttpStatus.CONFLICT);
        }

        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Override
    @Transactional
    public GenreDto patchGenre(String uuid, GenreRequest request) {
        Genre genre = genreRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Genre not found", HttpStatus.NOT_FOUND));

        if (request.getName() != null) {
            if (!request.getName().equals(genre.getName()) && genreRepository.existsByName(request.getName())) {
                throw new CustomRuntimeException("Genre name already exists", HttpStatus.CONFLICT);
            }
            genre.setName(request.getName());
        }
        if (request.getDescription() != null) {
            genre.setDescription(request.getDescription());
        }

        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Override
    @Transactional
    public void deleteGenre(String uuid) {
        Genre genre = genreRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Genre not found", HttpStatus.NOT_FOUND));

        if (movieRepository.existsByGenre(genre)) {
            throw new CustomRuntimeException("Cannot delete genre because it is associated with movies",
                    HttpStatus.CONFLICT);
        }

        genreRepository.delete(genre);
    }

    @Override
    public GenreDto getGenreByUuid(String uuid) {
        Genre genre = genreRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Genre not found", HttpStatus.NOT_FOUND));
        return genreMapper.toDto(genre);
    }

    @Override
    @Transactional
    public DirectorDto createDirector(DirectorRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new CustomRuntimeException("Director name is required", HttpStatus.BAD_REQUEST);
        }

        if (directorRepository.existsByName(request.getName())) {
            throw new CustomRuntimeException("Director already exists: " + request.getName(), HttpStatus.CONFLICT);
        }

        Director director = directorMapper.toEntity(request);
        Director savedDirector = directorRepository.save(director);
        return directorMapper.toDto(savedDirector);
    }

    @Override
    public ApiResponse.PagedResponse<DirectorDto> getAllDirectors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Director> directorPage = directorRepository.findAll(pageable);

        List<DirectorDto> content = directorPage.getContent().stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<DirectorDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(directorPage.getNumber());
        response.setPageSize(directorPage.getSize());
        response.setTotalElements(directorPage.getTotalElements());
        response.setTotalPages(directorPage.getTotalPages());
        response.setLast(directorPage.isLast());
        return response;
    }

    @Override
    @Transactional
    public DirectorDto updateDirector(String uuid, DirectorRequest request) {
        Director director = directorRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Director not found", HttpStatus.NOT_FOUND));

        if (request.getName() != null && !request.getName().equals(director.getName())
                && directorRepository.existsByName(request.getName())) {
            throw new CustomRuntimeException("Director name already exists", HttpStatus.CONFLICT);
        }

        director.setName(request.getName());
        director.setBiography(request.getBiography());
        return directorMapper.toDto(directorRepository.save(director));
    }

    @Override
    @Transactional
    public DirectorDto patchDirector(String uuid, DirectorRequest request) {
        Director director = directorRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Director not found", HttpStatus.NOT_FOUND));

        if (request.getName() != null) {
            if (!request.getName().equals(director.getName()) && directorRepository.existsByName(request.getName())) {
                throw new CustomRuntimeException("Director name already exists", HttpStatus.CONFLICT);
            }
            director.setName(request.getName());
        }
        if (request.getBiography() != null) {
            director.setBiography(request.getBiography());
        }

        return directorMapper.toDto(directorRepository.save(director));
    }

    @Override
    @Transactional
    public void deleteDirector(String uuid) {
        Director director = directorRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Director not found", HttpStatus.NOT_FOUND));

        if (movieRepository.existsByDirector(director)) {
            throw new CustomRuntimeException("Cannot delete director because it is associated with movies",
                    HttpStatus.CONFLICT);
        }

        directorRepository.delete(director);
    }

    @Override
    public DirectorDto getDirectorByUuid(String uuid) {
        Director director = directorRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Director not found", HttpStatus.NOT_FOUND));
        return directorMapper.toDto(director);
    }

    @Override
    @Transactional
    public StarDto createStar(StarRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new CustomRuntimeException("Star name is required", HttpStatus.BAD_REQUEST);
        }

        if (starRepository.existsByName(request.getName())) {
            throw new CustomRuntimeException("Star already exists: " + request.getName(), HttpStatus.CONFLICT);
        }

        Star star = starMapper.toEntity(request);
        Star savedStar = starRepository.save(star);
        return starMapper.toDto(savedStar);
    }

    @Override
    public ApiResponse.PagedResponse<StarDto> getAllStars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Star> starPage = starRepository.findAll(pageable);

        List<StarDto> content = starPage.getContent().stream()
                .map(starMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<StarDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(starPage.getNumber());
        response.setPageSize(starPage.getSize());
        response.setTotalElements(starPage.getTotalElements());
        response.setTotalPages(starPage.getTotalPages());
        response.setLast(starPage.isLast());
        return response;
    }

    @Override
    @Transactional
    public StarDto updateStar(String uuid, StarRequest request) {
        Star star = starRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Star not found", HttpStatus.NOT_FOUND));

        if (request.getName() != null && !request.getName().equals(star.getName())
                && starRepository.existsByName(request.getName())) {
            throw new CustomRuntimeException("Star name already exists", HttpStatus.CONFLICT);
        }

        star.setName(request.getName());
        star.setBiography(request.getBiography());
        return starMapper.toDto(starRepository.save(star));
    }

    @Override
    @Transactional
    public StarDto patchStar(String uuid, StarRequest request) {
        Star star = starRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Star not found", HttpStatus.NOT_FOUND));

        if (request.getName() != null) {
            if (!request.getName().equals(star.getName()) && starRepository.existsByName(request.getName())) {
                throw new CustomRuntimeException("Star name already exists", HttpStatus.CONFLICT);
            }
            star.setName(request.getName());
        }
        if (request.getBiography() != null) {
            star.setBiography(request.getBiography());
        }

        return starMapper.toDto(starRepository.save(star));
    }

    @Override
    @Transactional
    public void deleteStar(String uuid) {
        Star star = starRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Star not found", HttpStatus.NOT_FOUND));

        if (movieRepository.existsByStar(star)) {
            throw new CustomRuntimeException("Cannot delete star because it is associated with movies",
                    HttpStatus.CONFLICT);
        }

        starRepository.delete(star);
    }

    @Override
    public StarDto getStarByUuid(String uuid) {
        Star star = starRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Star not found", HttpStatus.NOT_FOUND));
        return starMapper.toDto(star);
    }
}
