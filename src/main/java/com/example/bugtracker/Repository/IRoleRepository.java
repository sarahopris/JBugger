package com.example.bugtracker.Repository;

import com.example.bugtracker.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository  extends CrudRepository<Role, Long> {
    Optional<Role> findById(Long id);
    Role findByType(String type);

}
