package com.college.lms.course.repository;

import com.college.lms.course.entity.Course;
import com.college.lms.course.entity.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCode(String code);

    Optional<Course> findByCode(String code);

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    Page<Course> findByDepartmentIdAndStatus(Long departmentId, CourseStatus status, Pageable pageable);

    /** Courses where the given user is an instructor (owner or collaborator). */
    @Query("SELECT c FROM Course c JOIN c.instructors ci WHERE ci.user.id = :userId")
    Page<Course> findByInstructorId(@Param("userId") Long userId, Pageable pageable);

    /** Courses the given student is actively enrolled in. */
    @Query("""
            SELECT e.course FROM Enrollment e
            WHERE e.student.id = :studentId AND e.status = 'ACTIVE'
            """)
    Page<Course> findActiveEnrolledCourses(@Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    long countActiveEnrollments(@Param("courseId") Long courseId);
}
