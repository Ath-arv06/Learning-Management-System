package com.college.lms.module.admin.dto;

public class AdminSystemStatDto {
    private long totalStudents;
    private long totalInstructors;
    private long totalCourses;
    private long totalDepartments;
    private long totalEnrollments;
    private double systemUptimePercentage;
    private String databaseEngine;
    private String activeAcademicTerm;

    public AdminSystemStatDto() {}

    public AdminSystemStatDto(long totalStudents, long totalInstructors, long totalCourses,
                              long totalDepartments, long totalEnrollments, double systemUptimePercentage,
                              String databaseEngine, String activeAcademicTerm) {
        this.totalStudents = totalStudents;
        this.totalInstructors = totalInstructors;
        this.totalCourses = totalCourses;
        this.totalDepartments = totalDepartments;
        this.totalEnrollments = totalEnrollments;
        this.systemUptimePercentage = systemUptimePercentage;
        this.databaseEngine = databaseEngine;
        this.activeAcademicTerm = activeAcademicTerm;
    }

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalInstructors() { return totalInstructors; }
    public void setTotalInstructors(long totalInstructors) { this.totalInstructors = totalInstructors; }

    public long getTotalCourses() { return totalCourses; }
    public void setTotalCourses(long totalCourses) { this.totalCourses = totalCourses; }

    public long getTotalDepartments() { return totalDepartments; }
    public void setTotalDepartments(long totalDepartments) { this.totalDepartments = totalDepartments; }

    public long getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(long totalEnrollments) { this.totalEnrollments = totalEnrollments; }

    public double getSystemUptimePercentage() { return systemUptimePercentage; }
    public void setSystemUptimePercentage(double systemUptimePercentage) { this.systemUptimePercentage = systemUptimePercentage; }

    public String getDatabaseEngine() { return databaseEngine; }
    public void setDatabaseEngine(String databaseEngine) { this.databaseEngine = databaseEngine; }

    public String getActiveAcademicTerm() { return activeAcademicTerm; }
    public void setActiveAcademicTerm(String activeAcademicTerm) { this.activeAcademicTerm = activeAcademicTerm; }
}
