import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { InstructorDashboardData } from '../types';
import {
  BookOpen,
  Users,
  CheckSquare,
  BarChart,
  ArrowRight,
  Sparkles,
  ChevronRight,
  Plus,
  Clock,
  AlertCircle
} from 'lucide-react';

export const InstructorDashboardPage: React.FC = () => {
  const [data, setData] = useState<InstructorDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiClient.get<InstructorDashboardData>('/instructor/dashboard')
      .then((res) => {
        setData(res.data);
      })
      .catch((err) => {
        console.error('Failed to load instructor dashboard:', err);
        setError('Unable to load faculty dashboard metrics. Please try again.');
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 space-y-4">
        <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
        <p className="text-sm font-medium text-slate-500">Loading your faculty workspace & teaching metrics...</p>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 p-6 rounded-2xl flex items-center space-x-3">
        <AlertCircle className="w-6 h-6 shrink-0" />
        <div>
          <h3 className="font-semibold text-red-900">Dashboard Loading Error</h3>
          <p className="text-sm">{error || 'An unexpected error occurred.'}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {/* Welcome Banner */}
      <div className="relative overflow-hidden bg-gradient-to-r from-slate-900 via-blue-950 to-indigo-950 rounded-3xl p-8 text-white shadow-xl shadow-slate-950/20">
        <div className="relative z-10 max-w-2xl">
          <div className="inline-flex items-center space-x-2 px-3 py-1 bg-blue-500/20 backdrop-blur-md rounded-full text-xs font-semibold text-blue-300 mb-4 border border-blue-400/30">
            <Sparkles className="w-3.5 h-3.5 text-blue-400" />
            <span>Academic Faculty Workspace</span>
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight mb-2">
            Welcome, {data.instructorName}!
          </h1>
          <p className="text-slate-300 text-sm leading-relaxed mb-6">
            You are managing <span className="font-semibold text-white">{data.activeCoursesCount} courses</span> across the {data.departmentName} department with {data.totalEnrolledStudents} actively enrolled students.
          </p>
          <div className="flex flex-wrap items-center gap-3 text-xs">
            <span className="px-3 py-1 bg-white/10 rounded-lg border border-white/10 font-medium">
              Employee ID: {data.employeeCode}
            </span>
            <span className="px-3 py-1 bg-white/10 rounded-lg border border-white/10 font-medium">
              Rank: {data.designation}
            </span>
            <span className="px-3 py-1 bg-blue-500/30 text-blue-200 rounded-lg border border-blue-400/30 font-semibold">
              Term: Spring 2026
            </span>
          </div>
        </div>

        {/* Action Button */}
        <div className="relative z-10 mt-6 sm:mt-0 sm:absolute sm:right-8 sm:bottom-8 flex space-x-3">
          <button className="py-2.5 px-4 bg-blue-600 hover:bg-blue-500 text-white rounded-xl text-xs font-semibold shadow-lg shadow-blue-600/30 transition-all flex items-center space-x-1.5">
            <Plus className="w-4 h-4" />
            <span>Create New Course</span>
          </button>
        </div>

        {/* Ambient Glow */}
        <div className="absolute right-0 top-0 -mt-10 -mr-10 w-96 h-96 bg-blue-600/20 rounded-full blur-3xl pointer-events-none"></div>
      </div>

      {/* 4 Faculty Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Courses Instructed</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.activeCoursesCount}</h3>
            <p className="text-[11px] text-blue-600 font-medium mt-1">Lead instructor for all</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
            <BookOpen className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Enrolled Students</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.totalEnrolledStudents}</h3>
            <p className="text-[11px] text-emerald-600 font-medium mt-1">100% registration capacity</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <Users className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Grading Queue</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.pendingGradingCount}</h3>
            <p className="text-[11px] text-amber-600 font-medium mt-1">Awaiting evaluation</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
            <CheckSquare className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Avg Class Attendance</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.averageClassAttendance}%</h3>
            <p className="text-[11px] text-emerald-600 font-medium mt-1">Across all lectures</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <BarChart className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Main Grid: Teaching Courses & Grading Submissions Queue */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 Cols: Assigned Courses */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900">Assigned Courses & Sections</h2>
              <p className="text-xs text-slate-500">Manage syllabus, lesson materials, quizzes, and live classes</p>
            </div>
            <span className="text-xs font-semibold text-blue-600 hover:text-blue-700 cursor-pointer flex items-center">
              View All <ChevronRight className="w-3.5 h-3.5 ml-0.5" />
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {data.assignedCourses.map((course) => (
              <div
                key={course.courseId}
                className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <span className="px-2.5 py-1 bg-blue-50 text-blue-700 font-bold text-xs rounded-lg border border-blue-200/50">
                      {course.code}
                    </span>
                    <span className="text-xs font-semibold text-slate-400">
                      {course.credits} Credits
                    </span>
                  </div>

                  <h3 className="font-bold text-slate-900 text-base line-clamp-1 mb-1">
                    {course.title}
                  </h3>
                  <p className="text-xs text-slate-500 mb-4">{course.departmentName}</p>

                  <div className="flex items-center justify-between text-xs py-2 px-3 bg-slate-50 rounded-xl mb-4 border border-slate-100">
                    <span className="text-slate-500">Enrolled Students</span>
                    <span className="font-bold text-slate-900">{course.enrolledStudentsCount} / {course.capacity}</span>
                  </div>
                </div>

                <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
                  <span className="px-2 py-0.5 bg-emerald-50 text-emerald-700 font-medium rounded text-[11px]">
                    {course.status}
                  </span>
                  <button className="text-blue-600 hover:text-blue-700 font-semibold inline-flex items-center space-x-1">
                    <span>Manage Syllabus</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Right 1 Col: Pending Submissions Queue & AI Grading Teaser */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl p-6 border border-slate-200/90 shadow-sm">
            <h2 className="text-base font-bold text-slate-900 mb-4 flex items-center justify-between">
              <span>Submissions Awaiting Review</span>
              <span className="text-xs font-normal text-slate-500">{data.pendingGradingQueue.length} pending</span>
            </h2>

            {data.pendingGradingQueue.length === 0 ? (
              <div className="text-center py-8 text-slate-400 text-sm">
                <CheckSquare className="w-8 h-8 mx-auto mb-2 text-slate-300" />
                <p>All student submissions have been evaluated.</p>
              </div>
            ) : (
              <div className="space-y-3.5">
                {data.pendingGradingQueue.map((item) => (
                  <div
                    key={item.submissionId}
                    className="p-3.5 rounded-xl border border-slate-100 bg-slate-50/60 hover:bg-slate-50 transition-colors"
                  >
                    <div className="flex items-center justify-between text-xs mb-1">
                      <span className="font-semibold text-blue-600">{item.courseCode}</span>
                      <span className="text-slate-400 text-[11px] flex items-center">
                        <Clock className="w-3 h-3 mr-1" />
                        {new Date(item.submittedAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                      </span>
                    </div>
                    <h4 className="text-sm font-semibold text-slate-800 line-clamp-1">{item.assignmentTitle}</h4>
                    <p className="text-xs text-slate-500 mt-1">Submitted by: <strong className="text-slate-700">{item.studentName}</strong></p>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* AI Assisted Grading Card */}
          <div className="bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100 rounded-2xl p-6">
            <div className="flex items-center space-x-2 text-blue-800 font-bold text-sm mb-2">
              <Sparkles className="w-4 h-4 text-blue-600" />
              <span>AI Grading Copilot</span>
            </div>
            <p className="text-xs text-slate-600 leading-relaxed mb-4">
              Speed up evaluation with rubric-aligned automated feedback suggestions. Instructor approval is strictly required before marks are released.
            </p>
            <button className="w-full py-2 px-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-semibold shadow-sm transition-colors flex items-center justify-center space-x-1.5">
              <span>Explore AI Question Generator</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
