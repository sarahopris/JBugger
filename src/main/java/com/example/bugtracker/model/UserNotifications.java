package com.example.bugtracker.model;
import lombok.Data;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "users_notifications")
public class UserNotifications {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Notification notification;

    private Timestamp date;
    private boolean read;


    @Column(columnDefinition = "TEXT")
    private String message;


}
