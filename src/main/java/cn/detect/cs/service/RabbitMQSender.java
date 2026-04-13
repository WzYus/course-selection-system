package cn.detect.cs.service;

import cn.detect.cs.notification.DropNotificationMessage;
import cn.detect.cs.notification.EnrollNotificationMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        log.info("RabbitTemplate 实际使用的 MessageConverter: {}", rabbitTemplate.getMessageConverter());
    }

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public void sendEnrollNotification(EnrollNotificationMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.info("选课通知消息已发送至队列: {}", message);
    }

    public void sendDropNotification(DropNotificationMessage message) {
        rabbitTemplate.convertAndSend(exchange, "drop.notification.key", message);
    }
}