package com.college.lms.institution.repository;

import com.college.lms.institution.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicTermRepository extends JpaRepository<AcademicTerm, Long> {

    List<AcademicTerm> findByDepartmentId(Long departmentId);

    List<AcademicTerm> findByActiveTrue();

    Optional<AcademicTerm> findByActiveTrue(Long departmentId);
}
