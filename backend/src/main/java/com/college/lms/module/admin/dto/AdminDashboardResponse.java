package com.college.lms.module.admin.dto;

import java.util.List;

public class AdminDashboardResponse {
    private AdminSystemStatDto stats;
    private List<RecentUserDto> recentUsers;
    private List<DepartmentEnrollmentDto> departmentDistribution;

    public AdminDashboardResponse() {}

    public AdminDashboardResponse(AdminSystemStatDto stats, List<RecentUserDto> recentUsers, List<DepartmentEnrollmentDto> departmentDistribution) {
        this.stats = stats;
        this.recentUsers = recentUsers;
        this.departmentDistribution = departmentDistribution;
    }

    public AdminSystemStatDto getStats() { return stats; }
    public void setStats(AdminSystemStatDto stats) { this.stats = stats; }

    public List<RecentUserDto> getRecentUsers() { return recentUsers; }
    public void setRecentUsers(List<RecentUserDto> recentUsers) { this.recentUsers = recentUsers; }

    public List<DepartmentEnrollmentDto> getDepartmentDistribution() { return departmentDistribution; }
    public void setDepartmentDistribution(List<DepartmentEnrollmentDto> departmentDistribution) { this.departmentDistribution = departmentDistribution; }
}
