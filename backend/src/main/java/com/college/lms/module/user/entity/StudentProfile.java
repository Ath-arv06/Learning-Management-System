package com.college.lms.module.user.entity;

import com.college.lms.common.entity.BaseEntity;
import com.college.lms.module.department.entity.Department;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "student_profiles")
public class StudentProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "student_id_number", length = 50, nullable = false, unique = true)
    private String studentIdNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "admission_year", nullable = false)
    private Integer admissionYear;

    @Column(name = "current_semester", nullable = false)
    private Integer currentSemester = 1;

    @Column(name = "cumulative_gpa", precision = 3, scale = 2, nullable = false)
    private BigDecimal cumulativeGpa = BigDecimal.ZERO;

    @Column(name = "total_credits_earned", nullable = false)
    private Integer totalCreditsEarned = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_status", length = 30, nullable = false)
    private AcademicStatus academicStatus = AcademicStatus.GOOD_STANDING;

    public StudentProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Integer getAdmissionYear() { return admissionYear; }
    public void setAdmissionYear(Integer admissionYear) { this.admissionYear = admissionYear; }

    public Integer getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(Integer currentSemester) { this.currentSemester = currentSemester; }

    public BigDecimal getCumulativeGpa() { return cumulativeGpa; }
    public void setCumulativeGpa(BigDecimal cumulativeGpa) { this.cumulativeGpa = cumulativeGpa; }

    public Integer getTotalCreditsEarned() { return totalCreditsEarned; }
    public void setTotalCreditsEarned(Integer totalCreditsEarned) { this.totalCreditsEarned = totalCreditsEarned; }

    public AcademicStatus getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(AcademicStatus academicStatus) { this.academicStatus = academicStatus; }
}
