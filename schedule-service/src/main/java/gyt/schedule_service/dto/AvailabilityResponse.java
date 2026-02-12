package gyt.schedule_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    private String scheduleUuid;
    private Integer availableSeats;
    private Integer totalSeats;
    private Boolean isAvailable;
    private Boolean fallbackUsed;
}
