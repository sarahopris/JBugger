package com.example.bugtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idUser;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String mobileNumber;
    @NotNull
    private String email;

    @Column(unique = true)
    private String username;
    private String password;
    private Short status;
    private String token;

    @OneToMany(mappedBy = "createdBy")
    List<Bug> bugsCreated;


    @OneToMany(mappedBy = "assignedTo")
    List<Bug> bugsAssigned;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "users_id_user"),
            inverseJoinColumns = @JoinColumn(name = "roles_id_role"))
    private List<Role> roles;



    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserNotifications> userNotificationsList = new ArrayList<>();


    public boolean isValidEmail() {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@msg.group";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidMobileNumber() {
        String romania="^([+]\\d{2})?\\d{9}$";
        Pattern patternR = Pattern.compile(romania);
        Matcher matcherR = patternR.matcher(mobileNumber);
        return matcherR.matches()  ;
    }

    public boolean isValid() {
        return firstName != null && lastName != null && mobileNumber!=null && email!= null && isValidEmail() && isValidMobileNumber();
    }
}
