package gyt.schedule_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private String uuid;
    private MovieDto movie;
    private String cinemaHall;
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalTime endTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal price;
    private Boolean isActive;
    private Boolean fallbackUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
