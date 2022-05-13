package com.example.bugtracker.dto;

import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserNotificationDTO {
    private String message;
    private String type;
    private Boolean read;
    private Timestamp date;
}

