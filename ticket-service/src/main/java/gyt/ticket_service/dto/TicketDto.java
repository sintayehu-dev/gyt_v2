package gyt.ticket_service.dto;

import gyt.ticket_service.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private String uuid;
    private String userUuid;
    private String scheduleUuid;
    private String seatNumber;
    private BigDecimal price;
    private TicketStatus status;
    private LocalDateTime bookingTime;
    private LocalDateTime paymentTime;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private UserDto user;
    private ScheduleDto schedule;
}
