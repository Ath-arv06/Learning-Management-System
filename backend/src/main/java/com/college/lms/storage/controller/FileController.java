package com.college.lms.storage.controller;

import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import com.college.lms.shared.exception.AccessDeniedException;
import com.college.lms.shared.exception.ResourceNotFoundException;
import com.college.lms.storage.dto.StorageFileResponse;
import com.college.lms.storage.entity.StorageContext;
import com.college.lms.storage.entity.StorageFile;
import com.college.lms.storage.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "Secure file upload and download endpoints")
public class FileController {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    public FileController(FileStorageService fileStorageService, UserRepository userRepository) {
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload a file")
    public ResponseEntity<StorageFileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("context") StorageContext context,
            @RequestParam(value = "contextId", required = false) Long contextId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        StorageFile storedFile = fileStorageService.storeFile(file, context, contextId, currentUser);
        return ResponseEntity.ok(StorageFileResponse.fromEntity(storedFile));
    }

    @GetMapping("/{storageKey}")
    @Operation(summary = "Download or stream a file by its storage key")
    public ResponseEntity<Resource> downloadFile(@PathVariable String storageKey) {
        StorageFile metadata = fileStorageService.getMetadata(storageKey);
        Resource resource = fileStorageService.loadFileAsResource(storageKey);

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(metadata.getMimeType());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + metadata.getOriginalFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{storageKey}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete an uploaded file")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String storageKey,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        StorageFile metadata = fileStorageService.getMetadata(storageKey);

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !metadata.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this file");
        }

        fileStorageService.deleteFile(storageKey);
        return ResponseEntity.noContent().build();
    }
}
