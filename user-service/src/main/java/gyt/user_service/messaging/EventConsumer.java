package gyt.user_service.messaging;

import gyt.user_service.dto.UserRegisteredEvent;
import gyt.user_service.config.RabbitMQConfig;
import gyt.user_service.model.UserProfile;
import gyt.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final UserProfileRepository userProfileRepository;

    @RabbitListener(queues = RabbitMQConfig.USER_REGISTERED_QUEUE)
    @Transactional
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            log.info("Received UserRegisteredEvent: {}", event.getEmail());

            if (userProfileRepository.findByEmail(event.getEmail()).isPresent()) {
                log.warn("User profile already exists: {}", event.getEmail());
                return;
            }

            UserProfile profile = new UserProfile();
            profile.setUuid(event.getUuid());
            profile.setEmail(event.getEmail());
            profile.setFullName(event.getName());

            userProfileRepository.save(profile);
            log.info("Created user profile for: {}", event.getEmail());

        } catch (Exception e) {
            log.error("Failed to handle UserRegisteredEvent: {}", e.getMessage());
            throw e;
        }
    }
}
