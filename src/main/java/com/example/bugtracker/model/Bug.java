package com.example.bugtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import static java.util.stream.Collectors.toList;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bugs")
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idBug;

    @NotNull
    private String title;

    @NotNull
    @Column(columnDefinition="TEXT")
    @Length(min = 250, message = "Description must be of minimum 250 characters.")
    private String description;

    @NotNull
    private String version;

    private String fixedVersion;

    private Timestamp targetDate;

    @NotNull
    private BugSeverity severity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_user_id_user")
    @NotNull
    @JsonIgnore
    private User createdBy;

    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to_id_user")
    @JsonIgnore
    private User assignedTo;

    @OneToMany(mappedBy = "bug", cascade = CascadeType.REMOVE)
    private List<Attachment> attachment = new ArrayList<>();

    @OneToMany(mappedBy = "bug", cascade = CascadeType.REMOVE)
    private List<History> histories;

    @Override
    public String toString() {
        return "Title: " + title + "\n" +
                "Description: " + description + "\n" +
                "Version: " + version + "\n" +
                "TargetDate: " + targetDate + "\n" +
                "Status: " + status + "\n" +
                "Fixed Version: " + fixedVersion + "\n" +
                "Severity: " + severity + "\n" +
                "Created by user: " + createdBy.getUsername() + "\n" +
                "Attachments: " + attachment.stream().map(Attachment::getAttContent) + "\n" +
                "Histories: " + histories + "\n";
    }

    public boolean isValid() {
        if (title != null && description != null && severity != null && createdBy != null
                && description.length() >= 250 && version.matches("^[a-zA-Z0-9.]+$") &&
                (fixedVersion == null || fixedVersion.matches("^[a-zA-Z0-9.]*$"))) {
            if (assignedTo == null) {
                assignedTo = createdBy;
            }
            return true;
        }
        return false;
    }
}
