package com.college.lms.module.admin.dto;

public class DepartmentEnrollmentDto {
    private String departmentName;
    private String code;
    private long courseCount;
    private long facultyCount;

    public DepartmentEnrollmentDto() {}

    public DepartmentEnrollmentDto(String departmentName, String code, long courseCount, long facultyCount) {
        this.departmentName = departmentName;
        this.code = code;
        this.courseCount = courseCount;
        this.facultyCount = facultyCount;
    }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public long getCourseCount() { return courseCount; }
    public void setCourseCount(long courseCount) { this.courseCount = courseCount; }

    public long getFacultyCount() { return facultyCount; }
    public void setFacultyCount(long facultyCount) { this.facultyCount = facultyCount; }
}
