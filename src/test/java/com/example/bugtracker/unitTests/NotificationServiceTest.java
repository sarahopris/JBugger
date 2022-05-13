package com.example.bugtracker.unitTests;

import com.example.bugtracker.Repository.INotificationRepository;
import com.example.bugtracker.dto.UserNotificationDTO;
import com.example.bugtracker.model.Notification;
import com.example.bugtracker.service.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotificationServiceTest {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private INotificationRepository iNotificationRepository;

    @Test
    void findByType() {
        Notification notification=Notification.builder()
                .URL("v")
                .type("type")
                .message("message")
                .build();
        iNotificationRepository.save(notification);
        Notification result=notificationService.findByType(notification.getType());
        Assertions.assertNotNull(result);
        iNotificationRepository.delete(notification);


    }
}