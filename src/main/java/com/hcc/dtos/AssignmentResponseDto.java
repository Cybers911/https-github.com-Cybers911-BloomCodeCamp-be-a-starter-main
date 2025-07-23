package com.hcc.dtos;

import com.hcc.entities.Assignment;

public class AssignmentResponseDto {

    private Long id;
    private String status;
    private Integer number;

    // Constructor to map from Assignment entity
    public AssignmentResponseDto(Assignment assignment) {
        this.id = assignment.getId();
        this.status = assignment.getStatus();
        this.number = assignment.getNumber();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}