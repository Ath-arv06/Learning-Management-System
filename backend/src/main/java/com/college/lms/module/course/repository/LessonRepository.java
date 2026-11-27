package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleIdAndIsPublishedTrueOrderBySortOrderAsc(Long moduleId);
}
