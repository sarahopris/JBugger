package com.example.bugtracker.service;

import com.example.bugtracker.Repository.IPermissionRepository;
import com.example.bugtracker.dto.PermissionDTO;
import com.example.bugtracker.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionService {
    @Autowired
    private IPermissionRepository iPermissionRepository;

    @Autowired
    private EntityManager entityManager;

    public List<PermissionDTO> findAllPermissions() {
        return ((List<Permission>) iPermissionRepository.findAll()).stream().map(this::convertToPermissionDTO).collect(Collectors.toList());

    }

    public PermissionDTO findById(Long id) {
        return iPermissionRepository.findById(id).stream().map(this::convertToPermissionDTO).findFirst().orElse(null);

    }


    /**
     * Adds a permission to the database.
     * @param permissionDTO
     *         the permission that wants to be added
     * @return
     *        message to inform how the process ended
     */
    public String addPermission(PermissionDTO permissionDTO) {
        //TODO validation
        Permission permission = convertToPermission(permissionDTO);
        iPermissionRepository.save(permission);
        return permissionDTO.getType() + " added successfully!";
    }

    /**
     *  Deletes a permission by its id.
     * @param id
     *        id of permission to be deleted
     * @return
     *        true - if the permission was deleted
     *        false- if the permission doesn't exist
     */
    public boolean deletePermissionById(Long id) {
        if(findById(id)==null)
            return false;
        iPermissionRepository.deleteById(id);
        return true;
    }


    public Permission convertToPermission(PermissionDTO permissionDTO) {
        return Permission.builder()
                .type(permissionDTO.getType())
                .description(permissionDTO.getDescription())
                .build();
    }


    public PermissionDTO convertToPermissionDTO(Permission permission) {
        return PermissionDTO.builder()
                .type(permission.getType())
                .description(permission.getDescription())
                .build();
    }
}
