package com.college.lms.module.admin.service;

import com.college.lms.module.admin.dto.AdminDashboardResponse;
import com.college.lms.module.admin.dto.RecentUserDto;

import java.util.List;

public interface AdminService {
    AdminDashboardResponse getAdminDashboard();
    List<RecentUserDto> getAllUsers();
}
