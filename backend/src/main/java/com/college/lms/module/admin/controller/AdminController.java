package com.college.lms.module.admin.controller;

import com.college.lms.module.admin.dto.AdminDashboardResponse;
import com.college.lms.module.admin.dto.RecentUserDto;
import com.college.lms.module.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        AdminDashboardResponse response = adminService.getAdminDashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<RecentUserDto>> getUsers() {
        List<RecentUserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
