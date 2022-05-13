package com.example.bugtracker.security;

import com.example.bugtracker.dto.LoginData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class LoginController {
    @Autowired
    UserSecurityService userSecurityService;

    @PostMapping("user/login")
    public ResponseEntity<?> login(@RequestBody LoginData loginData) throws Exception {
        return userSecurityService.login(loginData);
    }

    @PostMapping("user/logout")
    public ResponseEntity<?> logout(@RequestBody String username) throws Exception{
        return userSecurityService.logout(username);
    }
}

