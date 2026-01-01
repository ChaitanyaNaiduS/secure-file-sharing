package com.example.demo.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    // We now return a custom object containing BOTH the URL and the Hash
    public UploadResult uploadFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        // 1. Connect to Azure
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobContainerClient container = serviceClient.getBlobContainerClient(containerName);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blob = container.getBlobClient(fileName);

        // 2. Upload to Azure
        blob.upload(file.getInputStream(), file.getSize(), true);

        // 3. Calculate the Digital Fingerprint (SHA-256 Hash)
        String fileHash = calculateHash(file.getBytes());

        return new UploadResult(blob.getBlobUrl(), fileHash);
    }

    // This is the math that creates the fingerprint
    private String calculateHash(byte[] fileBytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // A simple helper class to hold our two results
    public static class UploadResult {
        public String url;
        public String hash;

        public UploadResult(String url, String hash) {
            this.url = url;
            this.hash = hash;
        }
    }
}