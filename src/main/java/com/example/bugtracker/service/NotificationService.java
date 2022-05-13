package com.example.bugtracker.service;

import com.example.bugtracker.Repository.INotificationRepository;
import com.example.bugtracker.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private INotificationRepository iNotificationRepository;

    public Notification findByType(String type) {
        return iNotificationRepository.findByType(type);
    }
}
