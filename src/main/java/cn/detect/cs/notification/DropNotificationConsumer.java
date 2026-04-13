package cn.detect.cs.notification;

import cn.detect.cs.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DropNotificationConsumer {
    private final EmailService emailService;
    @RabbitListener(queues = "drop.notification.queue")
    public void handle(DropNotificationMessage msg) {
        emailService.sendSimpleMail(msg.getStudentEmail(), "退课通知",
                String.format("同学 %s，您已成功退课《%s》。", msg.getStudentName(), msg.getCourseName()));
    }
}
