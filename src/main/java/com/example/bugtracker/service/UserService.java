package com.example.bugtracker.service;

import com.example.bugtracker.Repository.INotificationRepository;
import com.example.bugtracker.Repository.INotificationUserRepository;
import com.example.bugtracker.Repository.IRoleRepository;
import com.example.bugtracker.Repository.IUserRepository;
import com.example.bugtracker.controller.BugController;
import com.example.bugtracker.controller.UserController;
import com.example.bugtracker.dto.UserDTO;
import com.example.bugtracker.dto.UserNotificationDTO;
import com.example.bugtracker.email.EmailService;
import com.example.bugtracker.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService {
    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    public SimpleMailMessage template;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private INotificationRepository iNotificationRepository;

    @Autowired
    private INotificationUserRepository iNotificationUserRepository;


    public List<UserDTO> findAllUsersWithStatusOne() {
        return ((List<User>) iUserRepository.findAll()).stream()
                .filter(user -> user.getStatus() != 0)
                .map(this::convertToUserDTO).collect(Collectors.toList());
    }

    public List<UserDTO> findAll() {
        return ((List<User>) iUserRepository.findAll()).stream()
                .map(this::convertToUserDTO).collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        return iUserRepository.findById(id).stream().map(this::convertToUserDTO).findFirst().orElse(null);
    }


    @Transactional
    public boolean deleteUserById(Long id) {
        if (findById(id) == null)
            return false;
        iUserRepository.deleteById(id);
        return true;
    }

    /**
     * Generate unique username for new user: first 5 letter of last name + first letter of first name(+number)
     * @param firstName
     * @param lastName
     * @return unique username
     */
    public String generateUsername(String firstName, String lastName) {
        firstName = firstName.toLowerCase(Locale.ROOT);
        lastName = lastName.toLowerCase(Locale.ROOT);
        int i = 1;
        String prenume = "";
        String nume = "";
        if (lastName.length() >= 5) {
            prenume = Character.toString(firstName.charAt(0));
            nume = lastName.substring(0, 5);
        } else {
            prenume = firstName.substring(0, 3);
            nume = lastName;
        }

        String username = nume + prenume;
        while (iUserRepository.findByUsername(username) != null) {
            username = nume + prenume + Integer.toString(i);
            i++;
        }

        return username.toLowerCase();

    }


    public String addUser(UserDTO userDTO) {
        // firstName, lastName, mobileNumber and email can not be null

        if (userDTO.getFirstName() == null || userDTO.getLastName() == null || userDTO.getEmail() == null || userDTO.getMobileNumber() == null) {

            return "User not added to the database! You have to specify your first name, last name, mobile number and email to be able to add a new user.";
        }
        userDTO.setUsername(generateUsername(userDTO.getFirstName(), userDTO.getLastName()));

        User user = convertToUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));


        if (user.isValidMobileNumber()) {
            if (user.isValidEmail()) {
                iUserRepository.save(user);
                return userDTO.getFirstName() + " added successfully!";
            } else return "invalid email address";
        } else return "Invalid mobile nb";
    }


    public User convertToUser(UserDTO userDTO) {
        return User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .mobileNumber(userDTO.getMobileNumber())
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .status(userDTO.getStatus())
                .password(userDTO.getPassword())
//                .password(passwordEncoder.encode(userDTO.getPassword()))

                .roles(userDTO.getRoles())

                .userNotificationsList(userDTO.getUserNotificationsList())
                .build();
    }

    public UserDTO convertToUserDTO(User user) {

        UserDTO userDTO = UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .token(user.getToken())
//                .password(user.getPassword())
                .roles(user.getRoles())
                .userNotificationsList(user.getUserNotificationsList())
                .build();
        userDTO.add(linkTo(UserController.class).slash(user.getIdUser()).withSelfRel());
        userDTO.add(linkTo(methodOn(BugController.class).findBugsAssignedTo(userDTO.getUsername())).withRel("AsignedTo"));
        userDTO.add(linkTo(methodOn(BugController.class).findBugsCreatedBy(userDTO.getUsername())).withRel("CreatedBy"));
        return userDTO;
    }


    public UserDTO updateUser(UserDTO userDto) {
        User user = iUserRepository.findByUsername(userDto.getUsername());
        if (user == null)
            return null;
        User userToSave = convertToUser(userDto);

        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
        userToSave.setIdUser(user.getIdUser());
        return convertToUserDTO(iUserRepository.save(userToSave));
    }

    public UserDTO activateUser(String username) {
        User user = iUserRepository.findByUsername(username);
        if (user == null)
            return null;
        user.setStatus((short) 1);
        ;
        return convertToUserDTO(iUserRepository.save(user));
    }

    public UserDTO deactivateUser(String username) {
        User user = iUserRepository.findByUsername(username);
        if (user == null)
            return null;
        user.setStatus((short) 0);
        return convertToUserDTO(iUserRepository.save(user));
    }

    /**
     * Add existing role to exiting user by their ids
     * @param idUser
     * @param idRole
     * @return a string with message of success/ failure
     */
    @Transactional
    public String addRole(Long idUser, Long idRole) {
        Optional<User> user = iUserRepository.findById(idUser);
        Optional<Role> role = iRoleRepository.findById(idRole);
        if (user.isPresent() && role.isPresent()) {

            user.get().getRoles().add(role.get());
            iUserRepository.save(user.get());
            iRoleRepository.save(role.get());
            return "Role " + idRole + "added to user " + idUser;
        }
        return "Role " + idRole + "failed to be added to user " + idUser;
    }


    /**
     * Remove exiting role from the Role List of user
     * @param idUser
     * @param idRole
     * @return a string with message of success/ failure
     */
    @Transactional
    public String removeRole(Long idUser, Long idRole) {
        Optional<User> user = iUserRepository.findById(idUser);
        Optional<Role> role = iRoleRepository.findById(idRole);
        if (user.isPresent() && role.isPresent()) {

            user.get().getRoles().remove(role.get());
            iUserRepository.save(user.get());
            iRoleRepository.save(role.get());
            return "Role " + idRole + "removed from user " + idUser;
        }
        return "Role " + idRole + "failed to be removed from user " + idUser;
    }


    public UserDTO findByUsernameAndPassword(String username, String password) {
        User user = iUserRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return this.convertToUserDTO(user);
        }
        return null;
    }


    /**
     * Function to return the list of roles from a user specified by their username
     * @param username of the needed user
     * @return list of user roles
     */
    public List<Role> getUserRolesByUsername(String username) {
        User user = iUserRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        return user.getRoles();
    }


    /**
     * Chek if specific user has the Permission Management permission
     * @param username of the needed user
     * @return true if it has the permission, false otherwise
     */
    @Transactional
    public Boolean checkPermissionManagement(String username) {
        User user = iUserRepository.findByUsername(username);
        List<Role> roles = user.getRoles();
        for (Role r : roles) {
            List<Permission> permissions = r.getPermissions();
            for (Permission p : permissions) {
                if (p.getType().equals("PERMISSION_MANAGEMENT"))

                    return true;
            }
        }
        return false;
    }


    /**
     * Check if specific user has the User Management permission
     * @param username of the neede user
     * @return true if it has the permission, false otherwise
     */
    @Transactional
    public Boolean checkUserManagement(String username) {
        User user = iUserRepository.findByUsername(username);
        List<Role> roles = user.getRoles();
        for (Role r : roles) {
            List<Permission> permissions = r.getPermissions();
            for (Permission p : permissions) {
                if (p.getType().equals("USER_MANAGEMENT"))

                    return true;
            }
        }
        return false;
    }

    /**
     * Check if specific user has the Bug Management permission
     * @param username of the needed user
     * @return true if it has the permission, false otherwise
     */

    @Transactional
    public Boolean checkBugManagement(String username) {
        User user = iUserRepository.findByUsername(username);
        List<Role> roles = user.getRoles();
        for (Role r : roles) {
            List<Permission> permissions = r.getPermissions();
            for (Permission p : permissions) {
                if (p.getType().equals("BUG_MANAGEMENT"))
                    return true;
            }
        }
        return false;
    }


    /**
     * Check i specific user has the Bug Close permission
     * @param username of the needed user
     * @return true if it has the permission, false otherwise
     */
    @Transactional
    public Boolean checkBugClose(String username) {
        User user = iUserRepository.findByUsername(username);
        List<Role> roles = user.getRoles();
        for (Role r : roles) {
            List<Permission> permissions = r.getPermissions();
            for (Permission p : permissions) {
                if (p.getType().equals("BUG_CLOSE"))
                    return true;
            }
        }
        return false;
    }

    /**
     * Check if specific user has the Bug Export PDF permission
     * @param username of the needed user
     * @return true if it has the permission, false otherwise
     */
    @Transactional
    public Boolean checkBugExportPDF(String username) {
        User user = iUserRepository.findByUsername(username);
        List<Role> roles = user.getRoles();
        for (Role r : roles) {
            List<Permission> permissions = r.getPermissions();
            for (Permission p : permissions) {
                if (p.getType().equals("BUG_EXPORT_PDF"))
                    return true;
            }
        }
        return false;
    }


    /**
     * Function to deactivate user for the 5 times wrong password functionality
     * @param username of the needed user
     * @return a response entity with the resolved status
     */
    @Transactional
    public ResponseEntity<?> deactivateUserIfFiveTimesBadPassword(String username) {
        User user = iUserRepository.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            user.setStatus((short) 0);
            iUserRepository.save(user);
            notifyUserDeactivated(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


    /**
     * Method to add a specific notification to an existing user
     * Base on the notification type, the notification is sent with specific messages and o specific user
     * It uses only the notification meant for the user management functionalities
     * @param oldUser = data of the user before update
     * @param user = data of the user after update
     * @param notification = notification entity
     * @param secondUser = user in case notification needs to be sent to multiple users
     * @return a response entity with OK status
     */
    public ResponseEntity<?> addNotificationToUser(User oldUser, User user, Notification notification, User secondUser) {
        UserNotifications userNotification = new UserNotifications();
        userNotification.setNotification(notification);
        userNotification.setUser(user);
        userNotification.setRead(false);
        userNotification.setDate(Timestamp.valueOf(LocalDateTime.now()));

        String notificationMessage;
        switch (notification.getType()) {
            case ("WELCOME_NEW_USER") -> {
                //user= the created user
                notificationMessage = user.getFirstName() + ";" + user.getLastName() + ";" + user.getMobileNumber() + ";" + user.getEmail() + ";" + user.getUsername();
                break;
            }
            case ("USER_DEACTIVATED") -> {
                //user=the one that deactivated
                //secondUser= the deactivated user
                notificationMessage = secondUser.getFirstName() + ";" + secondUser.getLastName() + ";" + secondUser.getMobileNumber() + ";" + secondUser.getEmail() + ";" + secondUser.getUsername();
                break;
            }
            case ("USER_UPDATED") -> {
                //user=the updated user
                //secondUser= the user that performed the update
                notificationMessage = oldUser.getFirstName() + ";" + oldUser.getLastName() + ";" + oldUser.getMobileNumber() + ";" + oldUser.getEmail() + ";" + oldUser.getUsername() + ";" + user.getFirstName() + ";" + user.getLastName() + ";" + user.getMobileNumber() + ";" + user.getEmail() + ";" + user.getUsername();
                break;
            }
            default -> {
                notificationMessage = "Basic notification";
            }
        }


        userNotification.setMessage(notificationMessage);

        user.getUserNotificationsList().add(userNotification);
        iUserRepository.save(user);

        if (notification.getType().equals("USER_UPDATED") && !user.getUsername().equals(secondUser.getUsername()) ) {
            UserNotifications userNotificationUpdater = new UserNotifications();
            userNotificationUpdater.setNotification(notification);
            userNotificationUpdater.setUser(secondUser);
            userNotificationUpdater.setRead(false);
            userNotificationUpdater.setDate(Timestamp.valueOf(LocalDateTime.now()));
            userNotificationUpdater.setMessage(notificationMessage);

            secondUser.getUserNotificationsList().add(userNotification);
            iUserRepository.save(secondUser);
            iNotificationUserRepository.save(userNotificationUpdater);
            notification.getUserNotificationsList().add(userNotificationUpdater);
        }

        iNotificationUserRepository.save(userNotification);
        notification.getUserNotificationsList().add(userNotification);

        iNotificationRepository.save(notification);

        return new ResponseEntity<>(HttpStatus.OK);

    }


    /**
     * Method of adding a new user with a given list of roles
     * The method will trigger the email sending functionality and the WELCOME NEW USER notification functionality
     * @param userDTO
     * @param roles = array or roles to add to user
     * @return a response entity: OK for success and BAD REQUEST in case the introduced data is not valid
     */
    @Transactional
    public ResponseEntity<?> addUserWithRoles(UserDTO userDTO, String[] roles) {
        userDTO.setUsername(generateUsername(userDTO.getFirstName(), userDTO.getLastName()));
        User user = convertToUser(userDTO);

        user.setPassword(passwordEncoder.encode(userDTO.getUsername()));

        List<UserNotifications> userNotificationsList = new ArrayList<>();
        user.setUserNotificationsList(userNotificationsList);

        List<Role> roleList = new ArrayList<>();

        //add welcome notification
        Notification welcome = iNotificationRepository.findByType("WELCOME_NEW_USER");
        this.addNotificationToUser(null, user, welcome, null);//don't need another user to notify


        for (String role : roles) {
            Role roleAux = roleService.findRoleByType(role);
            roleList.add(roleAux);
        }
        user.setRoles(roleList);
        if (user.isValidMobileNumber()) {
            if (user.isValidEmail()) {
                iUserRepository.save(user);
                if (iUserRepository.findByUsername(userDTO.getUsername()) != null)
                    emailService.sendEmail(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getUsername(), userDTO.getEmail());
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    /**
     * Deactivate user by changing its status
     * @param userToDeactivate= username of the needed user
     * @return true if the user is found and has been deactivated, false otherwise
     */
    @Transactional
    public Boolean deactivateUserForStatus(String userToDeactivate) {
        User deactivatedUser = iUserRepository.findByUsername(userToDeactivate);
        if (deactivatedUser.getBugsAssigned().isEmpty()) {
            deactivatedUser.setStatus((short) 0);
            notifyUserDeactivated(deactivatedUser);
            convertToUserDTO(iUserRepository.save(deactivatedUser));
            return true;
        } else
            return false;
    }

    /**
     * Method to check if all the bugs from a list have status closed
     * @param bugsAssigned = list of bugs to check
     * @return true if all bugs are closed, false otherwise
     */
    public Boolean checkIfAllBugsClosed(List<Bug> bugsAssigned){
        boolean var = true;
        for(Bug bug: bugsAssigned){
            if(bug.getStatus() != Status.CLOSED) {
                var = false;
                break;
            }
        }
        return var;
    }

    /**
     * Ceck if a specific user has any bugs assigned to itself
     * @param username of the needed user
     * @return response entity OK if there are o bugs assigned, FORBIDDEN otherwise
     */
    @Transactional
    public ResponseEntity<?> checkIfBugAssigned(String username) {
        User deactivatedUser = iUserRepository.findByUsername(username);
        if (deactivatedUser.getBugsAssigned().isEmpty() || checkIfAllBugsClosed(deactivatedUser.getBugsAssigned()))
            return new ResponseEntity<>("user has no tasks", HttpStatus.OK);
        else
            return new ResponseEntity<>("user has tasks", HttpStatus.FORBIDDEN);
    }

    /**
     * Activate user by changing its status
     * @param userToActivate = username of the needed user
     * @return true after the activation
     */
    public Boolean activateUserForStatus(String userToActivate) {
        User user = iUserRepository.findByUsername(userToActivate);

        user.setStatus((short) 1);
        convertToUserDTO(iUserRepository.save(user));
        return true;
    }


    /**
     * method which encapsulates both the activation and deactivation functionalities
     * @param userToModify= username of the needed user
     * @return the return values from the activation and deactivation methods
     */
    public Boolean activateDeactivate(String userToModify) {

        // TODO: notification to all the users with USER_MANAGEMENT that a user was deactivated
        User modifiedUser = iUserRepository.findByUsername(userToModify);

        if (modifiedUser.getStatus() == (short) 0)
            return activateUserForStatus(userToModify);
        else
            return deactivateUserForStatus(userToModify);
    }


    /**
     * Method o update user with a list of given roles
     * The method will activate the UPDATED USER notification sending functionality
     * for the user that is being updated and the user that does the update
     * @param userDTO = the updated user
     * @param roles=  the list of new roles
     * @param loggedUsername = the user that does the update
     * @return a response entity Ok if all data is valid, BAD REQUEST otherwise
     */
    @Transactional
    public ResponseEntity<?> updateUserWithRoles(UserDTO userDTO, String[] roles, String loggedUsername) {
        User userLogged = iUserRepository.findByUsername(loggedUsername);

        String oldUserUsername = iUserRepository.findByUsername(userDTO.getUsername()).getUsername();
        String oldUserFirstName = iUserRepository.findByUsername(userDTO.getUsername()).getFirstName();
        String oldUserLastName = iUserRepository.findByUsername(userDTO.getUsername()).getLastName();
        String oldUsermobile = iUserRepository.findByUsername(userDTO.getUsername()).getMobileNumber();
        String oldUseremail = iUserRepository.findByUsername(userDTO.getUsername()).getEmail();

        User oldUser = User.builder()
                .firstName(oldUserFirstName)
                .lastName(oldUserLastName)
                .mobileNumber(oldUsermobile)
                .email(oldUseremail)
                .username(oldUserUsername)
                .build();

        User user = iUserRepository.findByUsername(userDTO.getUsername());
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (userDTO.isValid()) {
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setMobileNumber(userDTO.getMobileNumber());
            user.setEmail(userDTO.getEmail());
            // save the roles
            List<Role> roleList = new ArrayList<>();
            for (String role : roles) {
                Role roleAux = roleService.findRoleByType(role);
                roleList.add(roleAux);
            }
            user.setRoles(roleList);
            Notification userUpdatedNotification = iNotificationRepository.findByType("USER_UPDATED");
            this.addNotificationToUser(oldUser, user, userUpdatedNotification, userLogged);
            this.iUserRepository.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Method of sending the USER DEACTIVATED notification to all users with
     * User Management permission
     *
     * @param deactivatedUser= user that has been deactivated
     * @return response entity OK after the notifcation of users
     */
    @Transactional
    public ResponseEntity<?> notifyUserDeactivated(User deactivatedUser) {
        List<User> userManagers = this.getUsersWihUserManagement();
        Notification deactivated = iNotificationRepository.findByType("USER_DEACTIVATED");
        userManagers.forEach(user -> {
            addNotificationToUser(null, user, deactivated, deactivatedUser);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Get all the users with the permission of User Management
     * @return list of users
     */
    @Transactional
    public List<User> getUsersWihUserManagement() {
        List<User> neededUsers = new ArrayList<>();

        List<User> allUsers = (List<User>) iUserRepository.findAll();

        allUsers.forEach(user -> {
            if (checkUserManagement(user.getUsername())) {
                neededUsers.add(user);
            }
        });
        return neededUsers;
    }

    /**
     * Get a list of custom Notifications from all the notifications of the specified user
     * @param username
     * @return list of needed notifications
     */
    public List<UserNotificationDTO> getNotificationDTOFromUser(String username) {
        List<UserNotificationDTO> userNotificationDTOS = new ArrayList<>();

        List<String> notifications = this.getNotificationFromUser(username);
        List<String> notificationType = this.getNotificationTypeFromUser(username);
        List<Boolean> notificationStatus = this.getNotificationStatusFromUser(username);
        List<Timestamp> notificationsTime = this.getNotificationTimeFromUser(username);

        for (int i = 0; i < notifications.size(); i++) {
            UserNotificationDTO userNotificationDTO = new UserNotificationDTO(notifications.get(i), notificationType.get(i), notificationStatus.get(i), notificationsTime.get(i));
            userNotificationDTOS.add(userNotificationDTO);
        }

        return userNotificationDTOS;
    }

    /**
     * Method to return all the notification messages from the notification list of
     * the specified user
     * @param username
     * @return list of messages
     */
    public List<String> getNotificationFromUser(String username) {
        User user = iUserRepository.findByUsername(username);
        List<UserNotifications> userNotifications = user.getUserNotificationsList();
        List<String> neededNotifications = new ArrayList<>();
        userNotifications.forEach(un -> {
            neededNotifications.add(un.getMessage());
        });
        return neededNotifications;
    }

    /**
     * Method to return all the notification time stamps from the notification list of
     * the specified use
     * @param username
     * @return list of time stamps
     */

    public List<Timestamp> getNotificationTimeFromUser(String username) {
        User user = iUserRepository.findByUsername(username);
        List<UserNotifications> userNotifications = user.getUserNotificationsList();
        List<Timestamp> neededNotifications = new ArrayList<>();

        userNotifications.forEach(un -> {
            neededNotifications.add(un.getDate());
        });
        return neededNotifications;
    }

    /**
     * Method to return all the notification types from the notification list of
     * the specified use
     * @param username
     * @return list of strings
     */
    public List<String> getNotificationTypeFromUser(String username) {
        User user = iUserRepository.findByUsername(username);
        List<UserNotifications> userNotifications = user.getUserNotificationsList();
        List<String> neededNotifications = new ArrayList<>();
        userNotifications.forEach(un -> {
            neededNotifications.add(un.getNotification().getType());
        });

        return neededNotifications;
    }


    /**
     * Method to return the number of all the notifications
     * assigned to a user that have the read status set to false
     * @param username
     * @return number of unread notifications
     */
    public int numberUnreadNotificationFromUser(String username) {
        User user = iUserRepository.findByUsername(username);
        List<UserNotifications> notifications = user.getUserNotificationsList();
        return (int) notifications.stream().filter(userNotification -> !userNotification.isRead()).count();
    }

    /**
     * Method to return the list of status of all the notifications
     * assigned to a user
     * @param username
     * @return list of statuses
     */
    public List<Boolean> getNotificationStatusFromUser(String username) {
        User user = iUserRepository.findByUsername(username);
        List<UserNotifications> userNotifications = user.getUserNotificationsList();
        List<Boolean> neededNotifications = new ArrayList<>();
        userNotifications.forEach(un -> {
            neededNotifications.add(un.isRead());
        });

        return neededNotifications;
    }


    /**
     * Method to set all notifications of a psecific user
     * to read
     * @param username
     * @return response entity OK after the setting of status
     */
    public ResponseEntity<?> setNotificationToRead(String username) {
        User user = iUserRepository.findByUsername(username);
        List<UserNotifications> userNotifications = user.getUserNotificationsList();
        userNotifications.forEach(un -> {
            un.setRead(true);
        });

        iUserRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);

    }


    /**
     * Method to add a bug related notification to a specific user
     * @param user = the user to notify
     * @param notification = the bug related notification
     * @param bug = the bug the notification is about
     * @param oldVersion = the old data of the modified bug
     * @return response entity of OK after the addition of the notification
     */
    public ResponseEntity<?> addNotificationForBugs(User user, Notification notification, Bug bug, String oldVersion) {
        UserNotifications userNotification = new UserNotifications();
        userNotification.setNotification(notification);
        userNotification.setUser(user);
        userNotification.setRead(false);
        userNotification.setDate(Timestamp.valueOf(LocalDateTime.now()));

        String notificationMessage;

        if(notification.getType().equals("BUG_STATUS_UPDATED")) {
            notificationMessage = bug.getTitle() + ";" + bug.getDescription() + ";" + bug.getVersion() + ";" + bug.getFixedVersion() + ";" + bug.getTargetDate() + ";" + bug.getStatus() + ";" + oldVersion;
        }
        else {
            notificationMessage = bug.getTitle() + ";" + bug.getDescription() + ";" + bug.getVersion() + ";" + bug.getFixedVersion() + ";" + bug.getTargetDate() + ";" + bug.getStatus();
        }
        userNotification.setMessage(notificationMessage);
        user.getUserNotificationsList().add(userNotification);
        iUserRepository.save(user);
        iNotificationUserRepository.save(userNotification);
        notification.getUserNotificationsList().add(userNotification);
        iNotificationRepository.save(notification);
        return new ResponseEntity<>(HttpStatus.OK);

    }


    /**
     * Method to delete all the notification from the database
     * that are older than 30 days from the current date
     * @return response entity of OK after the notifications are deleted
     */
    public ResponseEntity<?> deleteOldNotifications(){
        List<UserNotifications> userNotificationsList= (List<UserNotifications>) iNotificationUserRepository.findAll();
        Timestamp currentDate=Timestamp.valueOf(LocalDateTime.now());
        LocalDate current=LocalDate.parse(currentDate.toString().split(" ")[0]);

        userNotificationsList.forEach(notification->{

            LocalDate notifDate = LocalDate.parse(notification.getDate().toString().split(" ")[0]);
            notifDate = notifDate.plusDays(30);
            if (notifDate.isBefore(current)) {
                iNotificationUserRepository.delete(notification);
            }
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Method to check if the username of the logged user is the same as the apssword
     * @param username
     * @return true if they are the same, false otherwise
     */
    public Boolean usernameSameAsPassword(String username) {
        User user = iUserRepository.findByUsername(username);
        return passwordEncoder.matches(user.getUsername(), user.getPassword());
    }

    /**
     * Method of changing the password of the specified user
     * @param username
     * @param old_password
     * @param new_password
     * @return true if the data is valid, false otherwise
     */
    public Boolean changepassword(String username, String old_password, String new_password) {
        User user = iUserRepository.findByUsername(username);

        if (user == null) {
            return false;
        }

        if (!passwordEncoder.matches(old_password, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(new_password));
        iUserRepository.save(user);
        return true;
    }


    public UserDTO getInformationsAboutUser(String username) {
        User user = iUserRepository.findByUsername(username);

        return this.convertToUserDTO(user);
    }

    /**
     * Method to edit the profile of the logged user
     * @param userDTO = the logged user
     * @param currentPass= the current password of the user
     * @param newPass = the new password
     * @return true if all the data is valid, false otherwise
     */
    public Boolean editAccount(UserDTO userDTO, String currentPass, String newPass) {

        User user = iUserRepository.findByUsername(userDTO.getUsername());
        if (user == null)
        {
            return false;
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setMobileNumber(userDTO.getMobileNumber());

        System.out.println("Change account");

        if (!currentPass.equals("")) {
            System.out.println("Change password1");
            if (!passwordEncoder.matches(currentPass, user.getPassword())) {
                return false;
            }
            else {
                // change password
                user.setPassword(passwordEncoder.encode(newPass));
                System.out.println("Change password2");
            }
        }

        Notification notification = iNotificationRepository.findByType("USER_UPDATED");
        this.addNotificationToUser(user, user, notification, user);
        iUserRepository.save(user);
        return true;
    }
}
