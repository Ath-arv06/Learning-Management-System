package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.StudentLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentLessonProgressRepository extends JpaRepository<StudentLessonProgress, Long> {
    Optional<StudentLessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
}
