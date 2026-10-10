package com.college.lms.storage.repository;

import com.college.lms.storage.entity.StorageContext;
import com.college.lms.storage.entity.StorageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {
    Optional<StorageFile> findByStorageKey(String storageKey);
    List<StorageFile> findByStorageContextAndContextId(StorageContext context, Long contextId);
}
