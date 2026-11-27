package com.college.lms.module.communication.repository;

import com.college.lms.module.communication.entity.DiscussionThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionThreadRepository extends JpaRepository<DiscussionThread, Long> {
    List<DiscussionThread> findByCourseIdOrderByIsPinnedDescCreatedAtDesc(Long courseId);
}
