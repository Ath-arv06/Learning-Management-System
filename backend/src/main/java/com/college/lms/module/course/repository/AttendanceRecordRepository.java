package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.AttendanceRecord;
import com.college.lms.module.course.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findBySessionId(Long sessionId);

    Optional<AttendanceRecord> findBySessionIdAndStudentId(Long sessionId, Long studentId);

    @Query("SELECT ar FROM AttendanceRecord ar JOIN FETCH ar.session s WHERE s.course.id = :courseId AND ar.student.id = :studentId")
    List<AttendanceRecord> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);
}
