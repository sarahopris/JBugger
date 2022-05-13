package com.example.bugtracker.service;

import com.example.bugtracker.Repository.IPermissionRepository;
import com.example.bugtracker.Repository.IRoleRepository;
import com.example.bugtracker.dto.RoleDTO;
import com.example.bugtracker.model.Permission;
import com.example.bugtracker.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RoleService {
    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private IPermissionRepository iPermissionRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     *  Finds all the role existent in the database.
     * @return
     *     the list with the roles
     */
    public List<RoleDTO> findAllRoles() {
        return ((List<Role>) iRoleRepository.findAll()).stream().map(this::convertToRoleDTO).collect(Collectors.toList());
    }


    /**
     * Find a role by its id.
     * @param id
     *        The id of the searched role.
     * @return
     *      found role
     */
    public RoleDTO findById(Long id) {
        return iRoleRepository.findById(id).stream().map(this::convertToRoleDTO).findFirst().orElse(null);

    }


    /**
     * Adds a role the database.
     * @param roleDTO
     *        the role that wants to be added
     * @return
     *        message
     */
    public String addRole(RoleDTO roleDTO) {
        // TODO validation
        Role role = convertToRole(roleDTO);
        iRoleRepository.save(role);

        return roleDTO.getType() + " added successfully!";
    }


    /**
     *  Deletes a role by its id.
     * @param id
     *        id of role to be deleted
     * @return
     *        true - if the role was deleted
     *        false- if the role doesn't exist
     */
    public boolean deleteRoleById(Long id) {
        if(findById(id)==null)
            return false;
        iRoleRepository.deleteById(id);
        return true;
    }


    /**
     *  Adds permission to a role.
     * @param idRole
     *           id of the role to which the role will be added
     * @param idPerm
     *          id of the permission that will be added to the role
     * @return
     *        message to inform how the process ended
     */
    public String addPermission(Long idRole, Long idPerm){
        Optional<Role> role=iRoleRepository.findById(idRole);
        Optional<Permission> permission=iPermissionRepository.findById(idPerm);
        if(role.isPresent()&& permission.isPresent()) {
            role.get().getPermissions().add(permission.get());
            iRoleRepository.save(role.get());
            return "Permission added to role";
        }
        else return "Not found";
    }


    /**
     * Deletes a permission from a role
     * @param idRole
     *          id of the role of which permission will be deleted
     * @param idPermission
     *          id of the permission which will be deleted from the role
     * @return
     *          message to inform how the process ended
     */
    public String deletePermissionFromRole(Long idRole, Long idPermission) {
        Role role=iRoleRepository.findById(idRole).orElse(null);
        Permission permission=iPermissionRepository.findById(idPermission).orElse(null);
        if(role!=null && permission!=null) {
            role.getPermissions().remove(permission);
            iRoleRepository.save(role);
            return "Permission removed from role";
        }
        else return "Not found";
    }


    /**
     * Converts a role of type RoleDTO to type Role
     * @param roleDTO
     *         the role that will be converted
     * @return
     *         the converted role
     */
    public Role convertToRole(RoleDTO roleDTO) {
        return Role.builder()
                .type(roleDTO.getType())
                .permissions(roleDTO.getPermissions())
                .build();
    }


    /**
     * Converts a role of type Role to type RoleDTO
     * @param role
     *         the role that will be converted
     * @return
     *         the converted role
     */
    public RoleDTO convertToRoleDTO(Role role) {
        return RoleDTO.builder()
                .type(role.getType())
                .permissions(role.getPermissions())
                .build();
    }


    /**
     * Adds a permission to a role.
     * @param roleType
     *          type of the role to which the permission will be added
     * @param permissionType
     *          type of the permission which will be added to the role
     * @return
     *          message to inform how the process ended
     */
    @Transactional
    public String addPermissionByType(String roleType, String permissionType){
       Role role=iRoleRepository.findByType(roleType);
       Permission permission=iPermissionRepository.findByType(permissionType);
        if(role!=null && permission!=null) {
            role.getPermissions().add(permission);
            iRoleRepository.save(role);
            return "Permission added to role";
        }
        else return "Not found";

    }

    /**
     * Gets all permissions of a role.
     * @param roleType
     *          type of the role of which permissions will be returned
     * @return
     *      list with the returned permissions
     * */
    public List<String> getPermissionList(String roleType){
        Role role=iRoleRepository.findByType(roleType);
        List<String> permList=new ArrayList<>();
        for( Permission p: role.getPermissions()){
            permList.add(p.getType());
        }
        return permList;
    }


    /**
     * Updates the permissions a given role has.
     * @param roleType
     *          the type if role of which permissions will be updated
     * @param permList
     *          the list with the permissions that will be added to the role
     * @return
     *          HTTP status OK if the permissions were added with succes
     */
    public ResponseEntity<?> updatePermList(String roleType, List<String> permList){
        Role role=iRoleRepository.findByType(roleType);
        List<Permission> newPermList=new ArrayList<>();
        for(String p:permList){
            Permission permission=iPermissionRepository.findByType(p);
            newPermList.add(permission);
        }
        role.setPermissions(newPermList);
        iRoleRepository.save(role);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     *Finds all the permissions that a role doesn#t have
     * @param roleType
     *          type of the role
     * @return
     *          List with the permissions that the given role doesn't have
     */
    public List<String> getRemainingPermissions(String roleType){
        Role role=iRoleRepository.findByType(roleType);
        List<Permission> currPermList=role.getPermissions();
        List<String> remainingPerm=new ArrayList<>();
        List<Permission> allPermissions= (List<Permission>) iPermissionRepository.findAll();
        for(Permission p:allPermissions){
            if(!currPermList.contains(p)){
                remainingPerm.add(p.getType());
            }
        }
        return remainingPerm;
    }

    public Role findRoleByType(String roleType){
        return iRoleRepository.findByType(roleType);
    }


    /**
     * Gives the list with roles of which types are in the given list
     * @param roleTypes
     *          list with the types of roles
     * @return
     *          generated List with the roles
     */
    public List<Role> generateListOfRolesFromListOfStringTypes(List<String> roleTypes) {
        List<Role> roles = new ArrayList<>();
        roleTypes.forEach(roleType -> {
            roles.add(this.findRoleByType(roleType));
        });

        return roles;
    }

}
