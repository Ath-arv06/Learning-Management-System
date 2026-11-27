import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { CourseAnalytics } from '../types';
import {
  Users,
  AlertTriangle,
  Award,
  CalendarCheck,
  TrendingUp,
  Sparkles
} from 'lucide-react';

export const FacultyAnalyticsPage: React.FC = () => {
  const [analytics, setAnalytics] = useState<CourseAnalytics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient.get<CourseAnalytics>('/attendance/course/1/analytics')
      .then((res) => setAnalytics(res.data))
      .catch((err) => console.error('Failed to load course analytics:', err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="p-8 text-center text-slate-500">Loading academic analytics...</div>;
  }

  if (!analytics) {
    return <div className="p-8 text-center text-red-500">Failed to load analytics.</div>;
  }

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      <div>
        <div className="flex items-center space-x-2 text-xs font-semibold text-blue-600 uppercase tracking-wider mb-1">
          <span>{analytics.courseCode}</span>
          <span>•</span>
          <span>Cohort Telemetry</span>
        </div>
        <h1 className="text-2xl font-bold text-slate-900">{analytics.courseTitle} — Academic Analytics</h1>
      </div>

      {/* 4 Analytics Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Avg Attendance</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{analytics.averageAttendanceRate}%</h3>
            <p className="text-[11px] text-emerald-600 font-medium mt-1 flex items-center">
              <TrendingUp className="w-3 h-3 mr-1" /> Healthy cohort rate
            </p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <CalendarCheck className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Class Avg Score</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{analytics.averageGradePercentage}%</h3>
            <p className="text-[11px] text-blue-600 font-medium mt-1">Grade Level: B</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
            <Award className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">At-Risk Students</p>
            <h3 className="text-2xl font-bold text-red-600 mt-1">{analytics.atRiskStudentsCount}</h3>
            <p className="text-[11px] text-red-500 font-medium mt-1">Requires intervention</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-red-50 text-red-600 flex items-center justify-center">
            <AlertTriangle className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Enrolled Students</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-1">{analytics.totalEnrolledStudents}</h3>
            <p className="text-[11px] text-slate-500 mt-1">Section 01</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <Users className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Grade Distribution Breakdown */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 bg-white border border-slate-200 rounded-3xl p-6 shadow-sm space-y-5">
          <h2 className="text-base font-bold text-slate-900">Grade Distribution Breakdown</h2>

          <div className="space-y-4">
            {analytics.gradeDistribution.map((bucket) => {
              const percentage = (bucket.count / analytics.totalEnrolledStudents) * 100;
              return (
                <div key={bucket.range} className="space-y-1.5">
                  <div className="flex justify-between text-xs font-semibold text-slate-700">
                    <span>{bucket.range}</span>
                    <span>{bucket.count} students ({percentage.toFixed(0)}%)</span>
                  </div>
                  <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-blue-600 rounded-full transition-all duration-500"
                      style={{ width: `${percentage}%` }}
                    ></div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Early Academic Alert Panel */}
        <div className="bg-gradient-to-br from-amber-50 to-orange-50 border border-amber-200/70 rounded-3xl p-6">
          <div className="flex items-center space-x-2 text-amber-800 font-bold text-sm mb-2">
            <Sparkles className="w-4 h-4 text-amber-600" />
            <span>AI Early Risk Detection</span>
          </div>
          <p className="text-xs text-slate-600 leading-relaxed mb-4">
            Our predictive engine flags students whose attendance drops below 75% or whose quiz performance slips before midterm exams.
          </p>
          <div className="p-3 bg-white/80 backdrop-blur-md rounded-2xl border border-amber-200/60 text-xs text-amber-900 font-medium">
            2 students flagged for personalized academic tutoring outreach.
          </div>
        </div>
      </div>
    </div>
  );
};
