package gyt.schedule_service.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gyt.schedule_service.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    Optional<Schedule> findByUuid(String uuid);
    
    Page<Schedule> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT s FROM Schedule s WHERE s.movieUuid = :movieUuid AND s.isActive = true")
    Page<Schedule> findByMovieUuid(@Param("movieUuid") String movieUuid, Pageable pageable);
    
    @Query("SELECT s FROM Schedule s WHERE s.showDate = :date AND s.isActive = true")
    Page<Schedule> findByShowDate(@Param("date") LocalDate date, Pageable pageable);
    
    @Query("SELECT s FROM Schedule s WHERE s.cinemaHall = :hall AND s.isActive = true")
    Page<Schedule> findByCinemaHall(@Param("hall") String hall, Pageable pageable);
    
    @Query("SELECT s FROM Schedule s WHERE s.isActive = true " +
           "AND (:movieUuid IS NULL OR s.movieUuid = :movieUuid) " +
           "AND (:cinemaHall IS NULL OR LOWER(s.cinemaHall) LIKE LOWER(CONCAT('%', :cinemaHall, '%'))) " +
           "AND (:startDate IS NULL OR s.showDate >= :startDate) " +
           "AND (:endDate IS NULL OR s.showDate <= :endDate) " +
           "AND (:minPrice IS NULL OR s.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR s.price <= :maxPrice)")
    Page<Schedule> searchSchedules(
        @Param("movieUuid") String movieUuid,
        @Param("cinemaHall") String cinemaHall,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice,
        Pageable pageable
    );
}
