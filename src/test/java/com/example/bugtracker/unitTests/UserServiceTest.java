package com.example.bugtracker.unitTests;

import com.example.bugtracker.Repository.INotificationRepository;
import com.example.bugtracker.Repository.IPermissionRepository;
import com.example.bugtracker.Repository.IRoleRepository;
import com.example.bugtracker.Repository.IUserRepository;
import com.example.bugtracker.dto.PermissionDTO;
import com.example.bugtracker.dto.RoleDTO;
import com.example.bugtracker.dto.UserDTO;
import com.example.bugtracker.model.*;
import com.example.bugtracker.service.PermissionService;
import com.example.bugtracker.service.RoleService;
import com.example.bugtracker.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private IPermissionRepository iPermissionRepository;

    @Autowired
    private INotificationRepository iNotificationRepository;


    @Test
    void findAll() {
        List<UserDTO> users = userService.findAll();
        int numberOfUsers = (int) StreamSupport.stream(iUserRepository.findAll().spliterator(), false).count();
        System.out.println(numberOfUsers);
        Assertions.assertEquals(numberOfUsers, users.size());
    }

    @Test
    void addUser() {
        // firstName, lastName, email and mobileNumber could not be null
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("naomi")
                .lastName("toth")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        String returnString = userService.addUser(userDTO);

        List<UserDTO> users = userService.findAll();
        int numberOfUsers = (int) StreamSupport.stream(iUserRepository.findAll().spliterator(), false).count();
        System.out.println(numberOfUsers);

        Assertions.assertEquals(userDTO.getFirstName() + " added successfully!", returnString);
        Assertions.assertNotNull(userService.findByUsernameAndPassword(userDTO.getUsername(), userDTO.getPassword()));

        User toDelete=iUserRepository.findByUsername(userDTO.getUsername());
        iUserRepository.delete(toDelete);

    }


    @Test
    void addUserTestNegativeFirstNameNotSpecified() {
        // firstName, lastName, email and mobileNumber could not be null

        // firstName not specified
        UserDTO userDTO = UserDTO.builder()
                .email("torok.szabi@msg.group")
                .lastName("torok")
                .mobileNumber("+40786938987")
                .password("szabi")
                .build();

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));

        String returnString = userService.addUser(userDTO);
        Assertions.assertEquals("User not added to the database! You have to specify your first name, last name, mobile number and email to be able to add a new user.", returnString);

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));
    }


    @Test
    void addUserTestNegativeLastNameNotSpecified() {
        // firstName, lastName, email and mobileNumber could not be null

        // lastName not specified
        UserDTO userDTO = UserDTO.builder()
                .email("torok.szabi@msg.group")
                .firstName("torok")
                .mobileNumber("0786938987")
                .password("szabi")
                .build();

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));

        String returnString = userService.addUser(userDTO);
        Assertions.assertEquals("User not added to the database! You have to specify your first name, last name, mobile number and email to be able to add a new user.", returnString);

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));
    }

    @Test
    void addUserTestNegativeEmailNotSpecified() {
        // firstName, lastName, email and mobileNumber could not be null

        // email not specified
        UserDTO userDTO = UserDTO.builder()
                .firstName("szabi")
                .lastName("torok")
                .mobileNumber("0786938987")
                .password("szabi")
                .build();

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));

        String returnString = userService.addUser(userDTO);
        Assertions.assertEquals("User not added to the database! You have to specify your first name, last name, mobile number and email to be able to add a new user.", returnString);

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));
    }

    @Test
    void addUserTestNegativeMobileNumberNotSpecified() {
        // firstName, lastName, email and mobileNumber could not be null

        // mobile number not specified
        UserDTO userDTO = UserDTO.builder()
                .email("torok.szabi@msg.group")
                .firstName("szabi")
                .lastName("torok")
                .password("szabi")
                .build();

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));

        String returnString = userService.addUser(userDTO);
        Assertions.assertEquals("User not added to the database! You have to specify your first name, last name, mobile number and email to be able to add a new user.", returnString);

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));
    }


    @Test
    void addUserTestNegativeEmailNotValid() {
        // firstName, lastName, email and mobileNumber could not be null

        // email not valid
        UserDTO userDTO = UserDTO.builder()
                .email("gdgdad")
                .firstName("szabi")
                .lastName("torok")
                .password("szabi")
                .mobileNumber("+40786938987")
                .build();

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));

        String returnString = userService.addUser(userDTO);
        Assertions.assertEquals("invalid email address", returnString);

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));
    }

    @Test
    void addUserTestNegativeMobileNumberNotValid() {
        // firstName, lastName, email and mobileNumber could not be null

        // mobile number not valid
        UserDTO userDTO = UserDTO.builder()
                .email("torok.szabi@msg.grpup")
                .firstName("szabi")
                .lastName("torok")
                .password("szabi")
                .mobileNumber("12547")
                .build();

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));

        String returnString = userService.addUser(userDTO);
        Assertions.assertEquals("Invalid mobile nb", returnString);

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));
    }

    @Test
    void ecpValidTestForMobileNumber() {
        // firstName, lastName, email and mobileNumber could not be null

        // mobile number not valid
        UserDTO userDTO = UserDTO.builder()
                .email("torok.szabi@msg.grpup")
                .firstName("szabi")
                .lastName("torok")
                .password("szabi")
                .mobileNumber("0728548801")
                .build();

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));

        String returnString = userService.addUser(userDTO);
        Assertions.assertEquals("Invalid mobile nb", returnString);

        Assertions.assertNull(iUserRepository.findByUsername("toroks"));
    }



    @Test

    void deleteUser() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);

        User user = iUserRepository.findByUsername("lastfir");

        Long id = user.getIdUser();

        Assertions.assertNotNull(iUserRepository.findByUsername("lastfir"));
        Assertions.assertTrue(userService.deleteUserById(id));
        Assertions.assertNull(iUserRepository.findByUsername("lastfir"));
    }

    @Test
    void deleteUserTestNegative() {
        Assertions.assertEquals(Optional.empty(), iUserRepository.findById(-1L));
        Assertions.assertFalse(userService.deleteUserById(-1L));
        Assertions.assertEquals(Optional.empty(), iUserRepository.findById(-1L));
    }


    @Test
    void findByIdTestNegative() {
        Assertions.assertNull(userService.findById(1000L));
    }


    @Test
    void activateUser() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);

        userService.activateUser("lastfir");
        User resultUser=iUserRepository.findByUsername("lastfir");
        Assertions.assertEquals((short) 1, (short) resultUser.getStatus());
         iUserRepository.delete(resultUser);
    }

    @Test
    void activateUserNullUser() {

        Assertions.assertNull(userService.activateUser("lastfirst"));

    }

    @Test
    void deactivateUser() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);

        userService.deactivateUser("lastfir");
        User resultUser=iUserRepository.findByUsername("lastfir");
        Assertions.assertEquals((short) 0,(short)  resultUser.getStatus());
        iUserRepository.delete(resultUser);
    }

    @Test
    void deactivateUserNullUser() {

        Assertions.assertNull(userService.deactivateUser("lastfirst"));

    }

    @Test
    void addRole() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last1")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletest1", null) ;

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);

        Long idUser=iUserRepository.findByUsername("last1f").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();

        String result=userService.addRole(idUser, idRole);
        String expected="Role "+idRole+"added to user "+idUser;
        Assertions.assertEquals(expected, result);

        iUserRepository.delete(iUserRepository.findByUsername("last1f"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));


    }

    @Test
    void addRoleFailed() {

        String result=userService.addRole(2L, 2L);
        String expected="Role "+2+"failed to be added to user "+2;
        Assertions.assertEquals(expected, result);

    }

    @Test
    void removeRole() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last2")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletest8", null) ;

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        Long idUser=iUserRepository.findByUsername("last2f").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();

        userService.addRole(idUser, idRole);

        String expected="Role "+idRole+"removed from user "+idUser;
        String result=userService.removeRole(idUser, idRole);
        Assertions.assertEquals(expected, result);

        iUserRepository.delete(iUserRepository.findByUsername("last2f"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
    }

    @Test
    void removeRoleFailed() {

        String expected="Role "+2+"failed to be removed from user "+2;
        String result=userService.removeRole(2L, 2L);
        Assertions.assertEquals(expected, result);

    }

    @Test
    void findByUsernameAndPassword() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);

        UserDTO userFound=userService.findByUsernameAndPassword("lastfir", userDTO.getPassword());
        Assertions.assertNotNull(userFound);

        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));
    }

    @Test
    void findByUsernameAndPasswordBadUsername() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);

        UserDTO userFound=userService.findByUsernameAndPassword("lastfirst", userDTO.getPassword());
        Assertions.assertNull(userFound);

        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));
    }

    @Test
    void findByUsernameAndPasswordBadPassword() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);

        UserDTO userFound=userService.findByUsernameAndPassword("lastfir", "pass1");
        Assertions.assertNull(userFound);

        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));
    }

    @Test
    void getUserRolesByUsername() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last3")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletest7", null) ;

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        Long idUser=iUserRepository.findByUsername("last3f").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();

        userService.addRole(idUser, idRole);


        userService.getUserRolesByUsername("last3f");

        Assertions.assertNotNull(userService.getUserRolesByUsername("last3f"));

        iUserRepository.delete(iUserRepository.findByUsername("last3f"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));

    }

    @Test
    void getUserRolesByUsernameNullUser() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last4")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();


        userService.getUserRolesByUsername("last4f");

        Assertions.assertNull(userService.getUserRolesByUsername("last4f"));


    }


    @Test
    void checkPermissionManagement() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last5")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletest4", null) ;
        PermissionDTO permissionDTO=new PermissionDTO( "PERMISSION_MANAGEMENT", "description for permission test" );

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        permissionService.addPermission(permissionDTO);

        roleService.addPermissionByType("roletest4", "PERMISSION_MANAGEMENT");
        Long idUser=iUserRepository.findByUsername("last5f").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();
        userService.addRole( idUser, idRole );

        Assertions.assertTrue(userService.checkPermissionManagement("last5f"));

        iUserRepository.delete(iUserRepository.findByUsername("last5f"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
        iPermissionRepository.delete(iPermissionRepository.findByType(permissionDTO.getType()));
    }

    @Test
    void checkPermissionManagementFalse() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last6")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletest5", null) ;
        PermissionDTO permissionDTO=new PermissionDTO( "USER_MANAGEMENT", "description for permission test" );

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        permissionService.addPermission(permissionDTO);

        roleService.addPermissionByType("roletest5", "USER_MANAGEMENT");
        Long idUser=iUserRepository.findByUsername("last6f").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();
        userService.addRole( idUser, idRole );

        Assertions.assertFalse(userService.checkPermissionManagement("last6f"));

        iUserRepository.delete(iUserRepository.findByUsername("last6f"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
        iPermissionRepository.delete(iPermissionRepository.findByType(permissionDTO.getType()));
    }

    @Test
    void checkUserManagement() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletest", null) ;
        PermissionDTO permissionDTO=new PermissionDTO( "USER_MANAGEMENT", "description for permission test" );

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        permissionService.addPermission(permissionDTO);

        roleService.addPermissionByType("roletest", "USER_MANAGEMENT");
        Long idUser=iUserRepository.findByUsername("lastfir").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();
        userService.addRole( idUser, idRole );

        Assertions.assertTrue(userService.checkUserManagement("lastfir"));

        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
        iPermissionRepository.delete(iPermissionRepository.findByType(permissionDTO.getType()));
    }

    @Test
    void checkUserManagementFalse() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last7")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletest6", null) ;
        PermissionDTO permissionDTO=new PermissionDTO( "PERMISSION_MANAGEMENT", "description for permission test" );

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        permissionService.addPermission(permissionDTO);

        roleService.addPermissionByType("roletest6", "PERMISSION_MANAGEMENT");
        Long idUser=iUserRepository.findByUsername("last7f").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();
        userService.addRole( idUser, idRole );

        Assertions.assertFalse(userService.checkUserManagement("last7f"));

        iUserRepository.delete(iUserRepository.findByUsername("last7f"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
        iPermissionRepository.delete(iPermissionRepository.findByType(permissionDTO.getType()));
    }

    @Test
    void deactivateUserIfFiveTimesBadPassword() {


        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);
        ResponseEntity<?> expected= new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<?> result=userService.deactivateUserIfFiveTimesBadPassword("lastfir");

        Assertions.assertEquals(expected, result);
        Assertions.assertEquals((short) 0, (short) iUserRepository.findByUsername("lastfir").getStatus());

        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));
    }

    @Test
    void deactivateUserIfFiveTimesBadPasswordNullUser() {


        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last8")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        // userService.addUser(userDTO);

        ResponseEntity<?> expected=new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ResponseEntity<?> result=userService.deactivateUserIfFiveTimesBadPassword("last8f");

        Assertions.assertEquals(expected, result);



    }


    @Test
    void addUserWithRoles() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("lasta")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();


        RoleDTO roleDTO=new RoleDTO( "roletestadd", null) ;
        roleService.addRole(roleDTO);
        Notification notification= Notification.builder()
                .type("WELCOME_NEW_USER")
                .message("mess")
                .build();

        iNotificationRepository.save(notification);
        String roles[]=new String[1];
        roles[0]=roleDTO.getType();
        ResponseEntity<?> result=userService.addUserWithRoles(userDTO, roles);
        ResponseEntity<?> expected=new ResponseEntity<>(HttpStatus.OK);

        Assertions.assertEquals(expected, result);

    }

    @Test
    void addUserWithRolesBadRequest() {
        //bad request: invalid phone nb

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("lastb")
                .mobileNumber("+4078693rer8987")
                .password("naomi")
                .build();


        RoleDTO roleDTO=new RoleDTO( "roletestaddf", null) ;
        roleService.addRole(roleDTO);
        Notification notification= Notification.builder()
                .type("WELCOME_NEW_USER1")
                .message("mess")
                .build();

        iNotificationRepository.save(notification);
        String roles[]=new String[1];
        roles[0]=roleDTO.getType();
        ResponseEntity<?> result=userService.addUserWithRoles(userDTO, roles);
        ResponseEntity<?> expected=new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Assertions.assertEquals(expected, result);

    }




    @Test
    void deleteUserById() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        userService.addUser(userDTO);
        User user=iUserRepository.findByUsername("lastfir");
        Boolean result=userService.deleteUserById(user.getIdUser());
        Assertions.assertNull(iUserRepository.findByUsername("lastfir"));
        Assertions.assertTrue(result);
    }



    @Test
    void convertToUser() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        User user=userService.convertToUser(userDTO);
        Assertions.assertEquals(userDTO.getUsername(), user.getUsername());
    }

    @Test
    void convertToUserDTO() {
        User user = User.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        UserDTO userDTO=userService.convertToUserDTO(user);
        Assertions.assertEquals(userDTO.getUsername(), user.getUsername());

    }

    @Test
    void updateUser() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        userService.addUser(userDTO);
        UserDTO newuserDTO = UserDTO.builder()
                .username("lastfir")
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40000000000")
                .password("naomi")
                .build();
        userService.updateUser(newuserDTO);
        User result=iUserRepository.findByUsername("lastfir");
        Assertions.assertEquals(newuserDTO.getMobileNumber(), result.getMobileNumber());
