package gyt.movie_service.repository;

import gyt.movie_service.model.Director;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> findByUuid(String uuid);

    Optional<Director> findByName(String name);

    boolean existsByName(String name);

    Page<Director> findAll(Pageable pageable);
}
