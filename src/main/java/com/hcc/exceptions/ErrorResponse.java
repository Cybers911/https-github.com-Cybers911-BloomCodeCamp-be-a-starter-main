package com.hcc.exceptions;

import java.util.Date;

public class ErrorResponse {

    private int status;
    private String message;
    private String details;
    private Date timestamp;

    public ErrorResponse(int status, String message, String details, Date timestamp) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}