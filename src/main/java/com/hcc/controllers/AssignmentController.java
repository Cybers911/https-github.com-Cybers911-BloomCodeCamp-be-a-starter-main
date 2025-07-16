package com.hcc.controllers;

import com.hcc.dtos.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.exceptions.UnauthorizedAccessException;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.services.UserService;
import com.hcc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Get all assignments for the logged-in user
    @GetMapping
    public List<AssignmentResponseDto> getAssignmentsByUser(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(jwt);

        User user = userService.findByUsername(username);

        return assignmentRepository.findByUserId(user.getId())
                .stream()
                .map(assignment -> {
                    AssignmentResponseDto dto = new AssignmentResponseDto();
                    dto.setId(assignment.getId());
                    dto.setStatus(assignment.getStatus());
                    dto.setNumber(assignment.getNumber());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Get an assignment by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        return ResponseEntity.ok(assignment);
    }

    // Create a new assignment for the logged-in user
    @PostMapping
    public ResponseEntity<Assignment> createAssignment(
            @Valid @RequestBody Assignment assignment,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(jwt);

        User user = userService.findByUsername(username);
        assignment.setUser(user);

        return ResponseEntity.status(201).body(assignmentRepository.save(assignment));
    }

    // Update an existing assignment by its ID
    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody Assignment updatedAssignment
    ) {
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        existingAssignment.setStatus(updatedAssignment.getStatus());
        existingAssignment.setGithubUrl(updatedAssignment.getGithubUrl());
        existingAssignment.setBranch(updatedAssignment.getBranch());
        existingAssignment.setReviewVideoUrl(updatedAssignment.getReviewVideoUrl());

        return ResponseEntity.ok(assignmentRepository.save(existingAssignment));
    }

    // Delete an assignment by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(jwt);

        User user = userService.findByUsername(username);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        if (!assignment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this assignment.");
        }

        assignmentRepository.delete(assignment);
        return ResponseEntity.ok("Assignment deleted successfully.");
    }
}