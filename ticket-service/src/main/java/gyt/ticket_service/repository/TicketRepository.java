package gyt.ticket_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gyt.ticket_service.model.Ticket;
import gyt.ticket_service.model.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    Optional<Ticket> findByUuid(String uuid);
    
    Page<Ticket> findByUserUuid(String userUuid, Pageable pageable);
    
    Page<Ticket> findByScheduleUuid(String scheduleUuid, Pageable pageable);
    
    List<Ticket> findByScheduleUuidAndSeatNumber(String scheduleUuid, String seatNumber);
    
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    
    Optional<Ticket> findByUuidAndUserUuid(String uuid, String userUuid);
    
    List<Ticket> findByUserUuidAndStatus(String userUuid, TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:userUuid IS NULL OR t.userUuid = :userUuid) " +
           "AND (:scheduleUuid IS NULL OR t.scheduleUuid = :scheduleUuid) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:seatNumber IS NULL OR LOWER(t.seatNumber) LIKE LOWER(CONCAT('%', :seatNumber, '%'))) " +
           "AND (:minPrice IS NULL OR t.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR t.price <= :maxPrice)")
    Page<Ticket> searchTickets(
        @Param("userUuid") String userUuid,
        @Param("scheduleUuid") String scheduleUuid,
        @Param("status") TicketStatus status,
        @Param("seatNumber") String seatNumber,
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice,
        Pageable pageable
    );
}
