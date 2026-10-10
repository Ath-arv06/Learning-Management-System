package com.college.lms.audit.repository;

import com.college.lms.audit.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Page<AuditEvent> findByActorIdOrderByOccurredAtDesc(Long actorId, Pageable pageable);

    Page<AuditEvent> findByActionOrderByOccurredAtDesc(String action, Pageable pageable);

    @Query("""
        SELECT a FROM AuditEvent a
        WHERE (:actorEmail IS NULL OR LOWER(a.actorEmail) LIKE LOWER(CONCAT('%', :actorEmail, '%')))
          AND (:action IS NULL OR a.action = :action)
        ORDER BY a.occurredAt DESC
        """)
    Page<AuditEvent> search(
        @Param("actorEmail") String actorEmail,
        @Param("action") String action,
        Pageable pageable
    );
}
