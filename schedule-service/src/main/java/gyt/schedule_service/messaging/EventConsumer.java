package gyt.schedule_service.messaging;

import gyt.schedule_service.config.RabbitMQConfig;
import gyt.schedule_service.dto.TicketBookedEvent;
import gyt.schedule_service.dto.TicketCancelledEvent;
import gyt.schedule_service.model.Schedule;
import gyt.schedule_service.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final ScheduleRepository scheduleRepository;

    @RabbitListener(queues = RabbitMQConfig.TICKET_BOOKED_QUEUE)
    @Transactional
    public void handleTicketBookedEvent(TicketBookedEvent event) {
        try {
            log.info("Received TicketBookedEvent for schedule: {}", event.getScheduleUuid());

            Schedule schedule = scheduleRepository.findByUuid(event.getScheduleUuid())
                    .orElseThrow(() -> new RuntimeException("Schedule not found: " + event.getScheduleUuid()));

            int newAvailableSeats = schedule.getAvailableSeats() - event.getNumberOfSeats();

            if (newAvailableSeats < 0) {
                log.error("Not enough seats available for schedule: {}", event.getScheduleUuid());
                throw new RuntimeException("Not enough seats available");
            }

            schedule.setAvailableSeats(newAvailableSeats);
            scheduleRepository.save(schedule);

            log.info("Successfully reduced {} seats for schedule {}. New available seats: {}",
                    event.getNumberOfSeats(), event.getScheduleUuid(), newAvailableSeats);

        } catch (Exception e) {
            log.error("Failed to handle TicketBookedEvent: {}", e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TICKET_CANCELLED_QUEUE)
    @Transactional
    public void handleTicketCancelledEvent(TicketCancelledEvent event) {
        try {
            log.info("Received TicketCancelledEvent for schedule: {}", event.getScheduleUuid());

            Schedule schedule = scheduleRepository.findByUuid(event.getScheduleUuid())
                    .orElseThrow(() -> new RuntimeException("Schedule not found: " + event.getScheduleUuid()));

            int newAvailableSeats = schedule.getAvailableSeats() + event.getNumberOfSeats();

            if (newAvailableSeats > schedule.getTotalSeats()) {
                log.warn("Restored seats exceed total seats for schedule: {}. Capping at total seats.",
                        event.getScheduleUuid());
                newAvailableSeats = schedule.getTotalSeats();
            }

            schedule.setAvailableSeats(newAvailableSeats);
            scheduleRepository.save(schedule);

            log.info("Successfully restored {} seats for schedule {}. New available seats: {}",
                    event.getNumberOfSeats(), event.getScheduleUuid(), newAvailableSeats);

        } catch (Exception e) {
            log.error("Failed to handle TicketCancelledEvent: {}", e.getMessage());
            throw e;
        }
    }
}
