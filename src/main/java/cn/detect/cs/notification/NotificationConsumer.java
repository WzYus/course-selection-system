package cn.detect.cs.notification;

import cn.detect.cs.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleEnrollNotification(EnrollNotificationMessage message) {
        log.info("收到选课通知，准备发送邮件给: {}", message.getStudentEmail());
        String subject = "选课成功通知";
        String text = String.format("同学 %s，您已成功选课《%s》。请按时上课。",
                message.getStudentName(), message.getCourseName());
        emailService.sendSimpleMail(message.getStudentEmail(), subject, text);
    }
}