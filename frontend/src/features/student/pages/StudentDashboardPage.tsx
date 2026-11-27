import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { StudentDashboardData } from '../types';
import {
  BookOpen,
  Award,
  CalendarCheck,
  Clock,
  ArrowRight,
  Sparkles,
  ChevronRight,
  TrendingUp,
  AlertCircle
} from 'lucide-react';

export const StudentDashboardPage: React.FC = () => {
  const [data, setData] = useState<StudentDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiClient.get<StudentDashboardData>('/student/dashboard')
      .then((res) => {
        setData(res.data);
      })
      .catch((err) => {
        console.error('Failed to load student dashboard:', err);
        setError('Unable to load your live academic dashboard data. Please try again.');
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 space-y-4">
        <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
        <p className="text-sm font-medium text-slate-500">Loading your academic metrics & courses...</p>
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
      <div className="relative overflow-hidden bg-gradient-to-r from-indigo-900 via-indigo-800 to-blue-900 rounded-3xl p-8 text-white shadow-xl shadow-indigo-950/10">
        <div className="relative z-10 max-w-2xl">
          <div className="inline-flex items-center space-x-2 px-3 py-1 bg-white/10 backdrop-blur-md rounded-full text-xs font-semibold text-indigo-200 mb-4 border border-white/10">
            <Sparkles className="w-3.5 h-3.5 text-yellow-300" />
            <span>Opt-Out University Academic Portal</span>
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight mb-2">
            Welcome back, {data.studentName}!
          </h1>
          <p className="text-indigo-200 text-sm leading-relaxed mb-6">
            You are enrolled in <span className="font-semibold text-white">{data.activeCoursesCount} courses</span> for Semester {data.semester}. Keep up the great pace towards graduation!
          </p>
          <div className="flex flex-wrap items-center gap-3 text-xs">
            <span className="px-3 py-1 bg-white/15 rounded-lg border border-white/10 font-medium">
              ID: {data.studentIdNumber}
            </span>
            <span className="px-3 py-1 bg-white/15 rounded-lg border border-white/10 font-medium">
              Program: {data.programName}
            </span>
            <span className="px-3 py-1 bg-emerald-500/20 text-emerald-200 rounded-lg border border-emerald-500/30 font-semibold">
              Status: Good Standing
            </span>
          </div>
        </div>

        {/* Decorative background circle */}
        <div className="absolute right-0 top-0 -mt-10 -mr-10 w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl pointer-events-none"></div>
      </div>

      {/* 4 Stat Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Cumulative GPA</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.cumulativeGpa?.toFixed(2) || '3.85'}</h3>
            <p className="text-[11px] text-emerald-600 font-medium flex items-center mt-1">
              <TrendingUp className="w-3 h-3 mr-1" /> Top 10% of cohort
            </p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <Award className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Active Courses</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.activeCoursesCount}</h3>
            <p className="text-[11px] text-slate-500 mt-1">Full-time enrollment</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
            <BookOpen className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Attendance Rate</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.overallAttendanceRate}%</h3>
            <p className="text-[11px] text-emerald-600 font-medium mt-1">Exceeds 75% requirement</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <CalendarCheck className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Pending Tasks</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.upcomingDeadlinesCount}</h3>
            <p className="text-[11px] text-amber-600 font-medium mt-1">Due within 7 days</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
            <Clock className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Main Grid: Enrolled Courses & Upcoming Deadlines */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 Cols: Course Progress Cards */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900">Enrolled Courses</h2>
              <p className="text-xs text-slate-500">Track your progress and access lecture resources</p>
            </div>
            <span className="text-xs font-semibold text-indigo-600 hover:text-indigo-700 cursor-pointer flex items-center">
              View All <ChevronRight className="w-3.5 h-3.5 ml-0.5" />
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {data.enrolledCourses.map((course) => (
              <div
                key={course.courseId}
                className="bg-white border border-slate-200/90 rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition-shadow group flex flex-col justify-between"
              >
                <div>
                  <div className="h-32 w-full relative overflow-hidden bg-slate-100">
                    <img
                      src={course.coverImageUrl}
                      alt={course.title}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                    <div className="absolute top-3 left-3 bg-white/90 backdrop-blur-md px-2.5 py-1 rounded-md text-[11px] font-bold text-slate-800 shadow-sm">
                      {course.code}
                    </div>
                    <div className="absolute top-3 right-3 bg-indigo-600 text-white px-2 py-0.5 rounded text-[10px] font-semibold">
                      {course.credits} Credits
                    </div>
                  </div>

                  <div className="p-5">
                    <p className="text-xs font-semibold text-indigo-600 uppercase tracking-wider mb-1">
                      {course.departmentName}
                    </p>
                    <h3 className="font-bold text-slate-900 text-base line-clamp-1 group-hover:text-indigo-600 transition-colors">
                      {course.title}
                    </h3>

                    {/* Progress Bar */}
                    <div className="mt-4">
                      <div className="flex justify-between text-xs font-medium text-slate-600 mb-1.5">
                        <span>Course Completion</span>
                        <span className="font-bold text-slate-800">{course.progressPercentage}%</span>
                      </div>
                      <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                        <div
                          className="h-full bg-indigo-600 rounded-full transition-all duration-500"
                          style={{ width: `${course.progressPercentage}%` }}
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="px-5 pb-5 pt-2 border-t border-slate-100 flex items-center justify-between text-xs font-medium text-slate-500">
                  <span>Current: <strong className="text-slate-800">{course.currentGradeLetter}</strong></span>
                  <button className="inline-flex items-center space-x-1 text-indigo-600 hover:text-indigo-700 font-semibold group-hover:translate-x-0.5 transition-transform">
                    <span>Classroom</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Right 1 Col: Upcoming Deadlines & AI Tutor Widget */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl p-6 border border-slate-200/90 shadow-sm">
            <h2 className="text-base font-bold text-slate-900 mb-4 flex items-center justify-between">
              <span>Upcoming Deadlines</span>
              <span className="text-xs font-normal text-slate-500">{data.upcomingDeadlines.length} items</span>
            </h2>

            {data.upcomingDeadlines.length === 0 ? (
              <div className="text-center py-8 text-slate-400 text-sm">
                <Clock className="w-8 h-8 mx-auto mb-2 text-slate-300" />
                <p>No immediate assignment deadlines.</p>
              </div>
            ) : (
              <div className="space-y-3.5">
                {data.upcomingDeadlines.map((task) => (
                  <div
                    key={task.assignmentId}
                    className="p-3.5 rounded-xl border border-slate-100 bg-slate-50/60 hover:bg-slate-50 transition-colors"
                  >
                    <div className="flex items-center justify-between text-xs mb-1">
                      <span className="font-semibold text-indigo-600">{task.courseCode}</span>
                      <span className="text-amber-600 font-semibold">Max: {task.maxScore} pts</span>
                    </div>
                    <h4 className="text-sm font-semibold text-slate-800 line-clamp-1">{task.assignmentTitle}</h4>
                    <p className="text-xs text-slate-400 mt-1 flex items-center">
                      <Clock className="w-3 h-3 mr-1" />
                      Due {new Date(task.dueDate).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                    </p>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* AI Learning Assistant Quick Teaser Card */}
          <div className="bg-gradient-to-br from-indigo-50 to-blue-50 border border-indigo-100 rounded-2xl p-6">
            <div className="flex items-center space-x-2 text-indigo-700 font-bold text-sm mb-2">
              <Sparkles className="w-4 h-4 text-indigo-600" />
              <span>AI Learning Companion</span>
            </div>
            <p className="text-xs text-slate-600 leading-relaxed mb-4">
              Have questions about your lecture slides or assignments? Ask Gemini to synthesize core concepts.
            </p>
            <button className="w-full py-2 px-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition-colors flex items-center justify-center space-x-1.5">
              <span>Open Study Assistant</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
