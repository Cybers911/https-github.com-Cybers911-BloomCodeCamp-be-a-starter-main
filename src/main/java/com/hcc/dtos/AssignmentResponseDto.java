package com.hcc.dtos;

import com.hcc.entities.Assignment;
import com.hcc.enums.AssignmentEnum;
import com.hcc.enums.AssignmentStatusEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class AssignmentResponseDto {

    private Long id;
    private String status;
    private Integer number;

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
