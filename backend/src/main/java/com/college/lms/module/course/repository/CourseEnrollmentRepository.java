package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.CourseEnrollment;
import com.college.lms.module.course.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    
    @Query("SELECT ce FROM CourseEnrollment ce JOIN FETCH ce.course c JOIN FETCH c.department WHERE ce.student.id = :studentId")
    List<CourseEnrollment> findByStudentIdWithCourse(@Param("studentId") Long studentId);

    List<CourseEnrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    Optional<CourseEnrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);

    long countByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
}
