package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a JOIN FETCH a.course c WHERE a.course.id IN :courseIds AND a.dueDate >= :now AND a.isPublished = true ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingAssignmentsForCourses(@Param("courseIds") List<Long> courseIds, @Param("now") Instant now);
}
