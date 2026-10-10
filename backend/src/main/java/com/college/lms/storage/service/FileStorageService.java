package com.college.lms.storage.service;

import com.college.lms.identity.entity.User;
import com.college.lms.storage.entity.StorageContext;
import com.college.lms.storage.entity.StorageFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StorageFile storeFile(MultipartFile file, StorageContext context, Long contextId, User user);
    Resource loadFileAsResource(String storageKey);
    StorageFile getMetadata(String storageKey);
    void deleteFile(String storageKey);
}
