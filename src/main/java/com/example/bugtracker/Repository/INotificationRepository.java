package com.example.bugtracker.Repository;

import com.example.bugtracker.model.Notification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificationRepository extends CrudRepository<Notification, Long> {

    Notification findByType(String type);
}
