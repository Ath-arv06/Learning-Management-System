export interface EnrolledCourse {
  courseId: number;
  code: string;
  title: string;
  departmentName: string;
  credits: number;
  coverImageUrl: string;
  progressPercentage: number;
  enrollmentStatus: string;
  currentGradeLetter: string;
}

export interface UpcomingAssignment {
  assignmentId: number;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  assignmentTitle: string;
  dueDate: string;
  maxScore: number;
}

export interface StudentDashboardData {
  studentName: string;
  studentIdNumber: string;
  programName: string;
  semester: number;
  cumulativeGpa: number;
  activeCoursesCount: number;
  overallAttendanceRate: number;
  upcomingDeadlinesCount: number;
  enrolledCourses: EnrolledCourse[];
  upcomingDeadlines: UpcomingAssignment[];
}
