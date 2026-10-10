package com.college.lms.course.repository;

import com.college.lms.course.entity.LessonProgress;
import com.college.lms.course.entity.LessonProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByLessonIdAndStudentId(Long lessonId, Long studentId);

    List<LessonProgress> findByStudentIdAndLesson_Module_CourseId(Long studentId, Long courseId);

    @Query("""
            SELECT COUNT(lp) FROM LessonProgress lp
            WHERE lp.student.id = :studentId
              AND lp.lesson.module.course.id = :courseId
              AND lp.status = :status
            """)
    long countByStudentAndCourseAndStatus(@Param("studentId") Long studentId,
                                          @Param("courseId") Long courseId,
                                          @Param("status") LessonProgressStatus status);

    @Query("""
            SELECT COUNT(l) FROM Lesson l
            WHERE l.module.course.id = :courseId AND l.published = true
            """)
    long countPublishedLessonsInCourse(@Param("courseId") Long courseId);
}
