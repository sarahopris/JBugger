package com.example.bugtracker.dto;

import com.example.bugtracker.model.Permission;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO {
    private String type;
    private List<Permission> permissions;

}
