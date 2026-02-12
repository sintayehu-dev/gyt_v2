package gyt.user_service.messaging;

import gyt.user_service.config.RabbitMQConfig;
import gyt.user_service.dto.MovieRatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishMovieRatedEvent(MovieRatedEvent event) {
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(LocalDateTime.now());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.RATING_EXCHANGE,
                    RabbitMQConfig.MOVIE_RATED_ROUTING_KEY,
                    event);
            log.info("Published MovieRatedEvent for movie: {}", event.getMovieUuid());
        } catch (Exception e) {
            log.error("Failed to publish MovieRatedEvent: {}", e.getMessage());
        }
    }

    public void publishUserProfileUpdatedEvent(gyt.user_service.dto.UserProfileUpdatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_PROFILE_UPDATED_EXCHANGE,
                    RabbitMQConfig.USER_PROFILE_UPDATED_ROUTING_KEY,
                    event);
            log.info("Published UserProfileUpdatedEvent for user: {}", event.getUserUuid());
        } catch (Exception e) {
            log.error("Failed to publish UserProfileUpdatedEvent: {}", e.getMessage());
        }
    }

    public void publishUserDeletedEvent(gyt.user_service.dto.UserDeletedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_DELETED_EXCHANGE,
                    RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                    event);
            log.info("Published UserDeletedEvent for user: {}", event.getUserUuid());
        } catch (Exception e) {
            log.error("Failed to publish UserDeletedEvent: {}", e.getMessage());
        }
    }
}
