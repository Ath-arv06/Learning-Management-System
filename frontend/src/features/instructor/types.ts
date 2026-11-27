export interface InstructorCourse {
  courseId: number;
  code: string;
  title: string;
  departmentName: string;
  credits: number;
  capacity: number;
  enrolledStudentsCount: number;
  status: string;
  isPrimary: boolean;
}

export interface PendingSubmission {
  submissionId: number;
  assignmentId: number;
  assignmentTitle: string;
  courseCode: string;
  studentId: number;
  studentName: string;
  submittedAt: string;
  status: string;
}

export interface InstructorDashboardData {
  instructorName: string;
  employeeCode: string;
  departmentName: string;
  designation: string;
  activeCoursesCount: number;
  totalEnrolledStudents: number;
  pendingGradingCount: number;
  averageClassAttendance: number;
  assignedCourses: InstructorCourse[];
  pendingGradingQueue: PendingSubmission[];
}
