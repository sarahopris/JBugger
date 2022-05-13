package com.example.bugtracker;

import com.example.bugtracker.security.JWTAuthorizationFilter;
import com.example.bugtracker.service.FileStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Properties;


@SpringBootApplication
public class BugTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BugTrackerApplication.class, args);
    }

    /**
     * we make here all the configurations of the project
     * the configure method is used when a user is logged out or logged in
     * the getJavaMailSender and the templateSimpleMessage methods are used when we send an email
     */
    @EnableWebSecurity
    @Configuration
    class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        @CrossOrigin
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                  //  .antMatchers(HttpMethod.POST, "/user/login", "/user/logout").permitAll()
                    .antMatchers(HttpMethod.POST, "/user/addUser").permitAll()
            .anyRequest().permitAll();
//                    .anyRequest().authenticated();

        }

        @Bean
        public JavaMailSender getJavaMailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);

            mailSender.setUsername("sarah.opris2000@gmail.com");
            mailSender.setPassword("redcross16");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");

            return mailSender;
        }

        @Bean
        public SimpleMailMessage templateSimpleMessage() {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setText(
                    "This is the test email template for your email:\n%s\n");
            return message;
        }

        @Bean
        public void fileUploaderInit() {
            FileStorageService fileStorageService = new FileStorageService();
            fileStorageService.init();
        }
    }

}
