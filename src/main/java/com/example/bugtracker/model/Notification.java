package com.example.bugtracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idNotification;
    private String type;


    private String message;
    private String URL;

    @OneToMany(mappedBy = "notification",fetch = FetchType.EAGER)
    private List<UserNotifications> userNotificationsList = new ArrayList<>();

}
