package com.hcc.repositories;

import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Find all assignments belonging to a specific user
    List<Assignment> findByUser(User user);

    // Find assignments by their status
    List<Assignment> findByStatus(String status);
}