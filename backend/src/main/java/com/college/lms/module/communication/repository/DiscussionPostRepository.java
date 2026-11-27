package com.college.lms.module.communication.repository;

import com.college.lms.module.communication.entity.DiscussionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionPostRepository extends JpaRepository<DiscussionPost, Long> {
    List<DiscussionPost> findByThreadIdOrderByCreatedAtAsc(Long threadId);
}
