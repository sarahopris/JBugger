package com.example.bugtracker.dto;

import com.example.bugtracker.model.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BugDTO {
    private Long idBug;
    private String title;
    private String description;
    private String version;
    private Timestamp targetDate;
    private Status status;
    private String fixedVersion;
    private BugSeverity severity;
    private String assignedToUsername;
    private String createdByUsername;
    @JsonIgnoreProperties({"idAtt", "bug"})
    private List<Attachment> attachments = new ArrayList<>();
}
