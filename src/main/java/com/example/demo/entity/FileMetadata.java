package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    
    @Column(columnDefinition = "TEXT")
    private String fileUrl; // The Azure Link
    
    private String uploaderEmail;
    private LocalDateTime uploadDate;
    
    // We will use this later for Security checks
    private String fileHash; 
    private boolean tamperedFlag = false;

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public String getUploaderEmail() { return uploaderEmail; }
    public void setUploaderEmail(String uploaderEmail) { this.uploaderEmail = uploaderEmail; }
    
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public boolean isTamperedFlag() { return tamperedFlag; }
    public void setTamperedFlag(boolean tamperedFlag) { this.tamperedFlag = tamperedFlag; }

    // ... existing fields ...

    // "PRIVATE", "PUBLIC", or "SHARED"
    private String visibility; 
    
    // Only used if visibility is "SHARED"
    private String sharePassword; 

    // --- Add these Getters and Setters ---
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public String getSharePassword() { return sharePassword; }
    public void setSharePassword(String sharePassword) { this.sharePassword = sharePassword; }
}