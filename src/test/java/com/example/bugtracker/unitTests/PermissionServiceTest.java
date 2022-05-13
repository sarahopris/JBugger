package com.example.bugtracker.unitTests;

import com.example.bugtracker.Repository.IPermissionRepository;
import com.example.bugtracker.dto.PermissionDTO;
import com.example.bugtracker.dto.UserDTO;
import com.example.bugtracker.model.Permission;
import com.example.bugtracker.service.PermissionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PermissionServiceTest {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private IPermissionRepository iPermissionRepository;

    @Test
    void findAllPermissions() {
        List<PermissionDTO> permissions = permissionService.findAllPermissions();
        int numberOfPermissions = (int) StreamSupport.stream(iPermissionRepository.findAll().spliterator(), false).count();
        System.out.println(numberOfPermissions);
        Assertions.assertEquals(numberOfPermissions, permissions.size());
    }

    @Test
    void findById() {
        PermissionDTO permissionDTO=PermissionDTO.builder()
                .description("desc")
                .type("type")
                .build();
        permissionService.addPermission(permissionDTO);
        PermissionDTO permission=permissionService.findById(iPermissionRepository.findByType(permissionDTO.getType()).getIdPermission());
        Assertions.assertEquals(permissionDTO.getType(), permission.getType());


    }

    @Test
    void addPermission() {
        PermissionDTO permissionDTO=PermissionDTO.builder()
                .description("desc")
                .type("type1")
                .build();
        String result=permissionService.addPermission(permissionDTO);
        Assertions.assertEquals(permissionDTO.getType() + " added successfully!", result);
        permissionService.deletePermissionById(iPermissionRepository.findByType(permissionDTO.getType()).getIdPermission());
    }

    @Test
    void deletePermissionById() {
        PermissionDTO permissionDTO=PermissionDTO.builder()
                .description("desc")
                .type("type2")
                .build();
        permissionService.addPermission(permissionDTO);
        Boolean result=permissionService.deletePermissionById(iPermissionRepository.findByType(permissionDTO.getType()).getIdPermission());
        Assertions.assertTrue(result);

    }

    @Test
    void deletePermissionByIdNull() {
        PermissionDTO permissionDTO=PermissionDTO.builder()
                .description("desc")
                .type("type3")
                .build();
        permissionService.addPermission(permissionDTO);
        Boolean result=permissionService.deletePermissionById(10000L);
        Assertions.assertFalse(result);

    }

    @Test
    void convertToPermission() {
        PermissionDTO permissionDTO=PermissionDTO.builder()
                .description("desc")
                .type("type")
                .build();

        Permission permission=permissionService.convertToPermission(permissionDTO);
        Assertions.assertEquals(permissionDTO.getType(), permission.getType());
        Assertions.assertEquals(permissionDTO.getDescription(), permission.getDescription());
    }

    @Test
    void convertToPermissionDTO() {
        Permission permission=Permission.builder()
                .description("desc")
                .type("type")
                .build();

        PermissionDTO permissionDTO=permissionService.convertToPermissionDTO(permission);
        Assertions.assertEquals(permissionDTO.getType(), permission.getType());
        Assertions.assertEquals(permissionDTO.getDescription(), permission.getDescription());
    }
}