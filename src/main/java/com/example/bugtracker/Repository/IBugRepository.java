package com.example.bugtracker.Repository;


import com.example.bugtracker.model.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBugRepository extends CrudRepository<Bug, Long> {
    Optional<Bug> findById(Long id);

    List<Bug> findByAssignedTo_Username(String username);

    List<Bug> findByCreatedBy_Username(String username);

    List<Bug> findAllByStatus(Status status);
}
