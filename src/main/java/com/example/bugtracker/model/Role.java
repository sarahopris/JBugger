package com.example.bugtracker.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idRole;
    private String type;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id_role"),
            inverseJoinColumns = @JoinColumn(name = "permissions_id_permission"))
    private List<Permission> permissions;
}
