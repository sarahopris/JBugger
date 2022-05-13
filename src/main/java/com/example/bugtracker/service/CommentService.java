package com.example.bugtracker.service;

import com.example.bugtracker.Repository.ICommentRepository;
import com.example.bugtracker.dto.CommentDTO;
import com.example.bugtracker.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentService {
    @Autowired
    private ICommentRepository iCommentRepository ;

    public List<CommentDTO> findAllComments() {
        return (iCommentRepository.findAll()).stream().map(this::convertToCommentDTO).collect(Collectors.toList());

    }

    public CommentDTO convertToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .text(comment.getText())
                .date(comment.getDate())
                .build();
    }
}
