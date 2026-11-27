import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Link, useNavigate } from 'react-router-dom';
import { apiClient } from './lib/api-client';
import { AuthProvider, useAuth } from './features/auth/AuthContext';
import { LoginPage } from './features/auth/LoginPage';
import { RegisterPage } from './features/auth/RegisterPage';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { StudentLayout } from './features/student/layout/StudentLayout';
import { StudentDashboardPage } from './features/student/pages/StudentDashboardPage';
import { StudentCoursesPage } from './features/student/pages/StudentCoursesPage';
import { InstructorLayout } from './features/instructor/layout/InstructorLayout';
import { InstructorDashboardPage } from './features/instructor/pages/InstructorDashboardPage';
import { InstructorCoursesPage } from './features/instructor/pages/InstructorCoursesPage';
import { AdminLayout } from './features/admin/layout/AdminLayout';
import { AdminDashboardPage } from './features/admin/pages/AdminDashboardPage';
import { AdminUsersPage } from './features/admin/pages/AdminUsersPage';
import { CourseClassroomPage } from './features/course/pages/CourseClassroomPage';
import { QuizRunnerPage } from './features/quiz/pages/QuizRunnerPage';
import { FacultyAttendancePage } from './features/attendance/pages/FacultyAttendancePage';
import { FacultyAnalyticsPage } from './features/attendance/pages/FacultyAnalyticsPage';
import { StudentAiTutorPage } from './features/ai/pages/StudentAiTutorPage';
import { FacultyAiQuestionGeneratorPage } from './features/ai/pages/FacultyAiQuestionGeneratorPage';
import { DiscussionsPage } from './features/communication/pages/DiscussionsPage';
import { CertificateViewerPage } from './features/certificate/pages/CertificateViewerPage';
import {
  GraduationCap,
  BookOpen,
  Users,
  ShieldCheck,
  Server,
  CheckCircle2,
  ArrowRight,
  Sparkles,
  LogOut,
  User as UserIcon,
  LayoutDashboard
} from 'lucide-react';

interface BackendHealth {
  status: string;
  serviceName: string;
  version: string;
  timestamp: string;
  message: string;
}







