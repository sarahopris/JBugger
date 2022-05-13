package com.example.bugtracker.dto;

import com.example.bugtracker.model.Role;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserToEditDTO extends RepresentationModel<UserDTO> {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String username;
    private String password;
    private Short status;
    private String token;
    private List<Role> roles;
}
