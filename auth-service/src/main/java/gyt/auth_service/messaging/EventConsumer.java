package gyt.auth_service.messaging;

import gyt.auth_service.dto.UserDeletedEvent;
import gyt.auth_service.dto.UserProfileUpdatedEvent;
import gyt.auth_service.model.User;
import gyt.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final UserRepository userRepository;

    @RabbitListener(queues = "user.profile.updated.queue")
    public void handleUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("Received user profile update event for user: {}", event.getUserUuid());

        try {
            User user = userRepository.findByUuid(event.getUserUuid())
                    .orElseThrow(() -> new RuntimeException("User not found: " + event.getUserUuid()));

            if (event.getEmail() != null) {
                user.setEmail(event.getEmail());
            }
            if (event.getFullName() != null) {
                user.setName(event.getFullName());
            }

            userRepository.save(user);
            log.info("Successfully updated user profile in auth service: {}", event.getUserUuid());
        } catch (Exception e) {
            log.error("Error processing user profile update event: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "user.deleted.queue")
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Received user deleted event for user: {}", event.getUserUuid());

        try {
            User user = userRepository.findByUuid(event.getUserUuid())
                    .orElse(null);

            if (user != null) {
                userRepository.delete(user);
                log.info("Successfully deleted user from auth service: {}", event.getUserUuid());
            } else {
                log.warn("User not found in auth service, may have been already deleted: {}", event.getUserUuid());
            }
        } catch (Exception e) {
            log.error("Error processing user deleted event: {}", e.getMessage(), e);
        }
    }
}
