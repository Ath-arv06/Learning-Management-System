package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.CourseInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseInstructorRepository extends JpaRepository<CourseInstructor, Long> {

    @Query("SELECT ci FROM CourseInstructor ci JOIN FETCH ci.course c JOIN FETCH c.department WHERE ci.instructor.id = :instructorId")
    List<CourseInstructor> findByInstructorIdWithCourse(@Param("instructorId") Long instructorId);

    long countByInstructorId(Long instructorId);
}
