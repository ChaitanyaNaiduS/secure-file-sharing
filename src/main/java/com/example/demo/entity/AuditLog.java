package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;     // WHO did it
    private String fileName;      // WHICH file
    private String action;        // WHAT did they do (e.g., "VIEWED")
    private LocalDateTime timestamp; // WHEN

    // Constructors
    public AuditLog() {}
    public AuditLog(String userEmail, String fileName, String action) {
        this.userEmail = userEmail;
        this.fileName = fileName;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getFileName() { return fileName; }
    public String getAction() { return action; }
    public LocalDateTime getTimestamp() { return timestamp; }
}