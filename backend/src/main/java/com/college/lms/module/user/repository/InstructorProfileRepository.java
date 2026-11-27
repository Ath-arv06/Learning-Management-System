package com.college.lms.module.user.repository;

import com.college.lms.module.user.entity.InstructorProfile;
import com.college.lms.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, Long> {
    Optional<InstructorProfile> findByUser(User user);
    Optional<InstructorProfile> findByUserId(Long userId);
    Optional<InstructorProfile> findByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCode(String employeeCode);
}
