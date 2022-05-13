package com.example.bugtracker.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id_bug")
    private Bug bug;

    private Timestamp modifiedDate;
    private String afterStatus;
    private String beforeStatus;
    private String modifiedBy;
}
