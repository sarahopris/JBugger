package com.example.bugtracker.Repository;

import com.example.bugtracker.model.Permission;
import com.example.bugtracker.model.Role;
import com.example.bugtracker.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPermissionRepository extends CrudRepository<Permission, Long>{
    Optional<Permission> findById(Long id);
    Permission findByType(String type);
    List<Permission> findAll();

}
