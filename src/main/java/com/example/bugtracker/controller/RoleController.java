package com.example.bugtracker.controller;

import com.example.bugtracker.dto.RoleDTO;
import com.example.bugtracker.model.Role;
import com.example.bugtracker.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    public List<RoleDTO> findAll() {
        return roleService.findAllRoles();
    }

    @GetMapping("/roles/getById/id={id}")
    public RoleDTO findById(@PathVariable("id") Long id) {
        return roleService.findById(id);
    }

    @PostMapping("roles/addRole")
    public String addRole(@RequestBody RoleDTO roleDTO) {
        return roleService.addRole(roleDTO);
    }

    @DeleteMapping("/roles/deleteRole/{id}")
    public boolean deleteRole(@PathVariable Long id) {
        return roleService.deleteRoleById(id);
    }


//    @PostMapping("/roles/addPermissionToRole/permissionId={idPermission}/roleId={idRole}")
//    public boolean addPermissionToRole(@PathVariable Long idPermission, @PathVariable Long idRole) {
//        return roleService.addPermissionToRole(idRole, idPermission);
//    }

    @PostMapping(value = "/roles/addPermissionToRole/roleId={idRole}/permissionId={idPermission}")
    public String addPermission(@PathVariable("idRole") Long idRole, @PathVariable("idPermission") Long idPerm) {
        return roleService.addPermission(idRole, idPerm);
    }


    @DeleteMapping("/roles/deletePermissionFromRole/roleId={idRole}/permissionId={idPermission}")
    public String deletePermissionFromRole(@PathVariable("idPermission") Long idPermission, @PathVariable("idRole") Long idRole) {
        return roleService.deletePermissionFromRole(idRole, idPermission);
    }

    @PostMapping(value = "/roles/addPermissionToRoleByType/{role}/{permission}")
    public String addPermissionByType(@PathVariable("role") String role, @PathVariable("permission") String permission) {
        return roleService.addPermissionByType(role, permission);
    }

    @GetMapping(value = "/roles/getPermissions")
   public List<String> getPermissions(@RequestParam("roleType") String roleType){
        return roleService.getPermissionList(roleType);
    }

    @PostMapping(value = "roles/updatePermList")
    public ResponseEntity<?> updatePermList(@RequestParam("roleType") String roleType, @RequestBody List<String> permList)
    {
        return roleService.updatePermList(roleType, permList);
    }

    @GetMapping(value = "/roles/getRemainingPermissions")
    public List<String> getRemainingPermissions(@RequestParam("roleType") String roleType){
        return roleService.getRemainingPermissions(roleType);
    }

    @GetMapping(value = "/roles/findRoleByType")
        public Role findRoleByType(@RequestParam("roleType") String roleType){
            return roleService.findRoleByType(roleType);
    }

    @GetMapping(value = "/roles/returnListOfRolesFromListOfTypes")
        public List<Role> generateListOfRolesFromListOfStringTypes(@RequestBody List<String> roleTypes){
            return roleService.generateListOfRolesFromListOfStringTypes(roleTypes);
    }
}
