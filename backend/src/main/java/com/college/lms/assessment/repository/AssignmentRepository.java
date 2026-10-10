package com.college.lms.assessment.repository;

import com.college.lms.assessment.entity.Assignment;
import com.college.lms.assessment.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourseId(Long courseId);

    List<Assignment> findByCourseIdAndStatus(Long courseId, AssignmentStatus status);
}