const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [health, setHealth] = useState<BackendHealth | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get<BackendHealth>('/health')
      .then((res) => setHealth(res.data))
      .catch(() => setHealth(null))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col">
      {/* Top Navbar */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-indigo-600 flex items-center justify-center text-white shadow-md">
              <GraduationCap className="w-6 h-6" />
            </div>
            <div>
              <span className="font-bold text-lg text-slate-900 tracking-tight">Opt-Out LMS</span>
              <span className="ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-indigo-50 text-indigo-700 border border-indigo-100">
                Phase 2 Auth Ready
              </span>
            </div>
          </div>

          <div className="flex items-center space-x-4">
            <div className="hidden sm:flex items-center space-x-2 text-xs text-slate-500">
              <Server className="w-4 h-4 text-emerald-600" />
              <span>
                Backend API:{' '}
                {loading ? (
                  <span className="text-amber-500 font-medium">Checking...</span>
                ) : health?.status === 'UP' ? (
                  <span className="text-emerald-600 font-semibold">Online ({health.serviceName})</span>
                ) : (
                  <span className="text-slate-400 font-medium">Standby</span>
                )}
              </span>
            </div>

            {user ? (
              <div className="flex items-center space-x-3">
                <div className="flex items-center space-x-2 text-xs font-medium text-slate-700 bg-slate-100 px-3 py-1.5 rounded-lg border border-slate-200">
                  <UserIcon className="w-3.5 h-3.5 text-indigo-600" />
                  <span>{user.fullName} ({user.roles[0]?.replace('ROLE_', '')})</span>
                </div>
                {user.roles.includes('ROLE_STUDENT') && (
                  <Link to="/student" className="p-1.5 text-indigo-600 hover:text-indigo-800 rounded-lg hover:bg-slate-100" title="Student Dashboard">
                    <LayoutDashboard className="w-4 h-4" />
                  </Link>
                )}
                {user.roles.includes('ROLE_INSTRUCTOR') && (
                  <Link to="/instructor" className="p-1.5 text-indigo-600 hover:text-indigo-800 rounded-lg hover:bg-slate-100" title="Instructor Dashboard">
                    <LayoutDashboard className="w-4 h-4" />
                  </Link>
                )}
                {user.roles.includes('ROLE_ADMIN') && (
                  <Link to="/admin" className="p-1.5 text-indigo-600 hover:text-indigo-800 rounded-lg hover:bg-slate-100" title="Admin Dashboard">
                    <LayoutDashboard className="w-4 h-4" />
                  </Link>
                )}
                <button
                  onClick={logout}
                  className="p-1.5 text-slate-400 hover:text-slate-600 rounded-lg hover:bg-slate-100 transition"
                  title="Sign Out"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-2">
                <button
                  onClick={() => navigate('/login')}
                  className="px-3.5 py-1.5 text-xs font-semibold text-slate-700 hover:text-slate-900 transition"
                >
                  Sign In
                </button>
                <button
                  onClick={() => navigate('/register')}
                  className="px-3.5 py-1.5 text-xs font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition shadow-sm"
                >
                  Register
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-indigo-50 border border-indigo-100 text-indigo-700 text-xs font-semibold mb-4">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Secure Authentication & Role Separation</span>
          </div>
          <h1 className="text-4xl font-extrabold text-slate-900 tracking-tight sm:text-5xl">
            University Learning Management System
          </h1>
          <p className="mt-4 text-lg text-slate-600 leading-relaxed">
            Stateless JWT authentication with HttpOnly cookies, Google Sign-In, and strict role authorization.
          </p>
        </div>

        {/* 3 Role Portals Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          <Link to="/student" className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition block group">
            <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center mb-5 group-hover:scale-105 transition">
              <BookOpen className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-bold text-slate-900 mb-2">Student Portal</h3>
            <p className="text-sm text-slate-600 mb-4 leading-relaxed">
              Enrolled courses, interactive video lectures, timed quizzes, assignment submissions, and conversational AI tutor.
            </p>
            <ul className="text-xs text-slate-500 space-y-2 mb-6">
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> Live Timetable & Calendar</li>
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> Verified Digital Certificates</li>
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> Grounded Course Q&A</li>
            </ul>
            <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs font-semibold text-blue-600">
              <span>Enter Student Portal</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </Link>

          <Link to="/instructor" className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition block group">
            <div className="w-12 h-12 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center mb-5 group-hover:scale-105 transition">
              <Users className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-bold text-slate-900 mb-2">Instructor Portal</h3>
            <p className="text-sm text-slate-600 mb-4 leading-relaxed">
              Curriculum design studio, multi-format question banks, split-screen grading queue, and AI-assisted rubric evaluation.
            </p>
            <ul className="text-xs text-slate-500 space-y-2 mb-6">
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> Syllabus & Resource Uploader</li>
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> AI Draft Feedback (Human-in-Loop)</li>
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> Attendance Session Logger</li>
            </ul>
            <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs font-semibold text-indigo-600">
              <span>Enter Instructor Portal</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </Link>

          <Link to="/admin" className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition block group">
            <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center mb-5 group-hover:scale-105 transition">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-bold text-slate-900 mb-2">Administrator Portal</h3>
            <p className="text-sm text-slate-600 mb-4 leading-relaxed">
              Department governance, academic term calendar, bulk student/faculty imports, and immutable security audit stream.
            </p>
            <ul className="text-xs text-slate-500 space-y-2 mb-6">
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> RBAC & Account Management</li>
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> University Academic Analytics</li>
              <li className="flex items-center"><CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2" /> Flyway & System Telemetry</li>
            </ul>
            <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs font-semibold text-purple-600">
              <span>Enter Administrator Portal</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </Link>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-200 bg-white py-6">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-xs text-slate-400">
          Opt-Out University Learning Management System &copy; 2026. Enterprise Academic Architecture.
        </div>
      </footer>
    </div>
  );
};

const UnauthorizedPage: React.FC = () => (
  <div className="min-h-screen flex items-center justify-center bg-slate-50 px-4">
    <div className="text-center max-w-md bg-white p-8 rounded-2xl border border-slate-200 shadow-sm">
      <div className="w-12 h-12 rounded-xl bg-rose-50 text-rose-600 flex items-center justify-center mx-auto mb-4">
        <ShieldCheck className="w-6 h-6" />
      </div>
      <h2 className="text-2xl font-bold text-slate-900">403 — Unauthorized Access</h2>
      <p className="mt-2 text-sm text-slate-600">You do not have the required institutional role to access this portal.</p>
      <Link to="/" className="mt-6 inline-block px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm font-semibold hover:bg-indigo-700 transition">
        Return to Home
      </Link>
    </div>
  </div>
);

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />
          <Route
            path="/student"
            element={
              <ProtectedRoute requiredRole="ROLE_STUDENT">
                <StudentLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<StudentDashboardPage />} />
            <Route path="courses" element={<StudentCoursesPage />} />
            <Route path="courses/:courseId" element={<CourseClassroomPage />} />
            <Route path="quizzes/:quizId" element={<QuizRunnerPage />} />
            <Route path="assignments" element={<StudentDashboardPage />} />
            <Route path="schedule" element={<StudentDashboardPage />} />
            <Route path="grades" element={<StudentDashboardPage />} />
            <Route path="certificates" element={<CertificateViewerPage />} />
            <Route path="discussions" element={<DiscussionsPage />} />
            <Route path="ai-assistant" element={<StudentAiTutorPage />} />
          </Route>
          <Route
            path="/instructor"
            element={
              <ProtectedRoute requiredRole="ROLE_INSTRUCTOR">
                <InstructorLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<InstructorDashboardPage />} />
            <Route path="courses" element={<InstructorCoursesPage />} />
            <Route path="submissions" element={<InstructorDashboardPage />} />
            <Route path="curriculum" element={<InstructorDashboardPage />} />
            <Route path="students" element={<FacultyAttendancePage />} />
            <Route path="analytics" element={<FacultyAnalyticsPage />} />
            <Route path="ai-tools" element={<FacultyAiQuestionGeneratorPage />} />
          </Route>
          <Route
            path="/admin"
            element={
              <ProtectedRoute requiredRole="ROLE_ADMIN">
                <AdminLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<AdminDashboardPage />} />
            <Route path="users" element={<AdminUsersPage />} />
            <Route path="departments" element={<AdminDashboardPage />} />
            <Route path="courses" element={<AdminDashboardPage />} />
            <Route path="security" element={<AdminDashboardPage />} />
            <Route path="telemetry" element={<AdminDashboardPage />} />
            <Route path="settings" element={<AdminDashboardPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
