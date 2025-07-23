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
        User user = userService.getUserFromToken(token);
        List<Assignment> assignments = assignmentRepository.findByUser(user);
        return assignments.stream()
                .map(AssignmentResponseDto::new)
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
        User user = userService.getUserFromToken(token);
        assignment.setUser(user);
        Assignment savedAssignment = assignmentRepository.save(assignment);
        return ResponseEntity.ok(savedAssignment);
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
        Assignment savedAssignment = assignmentRepository.save(existingAssignment);
        return ResponseEntity.ok(savedAssignment);
    }

    // Delete an assignment by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        userService.getUserFromToken(token); // Ensure user exists
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        assignmentRepository.delete(assignment);
        return ResponseEntity.ok("Assignment deleted successfully.");
    }

    // Get assignments ready for review
    @GetMapping("/ready")
    public List<AssignmentResponseDto> getReadyAssignments() {
        List<Assignment> readyAssignments = assignmentRepository.findByStatus("READY");
        return readyAssignments.stream()
                .map(AssignmentResponseDto::new)
                .collect(Collectors.toList());
    }

    // Get resubmitted assignments
    @GetMapping("/resubmitted")
    public List<AssignmentResponseDto> getResubmittedAssignments() {
        List<Assignment> resubmittedAssignments = assignmentRepository.findByStatus("RESUBMITTED");
        return resubmittedAssignments.stream()
                .map(AssignmentResponseDto::new)
                .collect(Collectors.toList());
    }

    // Claim an assignment for review
    @PostMapping("/claim/{id}")
    public ResponseEntity<?> claimAssignment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        User reviewer = userService.getUserFromToken(token);
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        if (!"READY".equals(assignment.getStatus())) {
            throw new UnauthorizedAccessException("Assignment is not ready to be claimed.");
        }

        assignment.setStatus("CLAIMED");
        assignment.setCodeReviewer(reviewer);
        assignmentRepository.save(assignment);
        return ResponseEntity.ok("Assignment claimed successfully.");
    }

    // Reclaim an assignment for review
    @PostMapping("/reclaim/{id}")
    public ResponseEntity<?> reclaimAssignment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        User reviewer = userService.getUserFromToken(token);
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        if (!"RESUBMITTED".equals(assignment.getStatus())) {
            throw new UnauthorizedAccessException("Assignment is not eligible to be reclaimed.");
        }

        assignment.setStatus("CLAIMED");
        assignment.setCodeReviewer(reviewer);
        assignmentRepository.save(assignment);
        return ResponseEntity.ok("Assignment reclaimed successfully.");
    }
}