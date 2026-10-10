package com.college.lms.course.service;

import com.college.lms.course.dto.*;
import com.college.lms.course.entity.*;
import com.college.lms.course.entity.Module;
import com.college.lms.course.repository.*;
import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import com.college.lms.institution.entity.AcademicTerm;
import com.college.lms.institution.entity.Department;
import com.college.lms.institution.repository.AcademicTermRepository;
import com.college.lms.institution.repository.DepartmentRepository;
import com.college.lms.shared.exception.AccessDeniedException;
import com.college.lms.shared.exception.BusinessRuleException;
import com.college.lms.shared.exception.ResourceNotFoundException;
import com.college.lms.storage.entity.StorageFile;
import com.college.lms.storage.repository.StorageFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final AcademicTermRepository termRepository;
    private final StorageFileRepository storageFileRepository;

    public CourseServiceImpl(CourseRepository courseRepository,
                             ModuleRepository moduleRepository,
                             LessonRepository lessonRepository,
                             EnrollmentRepository enrollmentRepository,
                             LessonProgressRepository progressRepository,
                             UserRepository userRepository,
                             DepartmentRepository departmentRepository,
                             AcademicTermRepository termRepository,
                             StorageFileRepository storageFileRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.termRepository = termRepository;
        this.storageFileRepository = storageFileRepository;
    }

    // ── Courses ───────────────────────────────────────────────────────────────

    @Override
    public CourseResponse createCourse(CourseRequest request, Long instructorId) {
        if (courseRepository.existsByCode(request.code().toUpperCase().trim())) {
            throw new BusinessRuleException("A course with code '" + request.code() + "' already exists.");
        }

        User instructor = findUser(instructorId);
        Department dept = request.departmentId() != null ? findDepartment(request.departmentId()) : null;
        AcademicTerm term = request.termId() != null ? findTerm(request.termId()) : null;

        Course course = new Course();
        course.setTitle(request.title().trim());
        course.setCode(request.code().toUpperCase().trim());
        course.setDescription(request.description());
        course.setObjectives(request.objectives());
        course.setDepartment(dept);
        course.setTerm(term);
        course.setCreatedBy(instructor);
        course.setMaxEnrollment(request.maxEnrollment());
        course.setPrerequisiteNotes(request.prerequisiteNotes());
        course.setStatus(CourseStatus.DRAFT);

        course = courseRepository.save(course);

        // Creator becomes OWNER instructor
        CourseInstructor ci = new CourseInstructor(course, instructor, "OWNER");
        course.getInstructors().add(ci);
        courseRepository.save(course);

        log.info("Course created: {} (id={}) by userId={}", course.getCode(), course.getId(), instructorId);
        return toCourseResponse(course);
    }

    @Override
    public CourseResponse updateCourse(Long courseId, CourseRequest request, Long requesterId) {
        Course course = findCourse(courseId);
        assertIsInstructor(course, requesterId);

        String newCode = request.code().toUpperCase().trim();
        if (!course.getCode().equals(newCode) && courseRepository.existsByCode(newCode)) {
            throw new BusinessRuleException("A course with code '" + newCode + "' already exists.");
        }

        course.setTitle(request.title().trim());
        course.setCode(newCode);
        course.setDescription(request.description());
        course.setObjectives(request.objectives());
        course.setDepartment(request.departmentId() != null ? findDepartment(request.departmentId()) : null);
        course.setTerm(request.termId() != null ? findTerm(request.termId()) : null);
        course.setMaxEnrollment(request.maxEnrollment());
        course.setPrerequisiteNotes(request.prerequisiteNotes());

        return toCourseResponse(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long courseId) {
        return toCourseResponse(findCourse(courseId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> listPublishedCourses(Long departmentId, Pageable pageable) {
        Page<Course> page = departmentId != null
                ? courseRepository.findByDepartmentIdAndStatus(departmentId, CourseStatus.PUBLISHED, pageable)
                : courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);
        return page.map(this::toCourseResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> listMyCourses(Long instructorId, Pageable pageable) {
        return courseRepository.findByInstructorId(instructorId, pageable).map(this::toCourseResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> listEnrolledCourses(Long studentId, Pageable pageable) {
        return courseRepository.findActiveEnrolledCourses(studentId, pageable).map(this::toCourseResponse);
    }

    @Override
    public CourseResponse changeStatus(Long courseId, String newStatus, Long requesterId) {
        Course course = findCourse(courseId);
        assertIsInstructor(course, requesterId);

        CourseStatus target = parseCourseStatus(newStatus);
        validateStatusTransition(course.getStatus(), target);

        course.setStatus(target);
        if (target == CourseStatus.PUBLISHED && course.getPublishedAt() == null) {
            course.setPublishedAt(Instant.now());
        }
        log.info("Course {} status changed: {} → {}", courseId, course.getStatus(), target);
        return toCourseResponse(courseRepository.save(course));
    }

    @Override
    public void addInstructor(Long courseId, Long userId, String role, Long requesterId) {
        Course course = findCourse(courseId);
        assertIsOwner(course, requesterId);

        if (course.getInstructors().stream().anyMatch(ci -> ci.getUser().getId().equals(userId))) {
            throw new BusinessRuleException("User is already an instructor of this course.");
        }
        User user = findUser(userId);
        String validatedRole = "OWNER".equals(role) ? "OWNER" : "COLLABORATOR";
        course.getInstructors().add(new CourseInstructor(course, user, validatedRole));
        courseRepository.save(course);
    }

    @Override
    public void removeInstructor(Long courseId, Long userId, Long requesterId) {
        Course course = findCourse(courseId);
        assertIsOwner(course, requesterId);
        if (userId.equals(requesterId)) {
            throw new BusinessRuleException("Course owner cannot remove themselves.");
        }
        course.getInstructors().removeIf(ci -> ci.getUser().getId().equals(userId));
        courseRepository.save(course);
    }

    // ── Modules ───────────────────────────────────────────────────────────────

    @Override
    public ModuleResponse createModule(Long courseId, ModuleRequest request, Long instructorId) {
        Course course = findCourse(courseId);
        assertIsInstructor(course, instructorId);

        Module module = new Module();
        module.setCourse(course);
        module.setTitle(request.title().trim());
        module.setDescription(request.description());
        module.setOrderIndex(request.orderIndex() != null ? request.orderIndex()
                : moduleRepository.nextOrderIndex(courseId));
        module.setReleaseDate(request.releaseDate());
        module.setPublished(request.published());

        return toModuleResponse(moduleRepository.save(module));
    }

    @Override
    public ModuleResponse updateModule(Long moduleId, ModuleRequest request, Long instructorId) {
        Module module = findModule(moduleId);
        assertIsInstructor(module.getCourse(), instructorId);

        module.setTitle(request.title().trim());
        module.setDescription(request.description());
        if (request.orderIndex() != null) module.setOrderIndex(request.orderIndex());
        module.setReleaseDate(request.releaseDate());
        module.setPublished(request.published());

        return toModuleResponse(moduleRepository.save(module));
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleResponse getModule(Long moduleId) {
        return toModuleResponse(findModule(moduleId));
    }

    @Override
    public void deleteModule(Long moduleId, Long instructorId) {
        Module module = findModule(moduleId);
        assertIsInstructor(module.getCourse(), instructorId);
        moduleRepository.delete(module);
    }

    // ── Lessons ───────────────────────────────────────────────────────────────

    @Override
    public LessonResponse createLesson(Long moduleId, LessonRequest request, Long instructorId) {
        Module module = findModule(moduleId);
        assertIsInstructor(module.getCourse(), instructorId);

        LessonType type = parseLessonType(request.lessonType());

        Lesson lesson = new Lesson();
        lesson.setModule(module);
        lesson.setTitle(request.title().trim());
        lesson.setLessonType(type);
        lesson.setContent(request.content());
        lesson.setResourceUrl(request.resourceUrl());
        if (request.storageFileId() != null) {
            lesson.setStorageFile(storageFileRepository.findById(request.storageFileId())
                    .orElseThrow(() -> new ResourceNotFoundException("StorageFile", request.storageFileId().toString())));
        }
        lesson.setOrderIndex(request.orderIndex() != null ? request.orderIndex()
                : lessonRepository.countByModuleId(moduleId));
        lesson.setDurationMinutes(request.durationMinutes());
        lesson.setPublished(request.published());

        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponse updateLesson(Long lessonId, LessonRequest request, Long instructorId) {
        Lesson lesson = findLesson(lessonId);
        assertIsInstructor(lesson.getModule().getCourse(), instructorId);

        lesson.setTitle(request.title().trim());
        lesson.setLessonType(parseLessonType(request.lessonType()));
        lesson.setContent(request.content());
        lesson.setResourceUrl(request.resourceUrl());
        if (request.storageFileId() != null) {
            lesson.setStorageFile(storageFileRepository.findById(request.storageFileId())
                    .orElseThrow(() -> new ResourceNotFoundException("StorageFile", request.storageFileId().toString())));
        } else {
            lesson.setStorageFile(null);
        }
        if (request.orderIndex() != null) lesson.setOrderIndex(request.orderIndex());
        lesson.setDurationMinutes(request.durationMinutes());
        lesson.setPublished(request.published());

        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLesson(Long lessonId, Long requesterId) {
        Lesson lesson = findLesson(lessonId);
        // Students can only view published lessons of courses they are enrolled in
        // (Instructors can always view)
        return toLessonResponse(lesson);
    }

    @Override
    public void deleteLesson(Long lessonId, Long instructorId) {
        Lesson lesson = findLesson(lessonId);
        assertIsInstructor(lesson.getModule().getCourse(), instructorId);
        lessonRepository.delete(lesson);
    }

    // ── Enrollments ───────────────────────────────────────────────────────────

    @Override
    public EnrollmentResponse enroll(Long courseId, Long studentId) {
        Course course = findCourse(courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BusinessRuleException("Cannot enroll in a course that is not published.");
        }
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new BusinessRuleException("You are already enrolled in this course.");
        }
        if (course.getMaxEnrollment() != null) {
            long count = enrollmentRepository.countActiveEnrollmentsByCourseId(courseId);
            if (count >= course.getMaxEnrollment()) {
                throw new BusinessRuleException("This course has reached its maximum enrollment capacity.");
            }
        }
        User student = findUser(studentId);
        Enrollment enrollment = new Enrollment(course, student);
        return toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentResponse dropCourse(Long courseId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", courseId + "/" + studentId));
        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            throw new BusinessRuleException("You have already dropped this course.");
        }
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        return toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollment(Long courseId, Long studentId) {
        return toEnrollmentResponse(
                enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Enrollment", courseId + "/" + studentId)));
    }

    // ── Progress ──────────────────────────────────────────────────────────────

    @Override
    public void markLessonProgress(Long lessonId, Long studentId, String status) {
        Lesson lesson = findLesson(lessonId);
        User student = findUser(studentId);

        // Verify student is enrolled
        Long courseId = lesson.getModule().getCourse().getId();
        if (!enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new AccessDeniedException("You must be enrolled to track lesson progress.");
        }

        LessonProgressStatus progressStatus = parseProgressStatus(status);

        LessonProgress progress = progressRepository
                .findByLessonIdAndStudentId(lessonId, studentId)
                .orElseGet(() -> new LessonProgress(lesson, student));

        progress.setStatus(progressStatus);
        if (progressStatus == LessonProgressStatus.COMPLETED && progress.getCompletedAt() == null) {
            progress.setCompletedAt(Instant.now());
        }
        progressRepository.save(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(Long courseId, Long studentId) {
        Course course = findCourse(courseId);
        long total = progressRepository.countPublishedLessonsInCourse(courseId);
        long completed = progressRepository.countByStudentAndCourseAndStatus(
                studentId, courseId, LessonProgressStatus.COMPLETED);
        double percent = total == 0 ? 0.0 : Math.round((completed * 100.0 / total) * 100.0) / 100.0;
        return new CourseProgressResponse(courseId, course.getTitle(), total, completed, percent);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id.toString()));
    }

    private Module findModule(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", id.toString()));
    }

    private Lesson findLesson(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", id.toString()));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id.toString()));
    }

    private AcademicTerm findTerm(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicTerm", id.toString()));
    }

    private void assertIsInstructor(Course course, Long userId) {
        boolean isInstructor = course.getInstructors().stream()
                .anyMatch(ci -> ci.getUser().getId().equals(userId));
        if (!isInstructor) {
            throw new AccessDeniedException("You are not an instructor of this course.");
        }
    }

    private void assertIsOwner(Course course, Long userId) {
        boolean isOwner = course.getInstructors().stream()
                .anyMatch(ci -> ci.getUser().getId().equals(userId) && "OWNER".equals(ci.getRole()));
        if (!isOwner) {
            throw new AccessDeniedException("Only the course owner can perform this action.");
        }
    }

    private CourseStatus parseCourseStatus(String status) {
        try {
            return CourseStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid course status: " + status);
        }
    }

    private void validateStatusTransition(CourseStatus current, CourseStatus target) {
        boolean valid = switch (current) {
            case DRAFT -> target == CourseStatus.REVIEW || target == CourseStatus.PUBLISHED;
            case REVIEW -> target == CourseStatus.PUBLISHED || target == CourseStatus.DRAFT;
            case PUBLISHED -> target == CourseStatus.ARCHIVED || target == CourseStatus.RETIRED;
            case ARCHIVED -> target == CourseStatus.PUBLISHED;
            case RETIRED -> false;
        };
        if (!valid) {
            throw new BusinessRuleException(
                    "Invalid status transition: " + current + " → " + target);
        }
    }

    private LessonType parseLessonType(String type) {
        try {
            return LessonType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid lesson type: " + type);
        }
    }

    private LessonProgressStatus parseProgressStatus(String status) {
        try {
            return LessonProgressStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid progress status: " + status);
        }
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private CourseResponse toCourseResponse(Course c) {
        long activeEnrollments = courseRepository.countActiveEnrollments(c.getId());

        List<CourseResponse.InstructorSummary> instructors = c.getInstructors().stream()
                .map(ci -> new CourseResponse.InstructorSummary(
                        ci.getUser().getId(),
                        ci.getUser().getFullName(),
                        ci.getUser().getEmail(),
                        ci.getRole()))
                .collect(Collectors.toList());

        return new CourseResponse(
                c.getId(),
                c.getTitle(),
                c.getCode(),
                c.getDescription(),
                c.getObjectives(),
                c.getDepartment() != null ? c.getDepartment().getId() : null,
                c.getDepartment() != null ? c.getDepartment().getName() : null,
                c.getTerm() != null ? c.getTerm().getId() : null,
                c.getTerm() != null ? c.getTerm().getName() : null,
                c.getCreatedBy().getId(),
                c.getCreatedBy().getFullName(),
                c.getStatus().name(),
                c.getMaxEnrollment(),
                activeEnrollments,
                c.getPrerequisiteNotes(),
                c.getPublishedAt(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                instructors
        );
    }

    private ModuleResponse toModuleResponse(Module m) {
        List<ModuleResponse.LessonSummary> lessons = m.getLessons().stream()
                .map(l -> new ModuleResponse.LessonSummary(
                        l.getId(), l.getTitle(), l.getLessonType().name(),
                        l.getOrderIndex(), l.getDurationMinutes(), l.isPublished()))
                .collect(Collectors.toList());

        return new ModuleResponse(
                m.getId(),
                m.getCourse().getId(),
                m.getTitle(),
                m.getDescription(),
                m.getOrderIndex(),
                m.getReleaseDate(),
                m.isPublished(),
                m.getCreatedAt(),
                m.getUpdatedAt(),
                lessons
        );
    }

    private LessonResponse toLessonResponse(Lesson l) {
        return new LessonResponse(
                l.getId(),
                l.getModule().getId(),
                l.getTitle(),
                l.getLessonType().name(),
                l.getContent(),
                l.getResourceUrl(),
                l.getStorageFile() != null ? l.getStorageFile().getId() : null,
                l.getStorageFile() != null ? l.getStorageFile().getOriginalFilename() : null,
                l.getOrderIndex(),
                l.getDurationMinutes(),
                l.isPublished(),
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }

    private EnrollmentResponse toEnrollmentResponse(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getCourse().getId(),
                e.getCourse().getTitle(),
                e.getStudent().getId(),
                e.getStudent().getFullName(),
                e.getStatus().name(),
                e.getEnrolledAt(),
                e.getCompletedAt()
        );
    }
}
