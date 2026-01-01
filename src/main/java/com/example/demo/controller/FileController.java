package com.example.demo.controller;

import com.example.demo.entity.AuditLog;
import com.example.demo.entity.FileMetadata;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class FileController {

    @Autowired private StorageService storageService;
    @Autowired private FileRepository fileRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    @GetMapping("/")
    public String home(Model model, Principal principal, @RequestParam(value = "search", required = false) String search) {
        String email = principal.getName();

        List<FileMetadata> myFiles;
        List<FileMetadata> publicFiles;
        List<FileMetadata> sharedFiles;

        if (search != null && !search.isEmpty()) {
            // SEARCH MODE: Find matching files across all categories
            // Note: In a real app, you might want to restrict search results based on visibility
            // For now, let's search everything for simplicity, but only show what the user is allowed to see
            List<FileMetadata> allResults = fileRepository.searchFiles(search);
            
            // Filter the results manually for the 3 tabs
            myFiles = allResults.stream().filter(f -> f.getUploaderEmail().equals(email)).toList();
            publicFiles = allResults.stream().filter(f -> "PUBLIC".equals(f.getVisibility())).toList();
            sharedFiles = allResults.stream().filter(f -> "SHARED".equals(f.getVisibility())).toList();
        } else {
            // NORMAL MODE: Show everything
            myFiles = fileRepository.findByUploaderEmail(email);
            publicFiles = fileRepository.findByVisibility("PUBLIC");
            sharedFiles = fileRepository.findByVisibility("SHARED");
        }

        model.addAttribute("myFiles", myFiles);
        model.addAttribute("publicFiles", publicFiles);
        model.addAttribute("sharedFiles", sharedFiles);
        model.addAttribute("searchKeyword", search); // To keep the text in the box
        
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("visibility") String visibility,     // <--- NEW
                             @RequestParam(value = "password", required = false) String password, // <--- NEW
                             Principal principal) {
        try {
            StorageService.UploadResult result = storageService.uploadFile(file);

            FileMetadata metadata = new FileMetadata();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileUrl(result.url);
            metadata.setFileHash(result.hash);
            metadata.setUploaderEmail(principal.getName());
            metadata.setUploadDate(LocalDateTime.now());
            
            // Set Visibility and Password
            metadata.setVisibility(visibility);
            if ("SHARED".equals(visibility)) {
                metadata.setSharePassword(password);
            }

            fileRepository.save(metadata);
            auditLogRepository.save(new AuditLog(principal.getName(), file.getOriginalFilename(), "UPLOADED (" + visibility + ")"));

            return "redirect:/?success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/?error";
        }
    }

    // --- ACCESS LOGIC ---

    // 1. View My Files & Public Files (Direct Access)
    @GetMapping("/view/{id}")
    public String viewFile(@PathVariable Long id, Principal principal) {
        FileMetadata file = fileRepository.findById(id).orElse(null);
        if (file != null) {
            // Log and Redirect
            auditLogRepository.save(new AuditLog(principal.getName(), file.getFileName(), "VIEWED"));
            return "redirect:" + file.getFileUrl();
        }
        return "redirect:/?error";
    }

    // 2. Unlock Shared Files (Show Password Form)
    @GetMapping("/unlock/{id}")
    public String showUnlockPage(@PathVariable Long id, Model model) {
        model.addAttribute("fileId", id);
        return "unlock"; // We will create this HTML page next
    }

    // 3. Verify Password
    @PostMapping("/unlock")
    public String verifyPassword(@RequestParam Long fileId, @RequestParam String password, Principal principal, RedirectAttributes redirectAttributes) {
        FileMetadata file = fileRepository.findById(fileId).orElse(null);
        
        if (file != null && file.getSharePassword().equals(password)) {
            // Success! Log it and go to Azure
            auditLogRepository.save(new AuditLog(principal.getName(), file.getFileName(), "UNLOCKED SHARED FILE"));
            return "redirect:" + file.getFileUrl();
        }
        
        // Failed
        redirectAttributes.addFlashAttribute("error", "Incorrect Password!");
        return "redirect:/unlock/" + fileId;
    }
}