//        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));


    }

    @Test
    void updateUserNull() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.updateUser(userDTO);
        User result=iUserRepository.findByUsername("abcdefghijklmnop");
        Assertions.assertNull(result);
//        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));


    }

    @Test
    void checkBugManagement() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last9")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        RoleDTO roleDTO=new RoleDTO( "roletest3", null) ;
        PermissionDTO permissionDTO=new PermissionDTO( "BUG_MANAGEMENT", "description for permission test" );

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        permissionService.addPermission(permissionDTO);

        roleService.addPermissionByType("roletest3", "BUG_MANAGEMENT");
        Long idUser=iUserRepository.findByUsername("last9f").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();
        userService.addRole( idUser, idRole );

        Assertions.assertTrue(userService.checkBugManagement("last9f"));

        iUserRepository.delete(iUserRepository.findByUsername("last9f"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
        iPermissionRepository.delete(iPermissionRepository.findByType(permissionDTO.getType()));
    }

//    @Test
//    void addNotificationToUser() {
//        UserDTO userDTO = UserDTO.builder()
//                .email("tothnaomi@msg.group")
//                .firstName("first")
//                .lastName("last")
//                .mobileNumber("+40786938987")
//                .password("naomi")
//                .build();
//        userService.addUser(userDTO);
//
//        Notification notification=Notification.builder()
//                .message("message")
//                .type("type")
//                .URL("url")
//                .build();
//        iNotificationRepository.save(notification);
//        User user=userService.convertToUser(userDTO);
//        List<UserNotifications> userNotificationsList=new ArrayList<>();
//        user.setUserNotificationsList(userNotificationsList);
//        notification.setUserNotificationsList(userNotificationsList);
//        ResponseEntity<?> result=userService.addNotificationToUser(user, notification, null);
//        ResponseEntity<?> expected=new ResponseEntity<>(HttpStatus.OK);
//        Assertions.assertEquals(expected, result);
//
//
//
//    }

    @Test
    void notifyUserDeactivated() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        userService.addUser(userDTO);
        ResponseEntity<?> result=userService.notifyUserDeactivated(userService.convertToUser(userDTO));
        ResponseEntity<?> expected=new ResponseEntity<>(HttpStatus.OK);
        Assertions.assertEquals(expected, result);

    }

    @Test
    void deactivateUserForStatus() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        userService.addUser(userDTO);
       Boolean result=userService.deactivateUserForStatus("lastfir");
       Assertions.assertTrue(result);
    }



    @Test
    void checkIfBugAssigned() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        userService.addUser(userDTO);
        ResponseEntity<?> expected=new ResponseEntity<>("user has no tasks", HttpStatus.OK);
        ResponseEntity<?> result=userService.checkIfBugAssigned("lastfir");
        Assertions.assertEquals(expected, result);
    }

    @Test
    void activateUserForStatus() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        userService.addUser(userDTO);
        Boolean result=userService.deactivateUserForStatus("lastfir");
        Assertions.assertTrue(result);
    }

    @Test
    void activateDeactivate() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        userDTO.setStatus((short)0);
        userService.addUser(userDTO);
        Boolean result=userService.activateDeactivate("lastfir");
        Assertions.assertTrue(result);
    }

    @Test
    void updateUserWithRoles() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("last")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();


        List<Role> roleList=new ArrayList<>();
        userDTO.setRoles(roleList);
        userService.addUser(userDTO);
        RoleDTO roleDTO=new RoleDTO( "roletest", null) ;
        roleService.addRole(roleDTO);
        Notification notification=Notification.builder()
                .message("message")
                .type("USER_UPDATED")
                .build();

        iNotificationRepository.save(notification);
        String roles[]=new String[1];
        roles[0]=roleDTO.getType();

        ResponseEntity<?> result=userService.updateUserWithRoles(userDTO, roles, userDTO.getUsername());


        ResponseEntity<?> expected=new ResponseEntity<>(HttpStatus.OK);

        Assertions.assertEquals(expected, result);
