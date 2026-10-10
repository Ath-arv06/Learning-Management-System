package com.college.lms.storage.service;

import com.college.lms.identity.entity.User;
import com.college.lms.shared.exception.BusinessRuleException;
import com.college.lms.shared.exception.ResourceNotFoundException;
import com.college.lms.storage.entity.StorageContext;
import com.college.lms.storage.entity.StorageFile;
import com.college.lms.storage.repository.StorageFileRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "csv",
            "png", "jpg", "jpeg", "gif", "webp", "svg",
            "mp4", "webm", "mp3", "wav",
            "zip", "tar", "gz"
    ));

    private static final Set<String> DANGEROUS_EXTENSIONS = new HashSet<>(Arrays.asList(
            "exe", "bat", "cmd", "sh", "bin", "jar", "war", "ear", "msi",
            "php", "jsp", "asp", "aspx", "cgi", "pl", "py", "js", "vbs", "ps1"
    ));

    private final Path fileStorageLocation;
    private final StorageFileRepository storageFileRepository;

    public LocalFileStorageService(
            @Value("${app.storage.local-dir:./uploads}") String uploadDir,
            StorageFileRepository storageFileRepository) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.storageFileRepository = storageFileRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    @Transactional
    public StorageFile storeFile(MultipartFile file, StorageContext context, Long contextId, User user) {
        if (file.isEmpty()) {
            throw new BusinessRuleException("Cannot store empty file");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown"
        );

        if (originalFilename.contains("..")) {
            throw new BusinessRuleException("Filename contains invalid path sequence: " + originalFilename);
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (DANGEROUS_EXTENSIONS.contains(extension)) {
            throw new BusinessRuleException("File type not permitted for security reasons: ." + extension);
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessRuleException("Unsupported file extension: ." + extension);
        }

        String storageKey = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storageKey);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            StorageFile storageFile = new StorageFile(
                    storageKey,
                    originalFilename,
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream",
                    file.getSize(),
                    user,
                    context,
                    contextId
            );

            return storageFileRepository.save(storageFile);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadFileAsResource(String storageKey) {
        try {
            Path filePath = this.fileStorageLocation.resolve(storageKey).normalize();
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new BusinessRuleException("Path traversal attempt detected");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + storageKey);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + storageKey);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StorageFile getMetadata(String storageKey) {
        return storageFileRepository.findByStorageKey(storageKey)
                .orElseThrow(() -> new ResourceNotFoundException("File record not found: " + storageKey));
    }

    @Override
    @Transactional
    public void deleteFile(String storageKey) {
        StorageFile file = getMetadata(storageKey);
        try {
            Path filePath = this.fileStorageLocation.resolve(storageKey).normalize();
            Files.deleteIfExists(filePath);
            storageFileRepository.delete(file);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete stored file: " + storageKey, ex);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
