package com.college.lms.module.communication.repository;

import com.college.lms.module.communication.entity.LiveClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveClassSessionRepository extends JpaRepository<LiveClassSession, Long> {
    List<LiveClassSession> findByCourseIdOrderByScheduledStartDesc(Long courseId);
}
