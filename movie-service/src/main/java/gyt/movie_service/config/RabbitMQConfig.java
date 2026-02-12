package gyt.movie_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RATING_EXCHANGE = "rating.exchange";
    public static final String MOVIE_RATED_QUEUE = "movie.rated.queue";
    public static final String MOVIE_RATED_ROUTING_KEY = "movie.rated";

    @Bean
    public TopicExchange ratingExchange() {
        return new TopicExchange(RATING_EXCHANGE);
    }

    @Bean
    public Queue movieRatedQueue() {
        return QueueBuilder.durable(MOVIE_RATED_QUEUE).build();
    }

    @Bean
    public Binding movieRatedBinding() {
        return BindingBuilder
                .bind(movieRatedQueue())
                .to(ratingExchange())
                .with(MOVIE_RATED_ROUTING_KEY);
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
