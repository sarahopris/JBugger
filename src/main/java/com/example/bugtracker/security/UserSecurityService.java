package com.example.bugtracker.security;

import com.example.bugtracker.Repository.IUserRepository;
import com.example.bugtracker.dto.LoginData;
import com.example.bugtracker.model.User;
import com.example.bugtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Service
public class UserSecurityService{
    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncryption passwordEncryption;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(LoginData loginData) throws Exception {

        User user = iUserRepository.findByUsername(loginData.getUsername());
        if (user != null && user.getStatus()!=0) {
            if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
                // the password is not correct
                return new ResponseEntity<>("Bad password!", HttpStatus.CONFLICT);
            }
            else {
                String token = getJWTToken(loginData.getUsername());
                user.setToken(token);
                iUserRepository.save(user);
                return new ResponseEntity<>(userService.convertToUserDTO(user), HttpStatus.OK);

            }
        }
        else {
            return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
        }
    }

    private String getJWTToken(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }



    public ResponseEntity<?> logout(String username) throws Exception{

        User user = iUserRepository.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }

        if (user.getToken() == null) {
            return new ResponseEntity<>(userService.convertToUserDTO(user), HttpStatus.OK);
        }

        user.setToken(null);
        iUserRepository.save(user);
        System.out.println("logout in service");
        return new ResponseEntity<>(userService.convertToUserDTO(user), HttpStatus.OK);
    }
}
