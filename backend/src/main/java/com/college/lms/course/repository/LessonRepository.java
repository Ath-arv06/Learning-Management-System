package com.college.lms.course.repository;

import com.college.lms.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);

    int countByModuleId(Long moduleId);
}
