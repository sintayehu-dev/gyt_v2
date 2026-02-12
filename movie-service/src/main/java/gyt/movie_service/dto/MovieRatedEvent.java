package gyt.movie_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRatedEvent implements Serializable {
    private String eventId;
    private String ratingUuid;
    private String movieUuid;
    private String userUuid;
    private Integer rating;
    private String review;
    private LocalDateTime timestamp;
}
