package com.example.bugtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;

@Entity
@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attachments")
@UniqueElements
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JoinColumn(name = "id_att")
    private Long idAtt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bug_id_bug")
    @JsonIgnore
    private Bug bug;

    @JoinColumn(name = "att_content")
    private String attContent;
}
