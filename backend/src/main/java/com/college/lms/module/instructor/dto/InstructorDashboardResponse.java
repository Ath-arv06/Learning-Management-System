package com.college.lms.module.instructor.dto;

import java.util.List;

public class InstructorDashboardResponse {
    private String instructorName;
    private String employeeCode;
    private String departmentName;
    private String designation;
    private Long activeCoursesCount;
    private Long totalEnrolledStudents;
    private Long pendingGradingCount;
    private Double averageClassAttendance;
    private List<InstructorCourseSummaryDto> assignedCourses;
    private List<PendingSubmissionReviewDto> pendingGradingQueue;

    public InstructorDashboardResponse() {}

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Long getActiveCoursesCount() { return activeCoursesCount; }
    public void setActiveCoursesCount(Long activeCoursesCount) { this.activeCoursesCount = activeCoursesCount; }

    public Long getTotalEnrolledStudents() { return totalEnrolledStudents; }
    public void setTotalEnrolledStudents(Long totalEnrolledStudents) { this.totalEnrolledStudents = totalEnrolledStudents; }

    public Long getPendingGradingCount() { return pendingGradingCount; }
    public void setPendingGradingCount(Long pendingGradingCount) { this.pendingGradingCount = pendingGradingCount; }

    public Double getAverageClassAttendance() { return averageClassAttendance; }
    public void setAverageClassAttendance(Double averageClassAttendance) { this.averageClassAttendance = averageClassAttendance; }

    public List<InstructorCourseSummaryDto> getAssignedCourses() { return assignedCourses; }
    public void setAssignedCourses(List<InstructorCourseSummaryDto> assignedCourses) { this.assignedCourses = assignedCourses; }

    public List<PendingSubmissionReviewDto> getPendingGradingQueue() { return pendingGradingQueue; }
    public void setPendingGradingQueue(List<PendingSubmissionReviewDto> pendingGradingQueue) { this.pendingGradingQueue = pendingGradingQueue; }
}
