package com.hcc.controllers;

import com.hcc.dtos.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.services.UserService;
import com.hcc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserService userService; // Replaced UserRepository with UserService

    @Autowired
    private JwtUtil jwtUtil;

    // Get all assignments for the logged-in user
    @GetMapping
    public List<AssignmentResponseDto> getAssignmentsByUser(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(jwt);

        // Use UserService to find the logged-in user
        User user = userService.findByUsername(username); // Replaced UserRepository call with UserService

        return assignmentRepository.findByUserId(user.getId())
                .stream()
                .map(assignment -> {
                    // Convert Assignment entity to AssignmentResponseDto for cleaner API responses
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
        return assignmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new assignment for the logged-in user
    @PostMapping
    public ResponseEntity<Assignment> createAssignment(
            @RequestBody Assignment assignment,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(jwt);

        // Use UserService to find the logged-in user
        User user = userService.findByUsername(username); // Replaced UserRepository call with UserService

        // Associate the assignment with the logged-in user
        assignment.setUser(user);
        return ResponseEntity.status(201).body(assignmentRepository.save(assignment));
    }

    // Update an existing assignment by its ID
    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable Long id,
            @RequestBody Assignment updatedAssignment
    ) {
        return assignmentRepository.findById(id)
                .map(existingAssignment -> {
                    // Update only the necessary fields
                    existingAssignment.setStatus(updatedAssignment.getStatus());
                    existingAssignment.setGithubUrl(updatedAssignment.getGithubUrl());
                    existingAssignment.setBranch(updatedAssignment.getBranch());
                    existingAssignment.setReviewVideoUrl(updatedAssignment.getReviewVideoUrl());

                    return ResponseEntity.ok(assignmentRepository.save(existingAssignment));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}