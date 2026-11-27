package com.college.lms.module.certificate.service.impl;

import com.college.lms.common.exception.ResourceNotFoundException;
import com.college.lms.module.certificate.dto.CertificateDto;
import com.college.lms.module.certificate.entity.Certificate;
import com.college.lms.module.certificate.repository.CertificateRepository;
import com.college.lms.module.certificate.service.CertificateService;
import com.college.lms.module.course.entity.Course;
import com.college.lms.module.course.repository.CourseRepository;
import com.college.lms.module.user.entity.User;
import com.college.lms.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final Logger log = LoggerFactory.getLogger(CertificateServiceImpl.class);

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  UserRepository userRepository,
                                  CourseRepository courseRepository) {
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateDto getCertificateByUuid(String uuid) {
        Certificate cert = certificateRepository.findByCertificateUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with UUID: " + uuid));
        return toDto(cert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateDto> getCertificatesForStudent(Long studentId) {
        return certificateRepository.findByStudentIdOrderByIssueDateDesc(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CertificateDto issueCertificate(Long studentId, Long courseId, Long issuerId, Double grade) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        User issuer = userRepository.findById(issuerId)
                .orElseThrow(() -> new ResourceNotFoundException("Issuer not found with ID: " + issuerId));

        String certUuid = "CERT-" + LocalDateTime.now().getYear() + "-" + course.getCode() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String hashInput = certUuid + ":" + student.getId() + ":" + course.getId() + ":" + grade + ":" + LocalDateTime.now();
        String verificationHash = generateSha256(hashInput);

        Certificate certificate = new Certificate(
                certUuid,
                student,
                course,
                issuer,
                LocalDateTime.now(),
                BigDecimal.valueOf(grade != null ? grade : 100.00),
                verificationHash,
                "ACTIVE"
        );

        Certificate saved = certificateRepository.save(certificate);
        log.info("Issued digital certificate {} to student {} for course {}", certUuid, studentId, courseId);
        return toDto(saved);
    }

    private String generateSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    private CertificateDto toDto(Certificate cert) {
        return new CertificateDto(
                cert.getId(),
                cert.getCertificateUuid(),
                cert.getStudent().getId(),
                cert.getStudent().getFullName(),
                cert.getStudent().getEmail(),
                cert.getCourse().getId(),
                cert.getCourse().getCode(),
                cert.getCourse().getTitle(),
                cert.getIssuer().getId(),
                cert.getIssuer().getFullName(),
                cert.getIssueDate(),
                cert.getFinalGrade(),
                cert.getVerificationHash(),
                cert.getStatus(),
                "ACTIVE".equalsIgnoreCase(cert.getStatus())
        );
    }
}
