package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
    List<CourseModule> findByCourseIdAndIsPublishedTrueOrderBySortOrderAsc(Long courseId);
}
