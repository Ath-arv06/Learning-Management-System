import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { AdminDashboardData } from '../types';
import {
  Users,
  GraduationCap,
  Building,
  BookOpen,
  Activity,
  ArrowRight,
  ShieldAlert,
  ChevronRight,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const [data, setData] = useState<AdminDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiClient.get<AdminDashboardData>('/admin/dashboard')
      .then((res) => {
        setData(res.data);
      })
      .catch((err) => {
        console.error('Failed to load admin dashboard:', err);
        setError('Unable to load university governance telemetry. Please verify institutional permissions.');
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 space-y-4">
        <div className="w-10 h-10 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
        <p className="text-sm font-medium text-slate-500">Querying platform telemetry & institutional databases...</p>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 p-6 rounded-2xl flex items-center space-x-3">
        <AlertCircle className="w-6 h-6 shrink-0" />
        <div>
          <h3 className="font-semibold text-red-900">Telemetry Query Error</h3>
          <p className="text-sm">{error || 'An unexpected error occurred.'}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {/* Welcome Banner */}
      <div className="relative overflow-hidden bg-gradient-to-r from-slate-950 via-purple-950 to-indigo-950 rounded-3xl p-8 text-white shadow-xl shadow-slate-950/20">
        <div className="relative z-10 max-w-2xl">
          <div className="inline-flex items-center space-x-2 px-3 py-1 bg-purple-500/20 backdrop-blur-md rounded-full text-xs font-semibold text-purple-300 mb-4 border border-purple-400/30">
            <Activity className="w-3.5 h-3.5 text-purple-400" />
            <span>University Governance & Telemetry Cockpit</span>
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight mb-2">
            Institutional Administration
          </h1>
          <p className="text-slate-300 text-sm leading-relaxed mb-6">
            Monitoring <span className="font-semibold text-white">{data.stats.totalStudents} students</span>, <span className="font-semibold text-white">{data.stats.totalInstructors} faculty members</span>, and <span className="font-semibold text-white">{data.stats.totalCourses} course offerings</span> across {data.stats.totalDepartments} departments.
          </p>
          <div className="flex flex-wrap items-center gap-3 text-xs">
            <span className="px-3 py-1 bg-white/10 rounded-lg border border-white/10 font-medium">
              Database: {data.stats.databaseEngine}
            </span>
            <span className="px-3 py-1 bg-white/10 rounded-lg border border-white/10 font-medium">
              Term: {data.stats.activeAcademicTerm}
            </span>
            <span className="px-3 py-1 bg-emerald-500/20 text-emerald-300 rounded-lg border border-emerald-500/30 font-semibold flex items-center">
              <CheckCircle className="w-3 h-3 mr-1" /> Uptime: {data.stats.systemUptimePercentage}%
            </span>
          </div>
        </div>

        {/* Ambient Glow */}
        <div className="absolute right-0 top-0 -mt-10 -mr-10 w-96 h-96 bg-purple-600/20 rounded-full blur-3xl pointer-events-none"></div>
      </div>

      {/* 4 Core Admin KPI Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Total Students</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.stats.totalStudents}</h3>
            <p className="text-[11px] text-emerald-600 font-medium mt-1">Active registrations</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center">
            <GraduationCap className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Faculty & Staff</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.stats.totalInstructors}</h3>
            <p className="text-[11px] text-blue-600 font-medium mt-1">Teaching appointments</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
            <Users className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Courses Offered</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.stats.totalCourses}</h3>
            <p className="text-[11px] text-indigo-600 font-medium mt-1">{data.stats.totalEnrollments} enrollments</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <BookOpen className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Departments</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{data.stats.totalDepartments}</h3>
            <p className="text-[11px] text-emerald-600 font-medium mt-1">All operational</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <Building className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Main Grid: Department Governance & Recent User Registrations */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 Cols: Department Distribution */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900">Academic Department Governance</h2>
              <p className="text-xs text-slate-500">Course loads, faculty allocations, and section distribution</p>
            </div>
            <span className="text-xs font-semibold text-purple-600 hover:text-purple-700 cursor-pointer flex items-center">
              Manage Departments <ChevronRight className="w-3.5 h-3.5 ml-0.5" />
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {data.departmentDistribution.map((dept) => (
              <div
                key={dept.code}
                className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <span className="px-2.5 py-1 bg-purple-50 text-purple-700 font-bold text-xs rounded-lg border border-purple-200/50">
                      {dept.code}
                    </span>
                    <span className="text-xs font-semibold text-slate-400">
                      Active Department
                    </span>
                  </div>

                  <h3 className="font-bold text-slate-900 text-base mb-3">
                    {dept.departmentName}
                  </h3>

                  <div className="grid grid-cols-2 gap-2 text-xs py-2 px-3 bg-slate-50 rounded-xl mb-4 border border-slate-100">
                    <div>
                      <span className="text-slate-500 block">Courses</span>
                      <span className="font-bold text-slate-900 text-sm">{dept.courseCount}</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block">Faculty</span>
                      <span className="font-bold text-slate-900 text-sm">{dept.facultyCount}</span>
                    </div>
                  </div>
                </div>

                <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
                  <span className="text-slate-500">Status: <strong className="text-emerald-600">Accredited</strong></span>
                  <button className="text-purple-600 hover:text-purple-700 font-semibold inline-flex items-center space-x-1">
                    <span>Inspect</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Right 1 Col: Recent User Accounts & System Security */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl p-6 border border-slate-200/90 shadow-sm">
            <h2 className="text-base font-bold text-slate-900 mb-4 flex items-center justify-between">
              <span>Recent User Provisions</span>
              <span className="text-xs font-normal text-slate-500">{data.recentUsers.length} accounts</span>
            </h2>

            <div className="space-y-3.5">
              {data.recentUsers.slice(0, 5).map((u) => (
                <div
                  key={u.id}
                  className="p-3.5 rounded-xl border border-slate-100 bg-slate-50/60 hover:bg-slate-50 transition-colors"
                >
                  <div className="flex items-center justify-between text-xs mb-1">
                    <span className="font-semibold text-slate-900">{u.fullName}</span>
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      u.primaryRole === 'ROLE_INSTRUCTOR'
                        ? 'bg-blue-100 text-blue-700'
                        : u.primaryRole === 'ROLE_ADMIN'
                        ? 'bg-purple-100 text-purple-700'
                        : 'bg-emerald-100 text-emerald-700'
                    }`}>
                      {u.primaryRole.replace('ROLE_', '')}
                    </span>
                  </div>
                  <p className="text-xs text-slate-500 truncate">{u.email}</p>
                </div>
              ))}
            </div>
          </div>

          {/* System Security Governance Teaser */}
          <div className="bg-gradient-to-br from-purple-50 to-indigo-50 border border-purple-100 rounded-2xl p-6">
            <div className="flex items-center space-x-2 text-purple-800 font-bold text-sm mb-2">
              <ShieldAlert className="w-4 h-4 text-purple-600" />
              <span>Security & Audit Engine</span>
            </div>
            <p className="text-xs text-slate-600 leading-relaxed mb-4">
              All institutional mutations, grading actions, and AI queries are cryptographically logged with immutable audit trails.
            </p>
            <button className="w-full py-2 px-3 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-semibold shadow-sm transition-colors flex items-center justify-center space-x-1.5">
              <span>Open Audit Log Telemetry</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
