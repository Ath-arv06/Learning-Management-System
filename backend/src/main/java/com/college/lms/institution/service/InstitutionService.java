package com.college.lms.institution.service;

import com.college.lms.institution.dto.*;
import java.util.List;

public interface InstitutionService {

    // ── Departments ──────────────────────────────────────────────────────────

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);

    DepartmentResponse getDepartment(Long id);

    List<DepartmentResponse> getAllDepartments(boolean activeOnly);

    void deactivateDepartment(Long id);

    // ── Academic Terms ────────────────────────────────────────────────────────

    AcademicTermResponse createTerm(AcademicTermRequest request);

    AcademicTermResponse updateTerm(Long id, AcademicTermRequest request);

    AcademicTermResponse getTerm(Long id);

    List<AcademicTermResponse> getAllTerms(Long departmentId);

    List<AcademicTermResponse> getActiveTerms();
}
