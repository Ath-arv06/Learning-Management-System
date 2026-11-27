package com.college.lms.module.admin.dto;

import java.time.Instant;

public class RecentUserDto {
    private Long id;
    private String fullName;
    private String email;
    private String primaryRole;
    private String departmentName;
    private boolean active;
    private Instant createdAt;

    public RecentUserDto() {}

    public RecentUserDto(Long id, String fullName, String email, String primaryRole,
                         String departmentName, boolean active, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.primaryRole = primaryRole;
        this.departmentName = departmentName;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrimaryRole() { return primaryRole; }
    public void setPrimaryRole(String primaryRole) { this.primaryRole = primaryRole; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
