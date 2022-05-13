package com.example.bugtracker.dto;

import com.example.bugtracker.model.Role;
import com.example.bugtracker.model.UserNotifications;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO extends RepresentationModel<UserDTO> {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String username;
    private String password;
    private Short status;

    private String token;
    private List<Role> roles;


    @JsonIgnore
    private List<UserNotifications> userNotificationsList = new ArrayList<>();

    public boolean isValid() {
        return this.isValidEmail() && this.isValidMobileNumber() && this.firstNameNotNull() && this.lastNameNotNull();
    }

    public boolean isValidEmail() {
        if (this.email == null) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@msg.group";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidMobileNumber() {
        if (this.mobileNumber== null) {
            return false;
        }
        String romania = "^([+]\\d{2})?\\d{9}$";
        Pattern patternR = Pattern.compile(romania);
        Matcher matcherR = patternR.matcher(mobileNumber);
        return matcherR.matches();
    }

    public boolean firstNameNotNull() {
        return this.firstName != null;
    }

    public boolean lastNameNotNull() {
        return this.lastName != null;
    }


}
