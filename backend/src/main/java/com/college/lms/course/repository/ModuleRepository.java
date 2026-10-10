package com.college.lms.course.repository;

import com.college.lms.course.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    /** Next order index for a new module in a course. */
    default int nextOrderIndex(Long courseId) {
        return findByCourseIdOrderByOrderIndexAsc(courseId).size();
    }
}
