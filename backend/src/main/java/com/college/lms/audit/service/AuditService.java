package com.college.lms.audit.service;

import com.college.lms.audit.entity.AuditEvent;
import com.college.lms.audit.repository.AuditEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service for recording security and business audit events.
 *
 * <p>All writes are async to avoid blocking the request thread on audit I/O.
 * If the async write fails, it is logged to the application log as a fallback.
 *
 * <p>IMPORTANT: Never pass passwords, tokens, API keys, or other secrets to
 * the {@code details} parameter.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditEventRepository repository;

    public AuditService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Async
    public void log(Long actorId, String actorEmail, String action,
                    String resourceType, String resourceId, String details) {
        try {
            AuditEvent event = new AuditEvent();
            event.setActorId(actorId);
            event.setActorEmail(actorEmail);
            event.setAction(action);
            event.setResourceType(resourceType);
            event.setResourceId(resourceId);
            event.setDetails(details);

            // Attempt to capture request metadata
            try {
                ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    HttpServletRequest request = attrs.getRequest();
                    event.setIpAddress(extractClientIp(request));
                    String ua = request.getHeader("User-Agent");
                    if (ua != null && ua.length() > 500) ua = ua.substring(0, 500);
                    event.setUserAgent(ua);
                }
            } catch (Exception e) {
                log.debug("Could not capture request metadata for audit event: {}", e.getMessage());
            }

            repository.save(event);

        } catch (Exception e) {
            // Audit failure must not break the main operation, but must be visible in logs
            log.error("AUDIT WRITE FAILED — action={}, actor={}, resource={}/{}: {}",
                action, actorEmail, resourceType, resourceId, e.getMessage());
        }
    }

    /** Convenience overload for system events without a specific resource. */
    @Async
    public void log(Long actorId, String actorEmail, String action, String details) {
        log(actorId, actorEmail, action, null, null, details);
    }

    private String extractClientIp(HttpServletRequest request) {
        // Check standard proxy headers before falling back to remote address
        String[] headers = {
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_CLIENT_IP"
        };
        for (String header : headers) {
            String value = request.getHeader(header);
            if (value != null && !value.isEmpty() && !"unknown".equalsIgnoreCase(value)) {
                // X-Forwarded-For may contain a chain; take the first (client) address
                return value.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
