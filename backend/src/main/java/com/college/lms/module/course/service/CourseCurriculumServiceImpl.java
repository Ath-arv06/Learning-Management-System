package com.college.lms.module.course.service;

import com.college.lms.common.exception.ResourceNotFoundException;
import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.dto.CourseClassroomResponse;
import com.college.lms.module.course.dto.LessonDetailDto;
import com.college.lms.module.course.dto.LessonMaterialDto;
import com.college.lms.module.course.dto.ModuleWithLessonsDto;
import com.college.lms.module.course.entity.*;
import com.college.lms.module.course.repository.*;
import com.college.lms.module.user.entity.User;
import com.college.lms.module.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseCurriculumServiceImpl implements CourseCurriculumService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final LessonMaterialRepository materialRepository;
    private final StudentLessonProgressRepository progressRepository;
    private final UserRepository userRepository;

    public CourseCurriculumServiceImpl(CourseRepository courseRepository,
                                       CourseModuleRepository moduleRepository,
                                       LessonRepository lessonRepository,
                                       LessonMaterialRepository materialRepository,
                                       StudentLessonProgressRepository progressRepository,
                                       UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.materialRepository = materialRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseClassroomResponse getCourseClassroom(Long courseId, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        List<CourseModule> modules = moduleRepository.findByCourseIdAndIsPublishedTrueOrderBySortOrderAsc(courseId);

        // If newly queried course has no modules yet, seed default module and lesson
        if (modules.isEmpty()) {
            CourseModule defaultModule = new CourseModule(course, "Module 1: Course Introduction & Core Principles",
                    "Overview of course syllabus, learning outcomes, and setup.", 1);
            defaultModule = moduleRepository.save(defaultModule);

            Lesson defaultLesson = new Lesson(defaultModule, "1.1 Welcome to the Course",
                    LessonContentType.VIDEO, "https://www.w3schools.com/html/mov_bbb.mp4", 18, 1);
            lessonRepository.save(defaultLesson);

            modules = moduleRepository.findByCourseIdAndIsPublishedTrueOrderBySortOrderAsc(courseId);
        }

        Long userId = principal != null ? principal.getId() : null;

        List<ModuleWithLessonsDto> moduleDtos = modules.stream().map(m -> {
            List<Lesson> lessons = lessonRepository.findByModuleIdAndIsPublishedTrueOrderBySortOrderAsc(m.getId());
            List<LessonDetailDto> lessonDtos = lessons.stream().map(l -> {
                boolean completed = false;
                if (userId != null) {
                    Optional<StudentLessonProgress> prog = progressRepository.findByStudentIdAndLessonId(userId, l.getId());
                    completed = prog.isPresent() && Boolean.TRUE.equals(prog.get().getIsCompleted());
                }

                List<LessonMaterialDto> materialDtos = materialRepository.findByLessonId(l.getId()).stream()
                        .map(mat -> new LessonMaterialDto(mat.getId(), mat.getTitle(), mat.getFileUrl(), mat.getFileType(), mat.getFileSizeBytes()))
                        .collect(Collectors.toList());

                return new LessonDetailDto(
                        l.getId(),
                        l.getTitle(),
                        l.getContentType().name(),
                        l.getContentBody(),
                        l.getVideoUrl(),
                        l.getDurationMinutes(),
                        l.getSortOrder(),
                        completed,
                        materialDtos
                );
            }).collect(Collectors.toList());

            return new ModuleWithLessonsDto(m.getId(), m.getTitle(), m.getDescription(), m.getSortOrder(), lessonDtos);
        }).collect(Collectors.toList());

        String deptName = course.getDepartment() != null ? course.getDepartment().getName() : "General";
        return new CourseClassroomResponse(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.getDescription(),
                deptName,
                course.getCredits(),
                moduleDtos
        );
    }

    @Override
    @Transactional
    public void markLessonComplete(Long lessonId, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + lessonId));

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));

        StudentLessonProgress progress = progressRepository.findByStudentIdAndLessonId(user.getId(), lessonId)
                .orElseGet(() -> new StudentLessonProgress(user, lesson, true));

        progress.setIsCompleted(true);
        progressRepository.save(progress);
    }
}
