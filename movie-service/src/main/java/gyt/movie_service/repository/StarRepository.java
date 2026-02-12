package gyt.movie_service.repository;

import gyt.movie_service.model.Star;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
    Optional<Star> findByUuid(String uuid);

    Optional<Star> findByName(String name);

    boolean existsByName(String name);

    Page<Star> findAll(Pageable pageable);
}
