package com.example.bugtracker.Repository;

import com.example.bugtracker.model.UserNotifications;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationUserRepository extends CrudRepository<UserNotifications, Long> {

}
