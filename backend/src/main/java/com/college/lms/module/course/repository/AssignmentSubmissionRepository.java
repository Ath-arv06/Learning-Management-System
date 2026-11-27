package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.AssignmentSubmission;
import com.college.lms.module.course.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    @Query("SELECT asub FROM AssignmentSubmission asub JOIN FETCH asub.assignment a JOIN FETCH a.course c JOIN FETCH asub.student s WHERE c.id IN :courseIds AND asub.status = :status ORDER BY asub.submittedAt DESC")
    List<AssignmentSubmission> findPendingSubmissionsForCourses(@Param("courseIds") List<Long> courseIds, @Param("status") SubmissionStatus status);

    @Query("SELECT COUNT(asub) FROM AssignmentSubmission asub JOIN asub.assignment a WHERE a.course.id IN :courseIds AND asub.status = :status")
    long countPendingSubmissions(@Param("courseIds") List<Long> courseIds, @Param("status") SubmissionStatus status);
}
