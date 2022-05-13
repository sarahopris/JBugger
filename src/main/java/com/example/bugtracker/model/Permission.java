package com.example.bugtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idPermission;
    private String type;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "roles_permissions",
            joinColumns = @JoinColumn(name = "permissions_id_permission"),
            inverseJoinColumns = @JoinColumn(name = "role_id_role"))
    @JsonIgnore
    private List<Role> roles;
}
