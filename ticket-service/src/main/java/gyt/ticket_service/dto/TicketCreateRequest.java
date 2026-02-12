package gyt.ticket_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateRequest {
    private String scheduleUuid;
    private String seatNumber;
    private BigDecimal price;
}
