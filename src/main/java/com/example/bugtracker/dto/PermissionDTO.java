package com.example.bugtracker.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionDTO {
    private String type;
    private String description;

}
