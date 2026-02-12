package gyt.ticket_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCancelledEvent implements Serializable {
    private String eventId;
    private String ticketUuid;
    private String scheduleUuid;
    private String userUuid;
    private Integer numberOfSeats;
    private LocalDateTime timestamp;
}
