package com.example.demo.repository;

import com.example.demo.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {
    
    List<FileMetadata> findByUploaderEmail(String uploaderEmail);
    List<FileMetadata> findByVisibility(String visibility);

    // NEW: Search Method (Case-insensitive search)
    @Query("SELECT f FROM FileMetadata f WHERE LOWER(f.fileName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.uploaderEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FileMetadata> searchFiles(@Param("keyword") String keyword);
}