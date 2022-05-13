package com.example.bugtracker.Repository;

import com.example.bugtracker.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IUserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByUsernameAndPassword(String username, String password);
    User findByUsername(String username);
}
