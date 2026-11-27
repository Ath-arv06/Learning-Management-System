package com.college.lms.module.certificate.controller;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.certificate.dto.CertificateDto;
import com.college.lms.module.certificate.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/certificates")
@Tag(name = "Certificates & Verification", description = "Endpoints for student digital certificates and public verification")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Operation(summary = "Verify certificate publicly by UUID")
    @GetMapping("/verify/{uuid}")
    public ResponseEntity<CertificateDto> verifyCertificate(@PathVariable String uuid) {
        return ResponseEntity.ok(certificateService.getCertificateByUuid(uuid));
    }

    @Operation(summary = "Get certificates earned by logged-in student")
    @GetMapping("/my-certificates")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CertificateDto>> getMyCertificates(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(certificateService.getCertificatesForStudent(currentUser.getId()));
    }

    @Operation(summary = "Issue a verified completion certificate")
    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CertificateDto> issueCertificate(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam(required = false, defaultValue = "100.0") Double grade,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(certificateService.issueCertificate(studentId, courseId, currentUser.getId(), grade));
    }
}
