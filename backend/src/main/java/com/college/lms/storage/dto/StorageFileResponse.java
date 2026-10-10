package com.college.lms.storage.dto;

import com.college.lms.storage.entity.StorageContext;
import com.college.lms.storage.entity.StorageFile;

import java.time.Instant;

public record StorageFileResponse(
        Long id,
        String storageKey,
        String originalFilename,
        String mimeType,
        Long sizeBytes,
        StorageContext storageContext,
        Long contextId,
        Instant createdAt
) {
    public static StorageFileResponse fromEntity(StorageFile file) {
        return new StorageFileResponse(
                file.getId(),
                file.getStorageKey(),
                file.getOriginalFilename(),
                file.getMimeType(),
                file.getSizeBytes(),
                file.getStorageContext(),
                file.getContextId(),
                file.getCreatedAt()
        );
    }
}
