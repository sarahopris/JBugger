package com.example.bugtracker.controller;

import com.example.bugtracker.dto.BugDTO;
import com.example.bugtracker.dto.UserDTO;
import com.example.bugtracker.dto.UserNotificationDTO;
import com.example.bugtracker.model.Role;
import com.example.bugtracker.model.User;
import com.example.bugtracker.model.UserNotifications;
import com.example.bugtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping(value = "/getNotificationStatusFromUser")
    public List<Boolean> getNotificationStatusFromUser(@RequestParam String username) {
        return userService.getNotificationStatusFromUser(username);
    }

    @GetMapping(value = "/setNotificationToRead")
    public ResponseEntity<?> setNotificationToRead(@RequestParam String username) {
        return userService.setNotificationToRead(username);
    }

    @GetMapping(value = "/numberUnreadNotificationFromUser")
    public int numberUnreadNotificationFromUser(@RequestParam String username) {
        return userService.numberUnreadNotificationFromUser(username);
    }


    @GetMapping(value = "/getAllWithStatusOne", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserDTO> findAllWithStatusOne() {
        return userService.findAllUsersWithStatusOne();
    }


    @GetMapping("/getUsersWithUserManagement")
    public List<User> getUsersWithUserManagement() {
        return userService.getUsersWihUserManagement();
    }


    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/getById/id={id}")
    public UserDTO findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PostMapping("/addUser")
    public String addUser(@RequestBody UserDTO userDto) {
        return userService.addUser(userDto);
    }



    @PostMapping("/updateUserWithRoles")
    public ResponseEntity<?> updateUserWithRoles(@RequestBody UserDTO userDTO, @RequestParam String[] roles, @RequestParam String loggedUsername) {

        return userService.updateUserWithRoles(userDTO, roles, loggedUsername);
    }


    @DeleteMapping("/deleteUser/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }


    @PutMapping("/changeUser")
    public UserDTO changeUser(@RequestBody UserDTO userDto) {
        return userService.updateUser(userDto);
    }

    @GetMapping("/activate/{username}")
    public UserDTO activate(@PathVariable String username) {
        return userService.activateUser(username);
    }


    @GetMapping("/deactivate/{username}")
    public UserDTO deactivate(@PathVariable String username) {
        return userService.deactivateUser(username);
    }


    @PostMapping("/addRoleToUser/idRole={idRole}/idUser={idUser}")
    public String addRoleToUser(@PathVariable("idRole") Long idRole, @PathVariable("idUser") Long idUser) {
        return userService.addRole(idUser, idRole);
    }


    @DeleteMapping("/removeRoleToUser/idRole={idRole}/idUser={idUser}")
    public String deleteRoleToUser(@PathVariable("idRole") Long idRole, @PathVariable("idUser") Long idUser) {
        return userService.removeRole(idUser, idRole);
    }

    @GetMapping("/findByUsername")
    public UserDTO findByUsernameAndPassword(@RequestParam("user") String username, @RequestParam("password") String pwd) {
        return userService.findByUsernameAndPassword(username, pwd);
    }

    @PutMapping("/deactivateUserIfFiveTimesBadPassword")
    public ResponseEntity<?> deactivateUserIfFiveTimesBadPassword(@RequestBody String username) {
        return userService.deactivateUserIfFiveTimesBadPassword(username);
    }

    @GetMapping("/checkPermissionManagement")
    public Boolean checkPermissionManagement(@RequestParam("username") String username) {
        return userService.checkPermissionManagement(username);
    }

    @GetMapping("/checkUserManagement")
    public Boolean checkUserManagement(@RequestParam("username") String username) {
        return userService.checkUserManagement(username);
    }

    @GetMapping("/checkBugManagement")
    public Boolean checkBugManagement(@RequestParam("username") String username) {
        return userService.checkBugManagement(username);
    }

    @GetMapping("/checkBugClose")
    public Boolean checkBugClose(@RequestParam("username") String username) {
        return userService.checkBugClose(username);
    }

    @GetMapping("/checkBugExportPDF")
    public Boolean checkBugExportPDF(@RequestParam("username") String username) {
        return userService.checkBugExportPDF(username);
    }


    @GetMapping("/checkIfBugAssignedIsEmpty")
    public ResponseEntity<?> checkIfBugAssignedIsEmpty(@RequestParam("username") String username) {
        return userService.checkIfBugAssigned(username);
    }


    @GetMapping("/activateDeactivate")
    public Boolean activateDeactivate(@RequestParam("userToModify") String userToModify) {
        return userService.activateDeactivate(userToModify);
    }

    @GetMapping(value = "/notifyUserDeactivated")
    public ResponseEntity<?> notifyUserDeactivated(@RequestBody User user){
        return userService.notifyUserDeactivated(user);
    }

    @GetMapping(value = "/getNotificationFromUser")
    public List<String> getNotificationFromUser(@RequestParam String username){
        return userService.getNotificationFromUser(username);
    }

    @GetMapping(value = "/getNotificationTypeFromUser")
    public List<String> getNotificationTypeFromUser(@RequestParam String username){
        return userService.getNotificationTypeFromUser(username);
    }

    @PostMapping("/addUserWithRoles")
    public ResponseEntity<?> addUserWithRoles(@RequestBody UserDTO userDTO, @RequestParam String[] roles){
        return userService.addUserWithRoles(userDTO, roles);
    }



    @GetMapping(value = "/getNotificationDTOsFromUser")
    public List<UserNotificationDTO> getNotificationDTOsFromUser(@RequestParam String username) {
        return userService.getNotificationDTOFromUser(username);
    }




    @GetMapping(value = "/getNotificationDateFromUser")
    public List<Timestamp> getNotificationDateFromUser(@RequestParam String username) {
        return userService.getNotificationTimeFromUser(username);
    }


    @GetMapping(value = "/deleteOldNotifications")
    public ResponseEntity<?> deleteOldNotifications() {
        return userService.deleteOldNotifications();
    }


    @GetMapping(value = "/userNameSameAsPassword")
    public Boolean usernameSameAsPassword(@RequestParam String username) {
        return userService.usernameSameAsPassword(username);
    }


    @GetMapping(value = "/changePassword")
    public Boolean changePassword(@RequestParam String username, @RequestParam String old_password, @RequestParam String new_password) {
        return userService.changepassword(username,old_password, new_password);
    }

    @PutMapping(value = "/editAccount")
    public Boolean editAccount( @RequestParam String currentPassword, @RequestParam String newPassword, @RequestBody UserDTO userDTO) {
        return userService.editAccount(userDTO, currentPassword, newPassword);
    }

    @GetMapping(value = "/getInformationsAboutUser")
    public UserDTO getInformationsAboutUser(@RequestParam String username) {
        return userService.getInformationsAboutUser(username);
    }


}
