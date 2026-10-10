package com.college.lms.course.entity;

/**
 * Discriminator for lesson content type.
 * TEXT            – rich-text/HTML content stored inline
 * VIDEO           – uploaded video file (storage_file_id)
 * DOCUMENT        – uploaded PDF/DOCX (storage_file_id)
 * EXTERNAL_VIDEO  – YouTube / Vimeo URL (resource_url)
 */
public enum LessonType {
    TEXT, VIDEO, DOCUMENT, EXTERNAL_VIDEO
}
