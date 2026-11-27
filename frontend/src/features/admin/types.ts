export interface AdminSystemStats {
  totalStudents: number;
  totalInstructors: number;
  totalCourses: number;
  totalDepartments: number;
  totalEnrollments: number;
  systemUptimePercentage: number;
  databaseEngine: string;
  activeAcademicTerm: string;
}

export interface RecentUser {
  id: number;
  fullName: string;
  email: string;
  primaryRole: string;
  departmentName: string;
  active: boolean;
  createdAt: string;
}

export interface DepartmentDistribution {
  departmentName: string;
  code: string;
  courseCount: number;
  facultyCount: number;
}

export interface AdminDashboardData {
  stats: AdminSystemStats;
  recentUsers: RecentUser[];
  departmentDistribution: DepartmentDistribution[];
}