//
//        iUserRepository.delete(iUserRepository.findByUsername("lastfir"));
//        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
//        iNotificationRepository.delete(iNotificationRepository.findByType("USER_UPDATED"));

    }

    @Test
    void getUsersWihUserManagement() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("lastt")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();
        RoleDTO roleDTO=new RoleDTO( "roletest2", null) ;
        PermissionDTO permissionDTO=new PermissionDTO( "USER_MANAGEMENT", "description for permission test" );

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        permissionService.addPermission(permissionDTO);

        roleService.addPermissionByType("roletest2", "USER_MANAGEMENT");
        Long idUser=iUserRepository.findByUsername("lasttf").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();
        userService.addRole( idUser, idRole );

        Assertions.assertNotNull(userService.getUsersWihUserManagement());

        iUserRepository.delete(iUserRepository.findByUsername("lasttf"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
        iPermissionRepository.delete(iPermissionRepository.findByType(permissionDTO.getType()));



    }

//    @Test
//    void getNotificationFromUser() {
//        UserDTO userDTO = UserDTO.builder()
//                .email("tothnaomi@msg.group")
//                .firstName("ana")
//                .lastName("ana")
//                .mobileNumber("+40786938987")
//                .password("naomi")
//                .build();
//        List<UserNotifications> userNotificationsList=new ArrayList<>();
//        userDTO.setUserNotificationsList(userNotificationsList);
//        userDTO.setUsername("anaana");
//       iUserRepository.save(userService.convertToUser(userDTO));
//
//        Notification notification=Notification.builder()
//                .message("message")
//                .type("type")
//                .build();
//
//        iNotificationRepository.save(notification);
//        userService.addNotificationToUser(userService.convertToUser(userDTO), notification, null);
//        List<String> messages=userService.getNotificationFromUser("anaana");
//        Assertions.assertNotNull(messages);
//
//
//    }

//    @Disabled
//    @Test
//    void getNotificationTypeFromUser() {
//        UserDTO userDTO = UserDTO.builder()
//                .email("tothnaomi@msg.group")
//                .firstName("airst")
//                .lastName("lastd")
//                .mobileNumber("+40786938987")
//                .password("naomi")
//                .build();
//
//        List<UserNotifications> userNotificationsList=new ArrayList<>();
//        userDTO.setUserNotificationsList(userNotificationsList);
//
//
//       userService.addUser(userDTO);
//
//        Notification notification=Notification.builder()
//                .message("message")
//                .type("getNotificationTypeFromUser")
//                .build();
//
//        notification.setUserNotificationsList(userNotificationsList);
//        iNotificationRepository.save(notification);
//
//        userService.addNotificationToUser(userService.convertToUser( userDTO), notification, null );
//        List<String> result=userService.getNotificationTypeFromUser("lastda");
//        Assertions.assertEquals("getNotificationTypeFromUser", result.get(0));
//
//
//
//
//    }



    @Test
    void findById() {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("laste")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);
        Assertions.assertNotNull(userService.findById( iUserRepository.findByUsername("lastef").getIdUser() ));

    }

    @Test
    void generateUsername() {
        Assertions.assertEquals("helpala" , userService.generateUsername("Alabala", "Help"));
    }

    @Test
    void checkBugClose() {
        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@msg.group")
                .firstName("first")
                .lastName("lastf")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        RoleDTO roleDTO=new RoleDTO( "roletestf", null) ;
        PermissionDTO permissionDTO=new PermissionDTO( "BUG_CLOSE", "description for permission test" );

        userService.addUser(userDTO);
        roleService.addRole(roleDTO);
        permissionService.addPermission(permissionDTO);

        roleService.addPermissionByType("roletestf", "BUG_CLOSE");
        Long idUser=iUserRepository.findByUsername("lastff").getIdUser();
        Long idRole=iRoleRepository.findByType(roleDTO.getType()).getIdRole();
        userService.addRole( idUser, idRole );

        Assertions.assertTrue(userService.checkBugClose("lastff"));

        iUserRepository.delete(iUserRepository.findByUsername("lastff"));
        iRoleRepository.delete(iRoleRepository.findByType(roleDTO.getType()));
        iPermissionRepository.delete(iPermissionRepository.findByType(permissionDTO.getType()));
    }

    @Test
    void getNotificationFromUser() {
    }

    @Test
    void getNotificationTimeFromUser() {
    }

    @Test
    void numberUnreadNotificationFromUser() {
    }

    @Test
    void getNotificationStatusFromUser() {
    }



    @Test
    void addNotificationForBugs() {
    }
}
