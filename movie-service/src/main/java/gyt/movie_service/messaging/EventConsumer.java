package gyt.movie_service.messaging;

import gyt.movie_service.config.RabbitMQConfig;
import gyt.movie_service.dto.MovieRatedEvent;
import gyt.movie_service.model.Movie;
import gyt.movie_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final MovieRepository movieRepository;

    @RabbitListener(queues = RabbitMQConfig.MOVIE_RATED_QUEUE)
    @Transactional
    public void handleMovieRatedEvent(MovieRatedEvent event) {
        try {
            log.info("Received MovieRatedEvent for movie: {}", event.getMovieUuid());

            Movie movie = movieRepository.findByUuid(event.getMovieUuid())
                    .orElseThrow(() -> new RuntimeException("Movie not found: " + event.getMovieUuid()));

            int currentCount = movie.getRatingCount() != null ? movie.getRatingCount() : 0;
            BigDecimal currentAverage = movie.getAverageRating() != null ? movie.getAverageRating() : BigDecimal.ZERO;

            BigDecimal totalRating = currentAverage.multiply(BigDecimal.valueOf(currentCount))
                    .add(BigDecimal.valueOf(event.getRating()));
            int newCount = currentCount + 1;
            BigDecimal newAverage = totalRating.divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP);

            movie.setRatingCount(newCount);
            movie.setAverageRating(newAverage);
            movieRepository.save(movie);

            log.info("Updated movie {} ratings: count={}, average={}", 
                    event.getMovieUuid(), newCount, newAverage);

        } catch (Exception e) {
            log.error("Failed to handle MovieRatedEvent: {}", e.getMessage());
            throw e;
        }
    }
}
