export interface AttendanceSession {
  id: number;
  courseId: number;
  courseCode: string;
  sessionDate: string;
  startTime: string;
  endTime: string;
  topic: string;
  totalPresent: number;
  totalStudents: number;
}

export interface StudentAttendanceRecord {
  studentId: number;
  studentName: string;
  studentIdNumber: string;
  status: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED';
  remarks: string | null;
}

export interface GpaDistributionBucket {
  range: string;
  count: number;
}

export interface CourseAnalytics {
  courseId: number;
  courseCode: string;
  courseTitle: string;
  averageAttendanceRate: number;
  averageGradePercentage: number;
  totalEnrolledStudents: number;
  atRiskStudentsCount: number;
  gradeDistribution: GpaDistributionBucket[];
}
