package com.example.bugtracker.controller;

import com.example.bugtracker.dto.CommentDTO;
import com.example.bugtracker.service.CommentService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/comments")
    public List<CommentDTO> findAll() {
        return commentService.findAllComments();
    }

}
