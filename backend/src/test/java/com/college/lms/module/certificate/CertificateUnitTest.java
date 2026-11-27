package com.college.lms.module.certificate;

import com.college.lms.module.certificate.dto.CertificateDto;
import com.college.lms.module.certificate.entity.Certificate;
import com.college.lms.module.certificate.repository.CertificateRepository;
import com.college.lms.module.certificate.service.impl.CertificateServiceImpl;
import com.college.lms.module.course.entity.Course;
import com.college.lms.module.course.repository.CourseRepository;
import com.college.lms.module.user.entity.User;
import com.college.lms.module.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CertificateUnitTest {

    private CertificateServiceImpl certificateService;
    private Certificate sampleCertificate;

    @BeforeEach
    void setUp() {
        User sampleStudent = new User();
        sampleStudent.setId(2L);
        sampleStudent.setFirstName("Alice");
        sampleStudent.setLastName("Student");
        sampleStudent.setEmail("student@optout.edu");

        User sampleIssuer = new User();
        sampleIssuer.setId(3L);
        sampleIssuer.setFirstName("Prof. John");
        sampleIssuer.setLastName("Smith");
        sampleIssuer.setEmail("smith@optout.edu");

        Course sampleCourse = new Course();
        sampleCourse.setId(1L);
        sampleCourse.setCode("CS101");
        sampleCourse.setTitle("Introduction to Computer Science");

        sampleCertificate = new Certificate(
                "CERT-2026-CS101-8F329A",
                sampleStudent,
                sampleCourse,
                sampleIssuer,
                LocalDateTime.now(),
                new BigDecimal("94.50"),
                "a7c9f8e132b4516f4e7d21098acbf561498b31a2c5df817290bcda14ef678912",
                "ACTIVE"
        );
        sampleCertificate.setId(1L);

        CertificateRepository certRepo = (CertificateRepository) Proxy.newProxyInstance(
                CertificateRepository.class.getClassLoader(),
                new Class<?>[]{CertificateRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findByCertificateUuid" -> "CERT-2026-CS101-8F329A".equals(args[0])
                            ? Optional.of(sampleCertificate) : Optional.empty();
                    case "findByStudentIdOrderByIssueDateDesc" -> List.of(sampleCertificate);
                    case "save" -> args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );

        UserRepository userRepo = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        Long id = (Long) args[0];
                        if (id == 2L) return Optional.of(sampleStudent);
                        if (id == 3L) return Optional.of(sampleIssuer);
                        return Optional.empty();
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );

        CourseRepository courseRepo = (CourseRepository) Proxy.newProxyInstance(
                CourseRepository.class.getClassLoader(),
                new Class<?>[]{CourseRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return ((Long) args[0]) == 1L ? Optional.of(sampleCourse) : Optional.empty();
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );

        certificateService = new CertificateServiceImpl(certRepo, userRepo, courseRepo);
    }

    @Test
    @DisplayName("Verify getCertificateByUuid successfully returns matching certificate DTO")
    void shouldReturnCertificateByUuid() {
        CertificateDto result = certificateService.getCertificateByUuid("CERT-2026-CS101-8F329A");

        assertNotNull(result);
        assertEquals("CERT-2026-CS101-8F329A", result.getCertificateUuid());
        assertEquals("Alice Student", result.getStudentName());
        assertEquals("CS101", result.getCourseCode());
        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("Verify getCertificatesForStudent returns list of earned certificates")
    void shouldReturnCertificatesForStudent() {
        List<CertificateDto> results = certificateService.getCertificatesForStudent(2L);

        assertEquals(1, results.size());
        assertEquals("Introduction to Computer Science", results.get(0).getCourseTitle());
    }

    @Test
    @DisplayName("Verify issueCertificate generates cryptographically hashed certificate")
    void shouldIssueNewCertificate() {
        CertificateDto issued = certificateService.issueCertificate(2L, 1L, 3L, 98.0);

        assertNotNull(issued);
        assertTrue(issued.getCertificateUuid().startsWith("CERT-"));
        assertNotNull(issued.getVerificationHash());
        assertFalse(issued.getVerificationHash().isEmpty());
        assertEquals(BigDecimal.valueOf(98.0), issued.getFinalGrade());
    }
}
