package com.example.bugtracker.unitTests;

import com.example.bugtracker.Repository.IPermissionRepository;
import com.example.bugtracker.Repository.IRoleRepository;
import com.example.bugtracker.dto.RoleDTO;
import com.example.bugtracker.model.Permission;
import com.example.bugtracker.model.Role;
import com.example.bugtracker.service.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@SpringBootTest
public class RoleServiceTest {
    @Autowired
    private IRoleRepository iRoleRepository;
    @Autowired
    private IPermissionRepository iPermissionRepository;
    @Autowired
    private RoleService roleService;

    @AfterEach
    void cleanUp() {
        iRoleRepository.deleteAll();
        iPermissionRepository.deleteAll();
    }

    @Test
    void findAllRoles() {
        int numberOfBugs = (int) StreamSupport.stream(iRoleRepository.findAll().spliterator(), false).count();
        Assertions.assertEquals(numberOfBugs, roleService.findAllRoles().size());
    }

    @Test
    void findRoleByIdFound() {
        Role role = Role.builder()
                .idRole(1L)
                .type("ADM")
                .build();
        Long id = iRoleRepository.save(role).getIdRole();
        Assertions.assertNotNull(roleService.findById(id));
    }

    @Test
    void findRoleByIdNotFound() {
        Assertions.assertNull(roleService.findById(-1L));
    }

    @Test
    void addRoleSuccess() {
        RoleDTO roleDTO = RoleDTO.builder().type("Tester").build();
        String response = "Tester added successfully!";
        Assertions.assertEquals(response, roleService.addRole(roleDTO));
    }

    @Test
    void deleteRoleByIdSuccess() {
        Role role = Role.builder().type("ADM").build();
        Long id = iRoleRepository.save(role).getIdRole();
        Assertions.assertTrue(roleService.deleteRoleById(id));
    }

    @Test
    void deleteRoleByIdFailed() {
        Assertions.assertFalse(roleService.deleteRoleById(-1L));
    }

    @Test
    void addPermissionSuccess() {
        Role role = Role.builder().type("ADM").build();
        Permission permission = Permission.builder()
                .description("new permission")
                .type("BUG_CLOSE")
                .build();
        Long idRole = iRoleRepository.save(role).getIdRole();
        Long idPermission = iPermissionRepository.save(permission).getIdPermission();
        String response = "Permission added to role";
        Assertions.assertEquals(response, roleService.addPermission(idRole, idPermission));
    }

    @Test
    void addPermissionFailed() {
        String response = "Not found";
        Assertions.assertEquals(response, roleService.addPermission(-1L, -1L));
    }

    @Test
    void deletePermissionFromRoleSuccess() {
        Role role = Role.builder().type("ADM").build();
        Permission permission = Permission.builder()
                .description("new permission")
                .type("BUG_CLOSE")
                .build();
        Long idRole = iRoleRepository.save(role).getIdRole();
        Long idPermission = iPermissionRepository.save(permission).getIdPermission();
        String response = "Permission removed from role";
        Assertions.assertEquals(response, roleService.deletePermissionFromRole(idRole, idPermission));
    }

    @Test
    void deletePermissionFromRoleFailed() {
        String response = "Not found";
        Assertions.assertEquals(response, roleService.deletePermissionFromRole(-1L, -1L));
    }

    @Test
    void addPermissionByTypeSuccess() {
        Role role = Role.builder().type("PM").build();
        Permission permission = Permission.builder()
                .description("new permission")
                .type("USER_MANAGEMENT")
                .build();
        String typeRole = iRoleRepository.save(role).getType();
        String typePermission = iPermissionRepository.save(permission).getType();
        String response = "Permission added to role";
        Assertions.assertEquals(response, roleService.addPermissionByType(typeRole, typePermission));
    }

    @Test
    void addPermissionByTypeFailed() {
        String response = "Not found";
        Assertions.assertEquals(response, roleService.addPermissionByType("TM", "BUG_close"));
    }

    @Test
    void findRoleByTypeFound() {
        Role role = Role.builder().type("TEST").build();
        iRoleRepository.save(role);
        Assertions.assertNotNull(roleService.findRoleByType("TEST"));
    }

    @Test
    void findRoleByTypeNotFound() {
        Assertions.assertNull(roleService.findRoleByType("tester"));
    }

    @Test
    void updatePermListSuccess() {
        Role role = Role.builder().type("DEV").build();
        Permission permission = Permission.builder().type("BUG_UPDATE").build();
        Permission permission1 = Permission.builder().type("USER_ADDED").build();
        iRoleRepository.save(role);
        iPermissionRepository.save(permission1);
        iPermissionRepository.save(permission);
        List<String> permissions = new ArrayList<>();
        permissions.add("BUG_UPDATE");
        permissions.add("USER_ADDED");
        ResponseEntity<?> response = new ResponseEntity<>(HttpStatus.OK);
        Assertions.assertEquals(response, roleService.updatePermList("DEV", permissions));
    }

    @Test
    void getPermissionList() {
        Role role = Role.builder().type("DEV").build();
        Permission permission = Permission.builder().type("BUG_UPDATE").build();
        Permission permission1 = Permission.builder().type("USER_ADDED").build();
        iRoleRepository.save(role);
        iPermissionRepository.save(permission1);
        iPermissionRepository.save(permission);
        List<String> permissions = new ArrayList<>();
        permissions.add("BUG_UPDATE");
        permissions.add("USER_ADDED");
        roleService.updatePermList("DEV", permissions);
        Assertions.assertEquals(permissions.size(), roleService.getPermissionList("DEV").size());
    }

    @Test
    void getRemainingPermissions() {
        Role role = Role.builder().type("DEV").build();
        Permission permission = Permission.builder().type("BUG_UPDATE").build();
        Permission permission1 = Permission.builder().type("USER_ADDED").build();
        Long id = iRoleRepository.save(role).getIdRole();
        iPermissionRepository.save(permission1);
        iPermissionRepository.save(permission);
        int allPermission = iPermissionRepository.findAll().size();
        int rolePermission = roleService.findById(id).getPermissions().size();
        Assertions.assertEquals(allPermission - rolePermission, roleService.getRemainingPermissions("DEV").size());
    }

    @Test
    void generateListOfRolesFromListOfStringTypesTest() {
        Role role = Role.builder().type("DEV").build();
        Role role1 = Role.builder().type("ADM").build();
        iRoleRepository.save(role);
        iRoleRepository.save(role1);
        List<String> roles = new ArrayList<>();
        roles.add("DEV");
        roles.add("ADM");

        Assertions.assertEquals(roles.size(),
                roleService.generateListOfRolesFromListOfStringTypes(roles).size());
    }
}
