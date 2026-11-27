package com.college.lms.module.course.dto;

import java.util.List;

public class CourseClassroomResponse {
    private Long courseId;
    private String code;
    private String title;
    private String description;
    private String departmentName;
    private Integer credits;
    private List<ModuleWithLessonsDto> modules;

    public CourseClassroomResponse() {}

    public CourseClassroomResponse(Long courseId, String code, String title, String description,
                                   String departmentName, Integer credits, List<ModuleWithLessonsDto> modules) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.description = description;
        this.departmentName = departmentName;
        this.credits = credits;
        this.modules = modules;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public List<ModuleWithLessonsDto> getModules() { return modules; }
    public void setModules(List<ModuleWithLessonsDto> modules) { this.modules = modules; }
}
