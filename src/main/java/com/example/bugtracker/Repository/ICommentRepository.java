package com.example.bugtracker.Repository;

import com.example.bugtracker.model.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Repository
@ApplicationScope
public interface ICommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAll();
}
