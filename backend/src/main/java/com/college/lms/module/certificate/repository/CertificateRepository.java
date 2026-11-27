package com.college.lms.module.certificate.repository;

import com.college.lms.module.certificate.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateUuid(String certificateUuid);
    List<Certificate> findByStudentIdOrderByIssueDateDesc(Long studentId);
    Optional<Certificate> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByCertificateUuid(String certificateUuid);
}
