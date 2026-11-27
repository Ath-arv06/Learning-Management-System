package com.college.lms.module.certificate.service;

import com.college.lms.module.certificate.dto.CertificateDto;

import java.util.List;

public interface CertificateService {
    CertificateDto getCertificateByUuid(String uuid);
    List<CertificateDto> getCertificatesForStudent(Long studentId);
    CertificateDto issueCertificate(Long studentId, Long courseId, Long issuerId, Double grade);
}
