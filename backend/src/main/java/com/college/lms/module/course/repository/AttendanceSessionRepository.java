package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    List<AttendanceSession> findByCourseIdOrderBySessionDateDesc(Long courseId);
}
