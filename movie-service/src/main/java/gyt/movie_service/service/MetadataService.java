package gyt.movie_service.service;

import gyt.movie_service.dto.*;

public interface MetadataService {

    GenreDto createGenre(GenreRequest request);

    ApiResponse.PagedResponse<GenreDto> getAllGenres(int page, int size);

    GenreDto updateGenre(String uuid, GenreRequest request);

    GenreDto patchGenre(String uuid, GenreRequest request);

    GenreDto getGenreByUuid(String uuid);

    void deleteGenre(String uuid);

    DirectorDto createDirector(DirectorRequest request);

    ApiResponse.PagedResponse<DirectorDto> getAllDirectors(int page, int size);

    DirectorDto updateDirector(String uuid, DirectorRequest request);

    DirectorDto patchDirector(String uuid, DirectorRequest request);

    DirectorDto getDirectorByUuid(String uuid);

    void deleteDirector(String uuid);

    StarDto createStar(StarRequest request);

    ApiResponse.PagedResponse<StarDto> getAllStars(int page, int size);

    StarDto updateStar(String uuid, StarRequest request);

    StarDto patchStar(String uuid, StarRequest request);

    StarDto getStarByUuid(String uuid);

    void deleteStar(String uuid);
}
