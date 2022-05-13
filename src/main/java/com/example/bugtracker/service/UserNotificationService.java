package com.example.bugtracker.service;

import com.example.bugtracker.Repository.IUserNotificationRepository;
import com.example.bugtracker.model.UserNotifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserNotificationService {
    @Autowired
    private IUserNotificationRepository iUserNotificationRepository;

    public void automaticDeleteAfterThirtyDays() {
        List<UserNotifications> userNotificationsList = iUserNotificationRepository.findAll();
        userNotificationsList.forEach(userNotification -> {
            long milliseconds = Timestamp.valueOf(LocalDateTime.now()).getTime()-userNotification.getDate().getTime();
            int seconds = (int) milliseconds / 1000;
            int hours = seconds/3600;

            int days = hours / 24;
            if (days>=30) {
                // delete the notification
                iUserNotificationRepository.delete(userNotification);
            }

//            if (seconds>=30) {
//                // delete the notification
//                iUserNotificationRepository.delete(userNotification);
//            }
        });
    }
}
