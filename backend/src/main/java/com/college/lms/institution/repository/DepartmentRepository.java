package com.college.lms.institution.repository;

import com.college.lms.institution.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByName(String name);

    boolean existsByCode(String code);

    Optional<Department> findByCode(String code);

    List<Department> findByActiveTrue();
}
