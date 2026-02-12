package gyt.ticket_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private String uuid;
    private String movieUuid;
    private String cinemaHall;
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalTime endTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal price;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean fallbackUsed;
    
    private MovieDto movie;
}
