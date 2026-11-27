package com.college.lms.module.user.repository;

import com.college.lms.module.user.entity.StudentProfile;
import com.college.lms.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByUserId(Long userId);
    Optional<StudentProfile> findByStudentIdNumber(String studentIdNumber);
    boolean existsByStudentIdNumber(String studentIdNumber);
}
