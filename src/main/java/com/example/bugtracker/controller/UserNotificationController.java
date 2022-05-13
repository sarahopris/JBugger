package com.example.bugtracker.controller;

import com.example.bugtracker.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserNotificationController {
    @Autowired
    private UserNotificationService userNotificationService;

    @GetMapping(value = "/automaticDeleteAfterThirtyDays")
    public void automaticDeleteAfterThirtyDays() {
        userNotificationService.automaticDeleteAfterThirtyDays();
    }
}
