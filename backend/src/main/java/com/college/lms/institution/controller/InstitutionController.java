package com.college.lms.institution.controller;

import com.college.lms.institution.dto.*;
import com.college.lms.institution.service.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing institution-level entities:
 * departments and academic terms.
 *
 * <p>All mutating operations are restricted to ADMIN role.
 * Read operations are available to any authenticated user.
 */
@RestController
@RequestMapping("/api/v1/institution")
@Tag(name = "Institution", description = "Manage departments and academic terms")
public class InstitutionController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    // ── Departments ──────────────────────────────────────────────────────────

    @GetMapping("/departments")
    @Operation(summary = "List all departments")
    public List<DepartmentResponse> listDepartments(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return institutionService.getAllDepartments(activeOnly);
    }

    @GetMapping("/departments/{id}")
    @Operation(summary = "Get a department by ID")
    public DepartmentResponse getDepartment(@PathVariable Long id) {
        return institutionService.getDepartment(id);
    }

    @PostMapping("/departments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new department (ADMIN only)")
    public DepartmentResponse createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return institutionService.createDepartment(request);
    }

    @PutMapping("/departments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a department (ADMIN only)")
    public DepartmentResponse updateDepartment(@PathVariable Long id,
                                               @Valid @RequestBody DepartmentRequest request) {
        return institutionService.updateDepartment(id, request);
    }

    @DeleteMapping("/departments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a department (ADMIN only)")
    public ResponseEntity<Map<String, String>> deactivateDepartment(@PathVariable Long id) {
        institutionService.deactivateDepartment(id);
        return ResponseEntity.ok(Map.of("message", "Department deactivated successfully."));
    }

    // ── Academic Terms ────────────────────────────────────────────────────────

    @GetMapping("/terms")
    @Operation(summary = "List academic terms, optionally filtered by department")
    public List<AcademicTermResponse> listTerms(
            @RequestParam(required = false) Long departmentId) {
        return institutionService.getAllTerms(departmentId);
    }

    @GetMapping("/terms/active")
    @Operation(summary = "List all active academic terms")
    public List<AcademicTermResponse> listActiveTerms() {
        return institutionService.getActiveTerms();
    }

    @GetMapping("/terms/{id}")
    @Operation(summary = "Get an academic term by ID")
    public AcademicTermResponse getTerm(@PathVariable Long id) {
        return institutionService.getTerm(id);
    }

    @PostMapping("/terms")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new academic term (ADMIN only)")
    public AcademicTermResponse createTerm(@Valid @RequestBody AcademicTermRequest request) {
        return institutionService.createTerm(request);
    }

    @PutMapping("/terms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an academic term (ADMIN only)")
    public AcademicTermResponse updateTerm(@PathVariable Long id,
                                           @Valid @RequestBody AcademicTermRequest request) {
        return institutionService.updateTerm(id, request);
    }
}
