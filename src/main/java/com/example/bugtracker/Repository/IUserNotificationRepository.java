package com.example.bugtracker.Repository;


import com.example.bugtracker.model.UserNotifications;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserNotificationRepository extends CrudRepository<UserNotifications, Long> {
    List<UserNotifications> findAll();
}
