package com.example.bugtracker.service;

import com.example.bugtracker.Repository.*;
import com.example.bugtracker.dto.BugDTO;
import com.example.bugtracker.email.EmailService;
import com.example.bugtracker.model.Attachment;
import com.example.bugtracker.model.Bug;
import com.example.bugtracker.model.Notification;
import com.example.bugtracker.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BugService {
    @Autowired
    private IBugRepository iBugRepository;

    @Autowired
    private INotificationRepository iNotificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private IAttachmentRepository iAttachmentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileStorageService storageService;

    /**
     * Function to get all the Bugs from the database.
     * @return a list of BugDTO
     */
    public List<BugDTO> findAllBugs() {
        return ((List<Bug>) iBugRepository.findAll()).stream().map(this::convertToBugDTO).collect(Collectors.toList());
    }

    /**
     * Function to find a bug with a specific id.
     * @param id of the bug we want to find
     * @return the bug as BugDTO or null if the bug is not found
     */
    public BugDTO findById(Long id) {
        return iBugRepository.findById(id).stream().map(this::convertToBugDTO).findFirst().orElse(null);
    }

    /**
     * Function to add a bug to the if the bug is valid database.
     * And send an email to the assigned user.
     * @param bugDTO a BugDTO from which we build our Bug
     * @return a message how the process ended
     */
    @Transactional
    public String addBug(BugDTO bugDTO) {
        bugDTO.setStatus(Status.OPEN);
        bugDTO.setAttachments(new ArrayList<>());
        Bug bug = convertToBug(bugDTO);
        if (bug.isValid()) {
            try {
                iBugRepository.save(bug);
                iUserRepository.save(bug.getAssignedTo());
                if (bug.getAssignedTo() != null) {
                    emailService.sendEmailWithBugSpecifications(bug.getAssignedTo(), bug);
                }
                return bug.getIdBug() + " " + bug.getTitle() + " added successfully!\n";
            } catch (Exception e) {
                return "Internal Server Error: the bug was not saved!";
            }
        }
        return bugDTO.getTitle() + " is not a valid bug!";
    }

    /**
     * Function to edit the attributes of a bug with specific id.
     * @param id the id of the bug
     * @param bugDTO store all the changes of the bug
     * @return a message how the process ended.
     */
    @Transactional
    public String editBug(Long id, BugDTO bugDTO) {
        Bug bugFromDB = iBugRepository.findById(id).orElse(null);

        if (bugFromDB == null) {
            return "The bug with the given ID was not found in the database.";
        }
        Status oldVersion = bugFromDB.getStatus();
        bugDTO.setCreatedByUsername(bugFromDB.getCreatedBy().getUsername());
        if (convertToBug(bugDTO).isValid()) {
            try {
                if (bugDTO.getTitle() != null) {
                    bugFromDB.setTitle(bugDTO.getTitle());
                }
                if (bugDTO.getDescription() != null) {
                    bugFromDB.setDescription(bugDTO.getDescription());
                }
                if (bugDTO.getSeverity() != null) {
                    bugFromDB.setSeverity(bugDTO.getSeverity());
                }
                if (bugDTO.getStatus() == bugFromDB.getStatus())//the status has not changed in the edit
                {
                    Notification notification = iNotificationRepository.findByType("BUG_UPDATED");

                    userService.addNotificationForBugs(bugFromDB.getCreatedBy(), notification, bugFromDB, oldVersion.toString());
                    if(!bugFromDB.getCreatedBy().getUsername().equals(bugFromDB.getAssignedTo().getUsername())){
                        userService.addNotificationForBugs(bugFromDB.getAssignedTo(), notification, bugFromDB, oldVersion.toString());
                    }
                }
                if (bugDTO.getStatus() != null && bugDTO.getStatus() != bugFromDB.getStatus()) {
                    if (checkStatus(bugFromDB.getStatus(), bugDTO.getStatus())) {
                        bugFromDB.setStatus(bugDTO.getStatus());
                        Notification notification;
                        if (bugFromDB.getStatus().equals(Status.CLOSED)) {
                            notification = iNotificationRepository.findByType("BUG_CLOSED");
                        } else {
                            notification = iNotificationRepository.findByType("BUG_STATUS_UPDATED");
                        }

                        userService.addNotificationForBugs(bugFromDB.getCreatedBy(), notification, bugFromDB, oldVersion.toString());
                        if(!bugFromDB.getCreatedBy().getUsername().equals(bugFromDB.getAssignedTo().getUsername())){
                            userService.addNotificationForBugs(bugFromDB.getAssignedTo(), notification, bugFromDB, oldVersion.toString());
                        }

                    } else {
                        return "The new status of the bug can not be set.";
                    }
                }
                if (bugDTO.getVersion() != null) {
                    bugFromDB.setVersion(bugDTO.getVersion());
                }
                if (bugDTO.getFixedVersion() != null) {
                    bugFromDB.setFixedVersion(bugDTO.getFixedVersion());
                }
                if (bugDTO.getAssignedToUsername() != null) {
                    bugFromDB.setAssignedTo(iUserRepository.findByUsername(bugDTO.getAssignedToUsername()));
                }
                iBugRepository.save(bugFromDB);
                iUserRepository.save(bugFromDB.getAssignedTo());
                if (bugFromDB.getAssignedTo() != null) {
                    emailService.sendEmailWithBugSpecifications(bugFromDB.getAssignedTo(), bugFromDB);
                }
                return bugFromDB.getTitle() + " edited successfully!\n";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "Internal Server Error: the edited bug was not saved!";
            }
        }
        return bugDTO.getTitle() + " is not a valid bug!";
    }

    /**
     * Function which check if the oldStatus can be changed in the new one.
     * @param oldStatus the bug old status
     * @param status the status we want to change it
     * @return true if we can change the status else false
     */
    public boolean checkStatus(Status oldStatus, Status status) {
        switch (oldStatus) {
            case OPEN -> {
                return status == Status.OPEN || status == Status.IN_PROGRESS || status == Status.REJECTED;
            }
            case IN_PROGRESS -> {
                return status == Status.IN_PROGRESS || status == Status.FIXED || status == Status.REJECTED
                        || status == Status.INFO_NEEDED;
            }
            case FIXED -> {
                return status == Status.FIXED || status == Status.OPEN || status == Status.CLOSED;
            }
            case INFO_NEEDED -> {
                return status == Status.INFO_NEEDED || status == Status.IN_PROGRESS;
            }
            case REJECTED -> {
                return status == Status.REJECTED || status == Status.CLOSED;
            }
            case CLOSED -> {
                return status == Status.CLOSED;
            }
        }
        return false;
    }

    /**
     * Function to assign files to a bug.
     * @param bugId the id of the bug we want to assign the files
     * @param files a list of files we want to assign
     * @return a message about how the process ended
     */
    @Transactional
    public String addFilesToBug(Long bugId, MultipartFile[] files) {
        try {
            Bug bug = iBugRepository.findById(bugId).orElse(null);
            if (bug == null) {
                System.out.println("Error: The bug with the given ID does not exist.");
                return "Internal Server Error: the bug was not saved!";
            }
            List<String> filesNotSaved = saveFiles(bug, files);
            iBugRepository.save(bug);
            String message;
            if (filesNotSaved.size() > 0) {
                message = "Bug '" + bug.getTitle() + "' saved without attachment integrity:\nAttachments that could not have been saved:\n"
                        + filesNotSaved;
            } else {
                message = bug.getTitle() + " was saved successfully with all of its attachments.";
            }
            return message;
        } catch (Exception e) {
            return "Internal Server Error: the bug was not saved!";
        }
    }

    // Adding attachments to database and saving them to upload folder

    /**
     * Adding attachments to database and saving them in the upload folder.
     * @param bug the bug of which belong the files
     * @param files the files of the bug
     * @return a list of files which weren't saved
     * @throws MaxUploadSizeExceededException if the size of the files exceed the maximum size allowed
     */
    @Transactional
    public List<String> saveFiles(Bug bug, MultipartFile[] files) throws MaxUploadSizeExceededException {
        List<String> filesNotSaved = new ArrayList<>();

        Arrays.stream(files).forEach(file -> {
            String contentType = file.getContentType();
            String[] accepted = {
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.oasis.opendocument.spreadsheet",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-excel"
            };
            if (contentType != null) {
                if (!contentType.startsWith("image")) {
                    List<String> list = new ArrayList<>();
                    for (String notCorrect : accepted) {
                        list.add(notCorrect);
                    }
                    if (!list.contains(contentType)) {
                        filesNotSaved.add(file.getOriginalFilename());
                        System.out.println("Warning: file " + file.getOriginalFilename() +
                                " was not saved, not supported content type");
                        return;
                    }
                }
            }

            String fileName = file.getOriginalFilename();

            assert fileName != null;
            fileName = fileName.replace(" ", "_");
            String searchedFileName = fileName;

            int foundCountWithFileName = 0;
            while (iAttachmentRepository.findAllByAttContent(searchedFileName).size() > 0) {
                foundCountWithFileName++;
                searchedFileName = insertNumBeforeExtension(fileName, foundCountWithFileName);
            }

            Attachment attachment = Attachment.builder()
                    .bug(bug)
                    .attContent(searchedFileName)
                    .build();
            try {
                storageService.save(file, searchedFileName);
                Attachment savedAttachment = iAttachmentRepository.save(attachment);
                if (savedAttachment.getAttContent() == null) {
                    filesNotSaved.add(file.getOriginalFilename());
                    storageService.delete(file.getOriginalFilename());
                    System.out.println("One file could not be saved.");
                } else {
                    bug.getAttachment().add(attachment);
                }
            } catch (RuntimeException storageServiceException) {
                filesNotSaved.add(file.getOriginalFilename());
                System.out.println("Runtime Exception Error: " + storageServiceException.getMessage());
            }
        });

        return filesNotSaved;
    }

    private String insertNumBeforeExtension(String filename, int number) {
        String[] splitFilename = filename.split("\\.");
        splitFilename[splitFilename.length - 2] += "_" + number + ".";
        return String.join("", splitFilename);
    }

    /**
     * Function to delete a specific bug.
     * @param id of the bug we want to delete
     * @return true if the process was successful or false if the bug wasn't found
     */
    @Transactional
    public boolean deleteBugById(Long id) {
        if (findById(id) == null)
            return false;
        iBugRepository.deleteById(id);
        return true;
    }

    /**
     * Function to convert a BugDTO into a Bug
     * @param bugDTO the BugDTO we want to convert
     * @return the converted Bug
     */
    @Transactional
    public Bug convertToBug(BugDTO bugDTO) {
        return Bug.builder()
                .idBug(bugDTO.getIdBug())
                .title(bugDTO.getTitle())
                .description(bugDTO.getDescription())
                .version(bugDTO.getVersion())
                .targetDate(bugDTO.getTargetDate())
                .status(bugDTO.getStatus())
                .fixedVersion(bugDTO.getFixedVersion())
                .severity(bugDTO.getSeverity())
                .assignedTo(iUserRepository.findByUsername(bugDTO.getAssignedToUsername()))
                .createdBy(iUserRepository.findByUsername(bugDTO.getCreatedByUsername()))
                .attachment(bugDTO.getAttachments())
                .build();
    }

    /**
     * Function to convert a Bug into a BugDTO
     * @param bug the Bug we want to convert
     * @return a converted BugDTO
     */
    public BugDTO convertToBugDTO(Bug bug) {
        return BugDTO.builder()
                .idBug(bug.getIdBug())
                .title(bug.getTitle())
                .description(bug.getDescription())
                .version(bug.getVersion())
                .targetDate(bug.getTargetDate())
                .status(bug.getStatus())
                .fixedVersion(bug.getFixedVersion())
                .severity(bug.getSeverity())
                .createdByUsername(bug.getCreatedBy().getUsername())
                .assignedToUsername(bug.getAssignedTo().getUsername())
                .attachments(bug.getAttachment())
                .build();
    }

    /**
     * Function to find all the bugs created by a user.
     * @param username the username of the User
     * @return a list of BugDTO which were created by the User
     */
    @Transactional
    public List<BugDTO> findBugsCreatedBy(String username) {
        return iBugRepository.findByCreatedBy_Username(username).stream()
                .map(this::convertToBugDTO)
                .collect(Collectors.toList());
    }

    /**
     * Function to find all the bugs assigned to a user.
     * @param username the username of the User
     * @return a list of BugDTO which are assigned to the User
     */
    @Transactional
    public List<BugDTO> findBugsAssignedTo(String username) {
        return iBugRepository.findByAssignedTo_Username(username).stream()
                .map(this::convertToBugDTO)
                .collect(Collectors.toList());
    }

    /**
     * Function to delete a file from a bug.
     * @param idBug the id of the Bug
     * @param filename the name of the file we want to delete
     */
    @Transactional
    public void deleteFileOfBug(Long idBug, String filename) {
        List<Attachment> att = iAttachmentRepository.findAllByAttContent(filename);
        if (att.size() == 0) {
            throw new RuntimeException("File was not found in the database.");
        }
        storageService.delete(filename);
        iAttachmentRepository.deleteById(att.get(0).getIdAtt());
    }

    /**
     * Function to update the status of a bug.
     * @param bug the Bug we want to update
     * @return a ResponseEntity which contain a message and the status of the request
     */
    @Transactional

    public ResponseEntity<String> updateBugStatus(BugDTO bug) {
        Status oldVersion = bug.getStatus();
        if (bug.getStatus() != Status.FIXED) {
            return new ResponseEntity<>("This bug is not fixed!", HttpStatus.BAD_REQUEST);
        }
        Bug updatedBug = iBugRepository.findById(bug.getIdBug()).orElse(null);
        if (updatedBug == null) {
            return new ResponseEntity<>("Bug not found!", HttpStatus.NOT_FOUND);
        } else {
            updatedBug.setStatus(Status.CLOSED);
            Notification notification = iNotificationRepository.findByType("BUG_CLOSED");

            userService.addNotificationForBugs(updatedBug.getCreatedBy(), notification, updatedBug, oldVersion.name());
            if(!updatedBug.getCreatedBy().getUsername().equals(updatedBug.getAssignedTo().getUsername())){
                userService.addNotificationForBugs(updatedBug.getAssignedTo(), notification, updatedBug, oldVersion.name());
            }
            iBugRepository.save(updatedBug);
            return new ResponseEntity<>("Updated status!", HttpStatus.OK);
        }
    }

    // NU STERGEEE ENDREE!!!
    public List<Integer> getListOfNumberBugsWithStatuses() {
        List<Bug> bugsOpen = iBugRepository.findAllByStatus(Status.OPEN);
        long numberBugsOpen = bugsOpen.size();

        List<Bug> bugsRejected = iBugRepository.findAllByStatus(Status.REJECTED);
        long numberBugsRejected = bugsRejected.size();

        List<Bug> bugsInprogress = iBugRepository.findAllByStatus(Status.IN_PROGRESS);
        long numberBugsInProgress = bugsInprogress.size();

        List<Bug> bugsInfoNeeded = iBugRepository.findAllByStatus(Status.INFO_NEEDED);
        long numberBugsInfoNeeded = bugsInfoNeeded.size();

        List<Bug> bugsFixed = iBugRepository.findAllByStatus(Status.FIXED);
        long numberBugsFixed = bugsFixed.size();

        List<Bug> bugsClosed = iBugRepository.findAllByStatus(Status.CLOSED);
        long numberBugsClosed = bugsClosed.size();

        List<Integer> numbersOfBugs = new ArrayList<>();
        numbersOfBugs.add((int) numberBugsOpen);
        numbersOfBugs.add((int) numberBugsRejected);
        numbersOfBugs.add((int) numberBugsInProgress);
        numbersOfBugs.add((int) numberBugsInfoNeeded);
        numbersOfBugs.add((int) numberBugsFixed);
        numbersOfBugs.add((int) numberBugsClosed);

        return numbersOfBugs;
    }
}
