package gyt.user_service.repository;

import gyt.user_service.model.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    List<UserRating> findByUserEmail(String email);

    List<UserRating> findByUserId(Long userId);
}
