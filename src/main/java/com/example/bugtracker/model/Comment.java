package com.example.bugtracker.model;


import com.example.bugtracker.dto.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long idComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id_bug")
    private Bug bug;

    private String text;
    private Timestamp date;

}
