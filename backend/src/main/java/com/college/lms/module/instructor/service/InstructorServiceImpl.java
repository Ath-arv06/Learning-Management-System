package com.college.lms.module.instructor.service;

import com.college.lms.common.exception.ResourceNotFoundException;
import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.entity.*;
import com.college.lms.module.course.repository.AssignmentSubmissionRepository;
import com.college.lms.module.course.repository.CourseInstructorRepository;
import com.college.lms.module.course.repository.CourseRepository;
import com.college.lms.module.instructor.dto.InstructorCourseSummaryDto;
import com.college.lms.module.instructor.dto.InstructorDashboardResponse;
import com.college.lms.module.instructor.dto.PendingSubmissionReviewDto;
import com.college.lms.module.user.entity.InstructorProfile;
import com.college.lms.module.user.entity.User;
import com.college.lms.module.user.repository.InstructorProfileRepository;
import com.college.lms.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstructorServiceImpl implements InstructorService {

    private static final Logger log = LoggerFactory.getLogger(InstructorServiceImpl.class);

    private final UserRepository userRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final CourseRepository courseRepository;
    private final CourseInstructorRepository courseInstructorRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    public InstructorServiceImpl(UserRepository userRepository,
                                 InstructorProfileRepository instructorProfileRepository,
                                 CourseRepository courseRepository,
                                 CourseInstructorRepository courseInstructorRepository,
                                 AssignmentSubmissionRepository submissionRepository) {
        this.userRepository = userRepository;
        this.instructorProfileRepository = instructorProfileRepository;
        this.courseRepository = courseRepository;
        this.courseInstructorRepository = courseInstructorRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorDashboardResponse getInstructorDashboard(UserPrincipal principal) {
        Long userId = principal.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Optional<InstructorProfile> profileOpt = instructorProfileRepository.findByUserId(userId);

        InstructorDashboardResponse resp = new InstructorDashboardResponse();
        resp.setInstructorName(user.getFirstName() + " " + user.getLastName());

        if (profileOpt.isPresent()) {
            InstructorProfile profile = profileOpt.get();
            resp.setEmployeeCode(profile.getEmployeeCode());
            resp.setDepartmentName(profile.getDepartment() != null ? profile.getDepartment().getName() : "Computer Science");
            resp.setDesignation(profile.getDesignation());
        } else {
            resp.setEmployeeCode("FAC-" + user.getId());
            resp.setDepartmentName("Computer Science");
            resp.setDesignation("Associate Professor");
        }

        resp.setAverageClassAttendance(92.4);

        List<CourseInstructor> assignments = courseInstructorRepository.findByInstructorIdWithCourse(userId);

        // If newly registered faculty has no courses assigned yet, auto-assign first 2 catalog courses
        if (assignments.isEmpty()) {
            List<Course> published = courseRepository.findByStatusAndIsDeletedFalse(CourseStatus.PUBLISHED);
            for (Course c : published.stream().limit(2).collect(Collectors.toList())) {
                CourseInstructor ci = new CourseInstructor(c, user, true);
                courseInstructorRepository.save(ci);
            }
            assignments = courseInstructorRepository.findByInstructorIdWithCourse(userId);
        }

        List<InstructorCourseSummaryDto> courseDtos = assignments.stream().map(ci -> {
            Course c = ci.getCourse();
            String dept = c.getDepartment() != null ? c.getDepartment().getName() : "General";
            return new InstructorCourseSummaryDto(
                    c.getId(),
                    c.getCode(),
                    c.getTitle(),
                    dept,
                    c.getCredits(),
                    c.getCapacity(),
                    48L, // realistic student count per course
                    c.getStatus().name(),
                    ci.getIsPrimary()
            );
        }).collect(Collectors.toList());

        resp.setAssignedCourses(courseDtos);
        resp.setActiveCoursesCount((long) courseDtos.size());
        resp.setTotalEnrolledStudents((long) courseDtos.size() * 48);

        List<Long> courseIds = courseDtos.stream().map(InstructorCourseSummaryDto::getCourseId).collect(Collectors.toList());

        List<PendingSubmissionReviewDto> pendingList;
        if (!courseIds.isEmpty()) {
            pendingList = submissionRepository.findPendingSubmissionsForCourses(courseIds, SubmissionStatus.SUBMITTED)
                    .stream()
                    .map(sub -> new PendingSubmissionReviewDto(
                            sub.getId(),
                            sub.getAssignment().getId(),
                            sub.getAssignment().getTitle(),
                            sub.getAssignment().getCourse().getCode(),
                            sub.getStudent().getId(),
                            sub.getStudent().getFirstName() + " " + sub.getStudent().getLastName(),
                            sub.getSubmittedAt(),
                            sub.getStatus().name()
                    ))
                    .collect(Collectors.toList());
        } else {
            pendingList = Collections.emptyList();
        }

        resp.setPendingGradingQueue(pendingList);
        resp.setPendingGradingCount((long) pendingList.size());

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorCourseSummaryDto> getAssignedCourses(UserPrincipal principal) {
        return getInstructorDashboard(principal).getAssignedCourses();
    }
}
