package com.example.bugtracker.controller;

import com.example.bugtracker.dto.BugDTO;
import com.example.bugtracker.service.BugService;
import com.example.bugtracker.service.FileStorageService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("bugs")
public class BugController {
    @Autowired
    private BugService bugService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("")
    public List<BugDTO> findAll() {
        return bugService.findAllBugs();
    }

    @GetMapping("{id}")
    public BugDTO findById(@PathVariable("id") Long id) {
        return bugService.findById(id);
    }

    @PostMapping("")
    public ResponseEntity<Object> addBug(@RequestBody BugDTO bugDTO) {
        JSONObject entity = new JSONObject();
        entity.put("message", bugService.addBug(bugDTO));
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editBug(@PathVariable("id") Long id, @RequestBody BugDTO bugDTO) {
        JSONObject entity = new JSONObject();
        String ret = bugService.editBug(id, bugDTO);
        entity.put("message", ret);
        if(!ret.contains("not found")) {
            return new ResponseEntity<>(entity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(entity, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/attachments/{filename}")
    public ResponseEntity<Resource> getFileFromBug(@PathVariable("id") Long id, @PathVariable("filename") String filename) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            Resource resource = fileStorageService.load(filename);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}/attachments/{filename}")
    public ResponseEntity<?> deleteFilesFromBug(@PathVariable("id") Long id, @PathVariable("filename") String filename) {
        JSONObject entity = new JSONObject();
        try {
            bugService.deleteFileOfBug(id, filename);
            entity.put("message", "OK");
            return new ResponseEntity<>(entity, HttpStatus.OK);
        } catch (Exception e) {
            entity.put("message", "Interal server error.");
            return new ResponseEntity<>(entity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("{id}/attachments")
    public ResponseEntity<Object> addFilesToBug(@PathVariable("id") Long id, @RequestPart("files") MultipartFile[] files) {
        JSONObject entity = new JSONObject();
        entity.put("message", bugService.addFilesToBug(id, files));
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public boolean deleteBug(@PathVariable Long id) {
        return bugService.deleteBugById(id);
    }

    @GetMapping("/createdby/{username}")
    public List<BugDTO> findBugsCreatedBy(@PathVariable String username) {
        return bugService.findBugsCreatedBy(username);
    }

    @GetMapping("/assignedto/{username}")
    public List<BugDTO> findBugsAssignedTo(@PathVariable String username) {
        return bugService.findBugsAssignedTo(username);
    }


    @PutMapping("/updateStatus")
    public ResponseEntity<String> updateBugStatus(@RequestBody BugDTO bug){
        return this.bugService.updateBugStatus(bug);
    }

    @GetMapping("assets/{logoId}")
    public ResponseEntity<Resource> getFileFromBug(@PathVariable("logoId") int id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + (id == 0 ? "msg.jpg" : "bt.jpg"));
            Resource resource = fileStorageService.loadAsset(id);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // NU STERGEEE ENDRE!!!!
    @GetMapping("/getListOfStatuses")
    public List<Integer> getListOfNumberBugsWithStatuses(){
        return this.bugService.getListOfNumberBugsWithStatuses();
    }

}
