package gyt.schedule_service.dto;

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
    private String scheduleUuid;
    private String seatNumber;
    private BigDecimal price;
    private String status;
    private LocalDateTime bookingTime;
    private LocalDateTime paymentTime;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
