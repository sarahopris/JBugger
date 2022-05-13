package com.example.bugtracker.Repository;

import com.example.bugtracker.model.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAttachmentRepository extends CrudRepository<Attachment, Long> {
    List<Attachment> findAllByAttContent(String attContent);
}
