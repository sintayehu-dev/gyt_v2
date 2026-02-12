package gyt.auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "user.exchange";
    public static final String USER_REGISTERED_QUEUE = "user.registered.queue";
    public static final String USER_REGISTERED_ROUTING_KEY = "user.registered";

    public static final String USER_PROFILE_UPDATED_EXCHANGE = "user.profile.updated.exchange";
    public static final String USER_PROFILE_UPDATED_QUEUE = "user.profile.updated.queue";
    public static final String USER_PROFILE_UPDATED_ROUTING_KEY = "user.profile.updated";

    public static final String USER_DELETED_EXCHANGE = "user.deleted.exchange";
    public static final String USER_DELETED_QUEUE = "user.deleted.queue";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable(USER_REGISTERED_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder
                .bind(userRegisteredQueue())
                .to(userExchange())
                .with(USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public TopicExchange userProfileUpdatedExchange() {
        return new TopicExchange(USER_PROFILE_UPDATED_EXCHANGE);
    }

    @Bean
    public Queue userProfileUpdatedQueue() {
        return QueueBuilder.durable(USER_PROFILE_UPDATED_QUEUE).build();
    }

    @Bean
    public Binding userProfileUpdatedBinding() {
        return BindingBuilder
                .bind(userProfileUpdatedQueue())
                .to(userProfileUpdatedExchange())
                .with(USER_PROFILE_UPDATED_ROUTING_KEY);
    }

    @Bean
    public TopicExchange userDeletedExchange() {
        return new TopicExchange(USER_DELETED_EXCHANGE);
    }

    @Bean
    public Queue userDeletedQueue() {
        return QueueBuilder.durable(USER_DELETED_QUEUE).build();
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder
                .bind(userDeletedQueue())
                .to(userDeletedExchange())
                .with(USER_DELETED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
