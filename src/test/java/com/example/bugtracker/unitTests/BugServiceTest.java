package com.example.bugtracker.unitTests;

import com.example.bugtracker.Repository.IBugRepository;
import com.example.bugtracker.Repository.INotificationRepository;
import com.example.bugtracker.Repository.IUserRepository;
import com.example.bugtracker.dto.BugDTO;
import com.example.bugtracker.model.*;
import com.example.bugtracker.service.BugService;
import com.example.bugtracker.service.FileStorageService;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BugServiceTest {
    @Autowired
    private IBugRepository iBugRepository;

    @Autowired
    private IUserRepository iUserRepository;
    @Autowired
    private INotificationRepository iNotificationRepository;

    @Autowired
    private BugService bugService;

    @BeforeAll
    public void setUpTests() {
        User user = User.builder()
                .status((short) 1)
                .username("tester")
                .email("test@as.com")
                .firstName("Admin")
                .lastName("Tester")
                .password("2321331")
                .build();

        User user1 = User.builder()
                .status((short) 1)
                .username("tester1")
                .email("test@as.com")
                .firstName("Admin")
                .lastName("Tester")
                .password("2321331")
                .build();

        iUserRepository.save(user);
        iUserRepository.save(user1);

        Notification n1 = Notification.builder()
                .idNotification(1L)
                .type("BUG_CLOSED")
                .build();

        Notification n2 = Notification.builder()
                .idNotification(2L)
                .type("BUG_UPDATED")
                .build();

        Notification n3 = Notification.builder()
                .idNotification(3L)
                .type("BUG_STATUS_UPDATED")
                .build();

        iNotificationRepository.save(n1);
        iNotificationRepository.save(n2);
        iNotificationRepository.save(n3);
    }

    @Test
   public void changeStatusBadRequest() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.IN_PROGRESS)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();

        ResponseEntity<String> rpEntity = new ResponseEntity<>("This bug is not fixed!", HttpStatus.BAD_REQUEST);
        Bug bug = iBugRepository.save(bugService.convertToBug(bugDTO));
        Assertions.assertEquals(rpEntity, bugService.updateBugStatus(bugService.convertToBugDTO(bug)));
    }

    @Test
    public void updateStatusOK() {
        Notification n1 = Notification.builder()
                .idNotification(1L)
                .type("BUG_CLOSED")
                .build();
        iNotificationRepository.save(n1);
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester1")
                .build();

        ResponseEntity<String> rpEntity = new ResponseEntity<>("Updated status!", HttpStatus.OK);
        Bug bug = iBugRepository.save(bugService.convertToBug(bugDTO));
        Assertions.assertEquals(rpEntity, bugService.updateBugStatus(bugService.convertToBugDTO(bug)));
    }

    @Test
    public void updateStatusNotFound() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();

        ResponseEntity<String> rpEntity = new ResponseEntity<>("Bug not found!", HttpStatus.NOT_FOUND);
        Bug bug = iBugRepository.save(bugService.convertToBug(bugDTO));
        bug.setIdBug(-1L);
        Assertions.assertEquals(rpEntity, bugService.updateBugStatus(bugService.convertToBugDTO(bug)));
    }

    @Test
    public void findAllBugs() {
        int numberOfBugs = (int) StreamSupport.stream(iBugRepository.findAll().spliterator(), false).count();
        Assertions.assertEquals(numberOfBugs, bugService.findAllBugs().size());
    }

    @Test
    public void findBugByIdTestFound() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("dsadsdsadsadsdsadsadsadsadsadsadasLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();
        BugDTO response = bugService.convertToBugDTO(iBugRepository.save(bugService.convertToBug(bugDTO)));
        Assertions.assertEquals(response.getIdBug(), bugService.findById(response.getIdBug()).getIdBug());
    }

    @Test
    public void findBugByIdTestNotFound() {
        Assertions.assertNull(bugService.findById(1L));
    }

    @Test
    public void addBugValid() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername(null)
                .build();
        String response = bugDTO.getIdBug() + " " + bugDTO.getTitle() + " added successfully!\n";

        Assertions.assertEquals(response, bugService.addBug(bugDTO));
    }

    @Test
    public  void addBugInvalid() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title(null)
                .version("1.1.10")
                .createdByUsername("admin")
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();
        String response = bugDTO.getTitle() + " is not a valid bug!";

        Assertions.assertEquals(response, bugService.addBug(bugDTO));
    }

    @Test
    @Disabled
    public void addBugException() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("dsadsadsadsadsadsadsadsadsadsadsadsadLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("lajcsi")
                .build();
        String response = "Internal Server Error: the bug was not saved!";

        Assertions.assertEquals(response, bugService.addBug(bugDTO));
    }

    @Test
    public void editBugNotFound() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("dsadsadsadsadsadsadsadsadsadsadsadsadLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();

        String response = "The bug with the given ID was not found in the database.";
        Assertions.assertEquals(response, bugService.editBug(-1L, bugDTO));
    }

    @Test
    public void editBugSuccess() {
        Notification n3 = Notification.builder()
                .idNotification(3L)
                .type("BUG_STATUS_UPDATED")
                .build();
        iNotificationRepository.save(n3);
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .fixedVersion("1.1.11")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester1")
                .build();
        Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();
        bugDTO.setTitle("test1");

        String response = "test1 edited successfully!\n";
        Assertions.assertEquals(response, bugService.editBug(id, bugDTO));
    }

    @Test
    public void editBugStatusEditFailed() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();
        Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();
        bugDTO.setStatus(Status.IN_PROGRESS);

        String response = "The new status of the bug can not be set.";
        Assertions.assertEquals(response, bugService.editBug(id, bugDTO));
    }

    @Test
    public void editBugStatusEditSuccess() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.IN_PROGRESS)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester1")
                .build();
        Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();
        bugDTO.setStatus(Status.FIXED);

        String response = "test_status edited successfully!\n";
        Assertions.assertEquals(response, bugService.editBug(id, bugDTO));
    }

    @Test
    public void editBugStatusToClosed() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester1")
                .build();
        Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();
        bugDTO.setStatus(Status.CLOSED);

        String response = "test_status edited successfully!\n";
        Assertions.assertEquals(response, bugService.editBug(id, bugDTO));
    }

    @Test
    @Disabled
    public void editBugException() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("lajcsi")
                .build();
        Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();
        bugDTO.setStatus(Status.OPEN);

        String response = "Internal Server Error: the edited bug was not saved!";
        Assertions.assertEquals(response, bugService.editBug(id, bugDTO));
    }

    @Test
    public void editBugInvalidBug() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title(null)
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();
        Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();
        bugDTO.setStatus(Status.CLOSED);

        String response = "null is not a valid bug!";
        Assertions.assertEquals(response, bugService.editBug(id, bugDTO));
    }

    @Test
    public void deleteByIdTrue() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title(null)
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .build();
        Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();
        Assertions.assertTrue(bugService.deleteBugById(id));
    }

    @Test
    public void deleteByIdFalse() {
        Assertions.assertFalse(bugService.deleteBugById(2L));
    }

    @Test
    public void checkStatusTests() {
        Assertions.assertTrue(bugService.checkStatus(Status.OPEN, Status.IN_PROGRESS));
        Assertions.assertFalse(bugService.checkStatus(Status.OPEN, Status.CLOSED));
        Assertions.assertTrue(bugService.checkStatus(Status.IN_PROGRESS, Status.FIXED));
        Assertions.assertFalse(bugService.checkStatus(Status.IN_PROGRESS, Status.CLOSED));
        Assertions.assertTrue(bugService.checkStatus(Status.FIXED, Status.CLOSED));
        Assertions.assertFalse(bugService.checkStatus(Status.FIXED, Status.IN_PROGRESS));
        Assertions.assertTrue(bugService.checkStatus(Status.INFO_NEEDED, Status.IN_PROGRESS));
        Assertions.assertFalse(bugService.checkStatus(Status.INFO_NEEDED, Status.OPEN));
        Assertions.assertTrue(bugService.checkStatus(Status.REJECTED, Status.CLOSED));
        Assertions.assertFalse(bugService.checkStatus(Status.REJECTED, Status.OPEN));
        Assertions.assertTrue(bugService.checkStatus(Status.CLOSED, Status.CLOSED));
        Assertions.assertFalse(bugService.checkStatus(Status.CLOSED, Status.OPEN));
    }

    @Test
    public void convertBetweenBugAndBugDTO() {
        Attachment att = new Attachment();
        List<Attachment> attList = new ArrayList<>();
        attList.add(att);
        attList.add(att);
        attList.add(att);

        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .targetDate(Timestamp.valueOf("2021-10-10 10:10:10"))
                .title("convert")
                .version("1.1.10")
                .fixedVersion("1.1.10")
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester")
                .attachments(attList)
                .build();

        BugDTO twoTimesConvertBugDTO = bugService.convertToBugDTO(bugService.convertToBug(bugDTO));

        Assertions.assertEquals(bugDTO.getIdBug(), twoTimesConvertBugDTO.getIdBug());
        Assertions.assertEquals(bugDTO.getStatus(), twoTimesConvertBugDTO.getStatus());
        Assertions.assertEquals(bugDTO.getSeverity(), twoTimesConvertBugDTO.getSeverity());
        Assertions.assertEquals(bugDTO.getTargetDate(), twoTimesConvertBugDTO.getTargetDate());
        Assertions.assertEquals(bugDTO.getTitle(), twoTimesConvertBugDTO.getTitle());
        Assertions.assertEquals(bugDTO.getVersion(), twoTimesConvertBugDTO.getVersion());
        Assertions.assertEquals(bugDTO.getFixedVersion(), twoTimesConvertBugDTO.getFixedVersion());
        Assertions.assertEquals(bugDTO.getCreatedByUsername(), twoTimesConvertBugDTO.getCreatedByUsername());
        Assertions.assertEquals(bugDTO.getAssignedToUsername(), twoTimesConvertBugDTO.getAssignedToUsername());
        Assertions.assertEquals(bugDTO.getDescription(), twoTimesConvertBugDTO.getDescription());
        Assertions.assertEquals(bugDTO.getAttachments(), twoTimesConvertBugDTO.getAttachments());
    }

    @Test
    public void findBugsCreatedBy() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester1")
                .build();
        String createdByUsername = iBugRepository.save(bugService.convertToBug(bugDTO)).getCreatedBy().getUsername();

        Assertions.assertEquals(createdByUsername, bugService.findBugsCreatedBy(createdByUsername).get(0).getCreatedByUsername());
    }

    @Test
    public void findBugsAssignedTo() {
        BugDTO bugDTO = BugDTO.builder()
                .idBug(2L)
                .status(Status.FIXED)
                .severity(BugSeverity.MEDIUM)
                .title("test_status")
                .version("1.1.10")
                .createdByUsername("admin")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                .createdByUsername("tester")
                .assignedToUsername("tester1")
                .build();
        String assignedToUsername = iBugRepository.save(bugService.convertToBug(bugDTO)).getAssignedTo().getUsername();

        Assertions.assertEquals(assignedToUsername, bugService.findBugsAssignedTo(assignedToUsername).get(0).getAssignedToUsername());
    }

    @Test
    public void addFilesToBugNonExistentBugId() {
        try {
            MultipartFile[] multipartFiles = {};
            Assertions.assertEquals("Internal Server Error: the bug was not saved!",
                    bugService.addFilesToBug(55L, multipartFiles));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void addFilesToBugTotalSuccessAndSuccessfulDeletions() {
        try {
            byte[] data = Files.readAllBytes(Paths.get("src/test/fileUploadSamples/exampleTesting.xls"));
            MockMultipartFile mockMultipartFile = new MockMultipartFile("exampleTesting.xls",
                    "exampleTesting.xls",
                    "application/vnd.ms-excel",
                    data);
            MultipartFile[] multipartFiles = {mockMultipartFile, mockMultipartFile};

            BugDTO bugDTO = BugDTO.builder()
                    .idBug(2L)
                    .status(Status.FIXED)
                    .severity(BugSeverity.MEDIUM)
                    .title("test_status")
                    .version("1.1.10")
                    .createdByUsername("admin")
                    .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                    .createdByUsername("tester")
                    .assignedToUsername("tester1")
                    .build();
            Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();

            Assertions.assertTrue(bugService.addFilesToBug(id, multipartFiles)
                    .contains("was saved successfully with all of its attachments."));

            bugService.deleteFileOfBug(id, "exampleTesting.xls");
            bugService.deleteFileOfBug(id, "exampleTesting_1.xls");
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void addFilesToBugPartialSuccessWithHalfSuccessfulDeletions() {
        try {
            byte[] data;
            data = Files.readAllBytes(Paths.get("src/test/fileUploadSamples/exampleTesting.xls"));
            MockMultipartFile mockMultipartFile = new MockMultipartFile("exampleTesting.xls",
                    "exampleTesting.xls",
                    "application/vnd.ms-excel",
                    data);

            data = Files.readAllBytes(Paths.get("src/test/fileUploadSamples/htmlTesting.html"));
            MockMultipartFile mockMultipartFile1 = new MockMultipartFile("htmlTesting.html",
                    "htmlTesting.html",
                    "text/html",
                    data);

            MultipartFile[] multipartFiles = {mockMultipartFile, mockMultipartFile1};

            BugDTO bugDTO = BugDTO.builder()
                    .idBug(2L)
                    .status(Status.FIXED)
                    .severity(BugSeverity.MEDIUM)
                    .title("test_status")
                    .version("1.1.10")
                    .createdByUsername("admin")
                    .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                    .createdByUsername("tester")
                    .assignedToUsername("tester1")
                    .build();
            Long id = iBugRepository.save(bugService.convertToBug(bugDTO)).getIdBug();

            Assertions.assertTrue(bugService.addFilesToBug(id, multipartFiles)
                    .contains("saved without attachment integrity"));

            bugService.deleteFileOfBug(id, "exampleTesting.xls");
            try {
                bugService.deleteFileOfBug(id, "htmlTesting.html");
                Assertions.fail();
            } catch (Exception e) {
                Assertions.assertTrue(true);
            }
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
