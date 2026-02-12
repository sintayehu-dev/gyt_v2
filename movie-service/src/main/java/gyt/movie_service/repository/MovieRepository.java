package gyt.movie_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gyt.movie_service.model.Director;
import gyt.movie_service.model.Genre;
import gyt.movie_service.model.Movie;
import gyt.movie_service.model.Star;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByUuid(String uuid);

    Page<Movie> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.isActive = true")
    Page<Movie> searchByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE LOWER(g.name) = LOWER(:genre) AND m.isActive = true")
    Page<Movie> findByGenre(@Param("genre") String genre, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.directors d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :director, '%')) AND m.isActive = true")
    Page<Movie> findByDirector(@Param("director") String director, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM Movie m JOIN m.genres g WHERE g = :genre AND m.isActive = true")
    boolean existsByGenre(@Param("genre") Genre genre);

    @Query("SELECT COUNT(m) > 0 FROM Movie m JOIN m.directors d WHERE d = :director AND m.isActive = true")
    boolean existsByDirector(@Param("director") Director director);

    @Query("SELECT COUNT(m) > 0 FROM Movie m JOIN m.stars s WHERE s = :star AND m.isActive = true")
    boolean existsByStar(@Param("star") Star star);
    
    @Query("SELECT DISTINCT m FROM Movie m " +
           "LEFT JOIN m.genres g " +
           "LEFT JOIN m.directors d " +
           "LEFT JOIN m.stars s " +
           "WHERE m.isActive = true " +
           "AND (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:description IS NULL OR LOWER(m.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
           "AND (:language IS NULL OR LOWER(m.language) = LOWER(:language)) " +
           "AND (:genre IS NULL OR LOWER(g.name) = LOWER(:genre)) " +
           "AND (:director IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :director, '%'))) " +
           "AND (:star IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :star, '%'))) " +
           "AND (:minDuration IS NULL OR m.durationMinutes >= :minDuration) " +
           "AND (:maxDuration IS NULL OR m.durationMinutes <= :maxDuration)")
    Page<Movie> searchMovies(
        @Param("title") String title,
        @Param("description") String description,
        @Param("language") String language,
        @Param("genre") String genre,
        @Param("director") String director,
        @Param("star") String star,
        @Param("minDuration") Integer minDuration,
        @Param("maxDuration") Integer maxDuration,
        Pageable pageable
    );
}
