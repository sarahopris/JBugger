package com.example.bugtracker.controller;

import com.example.bugtracker.dto.PermissionDTO;
import com.example.bugtracker.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping("/permissions")
    public List<PermissionDTO> findAll() {
        return permissionService.findAllPermissions();
    }

    @GetMapping("/permissions/getById/id={id}")
    public PermissionDTO findById(@PathVariable("id") Long id) {
        return permissionService.findById(id);
    }

    @PostMapping("permissions/addPermission")
    public String addPermission(@RequestBody PermissionDTO permissionDTO) {
        return permissionService.addPermission(permissionDTO);
    }

    @DeleteMapping("/permissions/deletePermission/{id}")
    public boolean deletePermission(@PathVariable Long id) {
        return permissionService.deletePermissionById(id);
    }

}
