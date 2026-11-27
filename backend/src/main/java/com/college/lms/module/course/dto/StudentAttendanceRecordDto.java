package com.college.lms.module.course.dto;

public class StudentAttendanceRecordDto {
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private String status;
    private String remarks;

    public StudentAttendanceRecordDto() {}

    public StudentAttendanceRecordDto(Long studentId, String studentName, String studentIdNumber, String status, String remarks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentIdNumber = studentIdNumber;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
