package com.college.lms.institution.service;

import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import com.college.lms.institution.dto.*;
import com.college.lms.institution.entity.AcademicTerm;
import com.college.lms.institution.entity.Department;
import com.college.lms.institution.repository.AcademicTermRepository;
import com.college.lms.institution.repository.DepartmentRepository;
import com.college.lms.shared.exception.BusinessRuleException;
import com.college.lms.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstitutionServiceImpl implements InstitutionService {

    private final DepartmentRepository departmentRepository;
    private final AcademicTermRepository termRepository;
    private final UserRepository userRepository;

    public InstitutionServiceImpl(DepartmentRepository departmentRepository,
                                  AcademicTermRepository termRepository,
                                  UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.termRepository = termRepository;
        this.userRepository = userRepository;
    }

    // ── Departments ──────────────────────────────────────────────────────────

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new BusinessRuleException("A department with this name already exists.");
        }
        if (departmentRepository.existsByCode(request.code().toUpperCase())) {
            throw new BusinessRuleException("A department with code '" + request.code() + "' already exists.");
        }

        User head = resolveUser(request.headUserId());

        Department dept = new Department(
                request.name().trim(),
                request.code().toUpperCase().trim(),
                request.description(),
                head
        );
        return toDeptResponse(departmentRepository.save(dept));
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department dept = findDept(id);

        // Check uniqueness only if changed
        if (!dept.getName().equals(request.name()) && departmentRepository.existsByName(request.name())) {
            throw new BusinessRuleException("A department with this name already exists.");
        }
        String newCode = request.code().toUpperCase().trim();
        if (!dept.getCode().equals(newCode) && departmentRepository.existsByCode(newCode)) {
            throw new BusinessRuleException("A department with code '" + newCode + "' already exists.");
        }

        dept.setName(request.name().trim());
        dept.setCode(newCode);
        dept.setDescription(request.description());
        dept.setHeadUser(resolveUser(request.headUserId()));
        return toDeptResponse(departmentRepository.save(dept));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartment(Long id) {
        return toDeptResponse(findDept(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments(boolean activeOnly) {
        List<Department> depts = activeOnly
                ? departmentRepository.findByActiveTrue()
                : departmentRepository.findAll();
        return depts.stream().map(this::toDeptResponse).collect(Collectors.toList());
    }

    @Override
    public void deactivateDepartment(Long id) {
        Department dept = findDept(id);
        dept.setActive(false);
        departmentRepository.save(dept);
    }

    // ── Academic Terms ────────────────────────────────────────────────────────

    @Override
    public AcademicTermResponse createTerm(AcademicTermRequest request) {
        if (request.endDate().isBefore(request.startDate()) ||
                request.endDate().isEqual(request.startDate())) {
            throw new BusinessRuleException("End date must be after start date.");
        }

        Department dept = request.departmentId() != null ? findDept(request.departmentId()) : null;

        AcademicTerm term = new AcademicTerm(
                request.name().trim(),
                dept,
                request.startDate(),
                request.endDate(),
                request.active()
        );
        return toTermResponse(termRepository.save(term));
    }

    @Override
    public AcademicTermResponse updateTerm(Long id, AcademicTermRequest request) {
        if (request.endDate().isBefore(request.startDate()) ||
                request.endDate().isEqual(request.startDate())) {
            throw new BusinessRuleException("End date must be after start date.");
        }

        AcademicTerm term = findTerm(id);
        Department dept = request.departmentId() != null ? findDept(request.departmentId()) : null;

        term.setName(request.name().trim());
        term.setDepartment(dept);
        term.setStartDate(request.startDate());
        term.setEndDate(request.endDate());
        term.setActive(request.active());
        return toTermResponse(termRepository.save(term));
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicTermResponse getTerm(Long id) {
        return toTermResponse(findTerm(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicTermResponse> getAllTerms(Long departmentId) {
        List<AcademicTerm> terms = departmentId != null
                ? termRepository.findByDepartmentId(departmentId)
                : termRepository.findAll();
        return terms.stream().map(this::toTermResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicTermResponse> getActiveTerms() {
        return termRepository.findByActiveTrue().stream()
                .map(this::toTermResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Department findDept(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id.toString()));
    }

    private AcademicTerm findTerm(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicTerm", id.toString()));
    }

    private User resolveUser(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private DepartmentResponse toDeptResponse(Department d) {
        return new DepartmentResponse(
                d.getId(),
                d.getName(),
                d.getCode(),
                d.getDescription(),
                d.getHeadUser() != null ? d.getHeadUser().getId() : null,
                d.getHeadUser() != null ? d.getHeadUser().getFullName() : null,
                d.isActive(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    private AcademicTermResponse toTermResponse(AcademicTerm t) {
        return new AcademicTermResponse(
                t.getId(),
                t.getName(),
                t.getDepartment() != null ? t.getDepartment().getId() : null,
                t.getDepartment() != null ? t.getDepartment().getName() : null,
                t.getStartDate(),
                t.getEndDate(),
                t.isActive(),
                t.getCreatedAt()
        );
    }
}
