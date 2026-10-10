package com.college.lms.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the calling user does not have permission to perform
 * an operation on a specific resource (ownership / assignment check).
 * Returns 403 Forbidden, not 401 Unauthorized.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
