import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { Award, TrendingUp, BookOpen, CheckCircle2 } from 'lucide-react';

interface GradeRow {
  courseId: number;
  courseCode: string;
  courseTitle: string;
  credits: number;
  progressPercentage: number;
  finalGradeLetter: string | null;
  finalGradePercentage: number | null;
  avgAssignmentScore: number;
  bestQuizScore: number;
}

export const StudentGradesPage: React.FC = () => {
  const [grades, setGrades] = useState<GradeRow[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get<GradeRow[]>('/portal/student/grades')
      .then((res) => setGrades(Array.isArray(res.data) ? res.data : []))
      .catch((err) => console.error('Failed to load grades:', err))
      .finally(() => setLoading(false));
  }, []);

  const totalCredits = grades.reduce((acc, g) => acc + (g.credits || 0), 0);
  const avgProgress = grades.length
    ? Math.round(grades.reduce((acc, g) => acc + Number(g.progressPercentage || 0), 0) / grades.length)
    : 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Grades & Academic Transcript</h1>
        <p className="text-sm text-slate-500 mt-1">
          Course-wise assessment marks, quiz performance, and credit progression for Spring 2026.
        </p>
      </div>

      {/* KPI Summary */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
          <div className="flex items-center justify-between text-slate-500 text-xs font-semibold uppercase">
            <span>Cumulative GPA</span>
            <Award className="w-4 h-4 text-indigo-600" />
          </div>
          <p className="text-2xl font-extrabold text-slate-900 mt-2">3.85 / 4.00</p>
          <p className="text-xs text-emerald-600 mt-1 font-medium">Dean&apos;s List Standing</p>
        </div>
        <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
          <div className="flex items-center justify-between text-slate-500 text-xs font-semibold uppercase">
            <span>Active Credits</span>
            <BookOpen className="w-4 h-4 text-blue-600" />
          </div>
          <p className="text-2xl font-extrabold text-slate-900 mt-2">{totalCredits} Credits</p>
          <p className="text-xs text-slate-500 mt-1">Across {grades.length} enrolled courses</p>
        </div>
        <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
          <div className="flex items-center justify-between text-slate-500 text-xs font-semibold uppercase">
            <span>Avg Course Progress</span>
            <TrendingUp className="w-4 h-4 text-emerald-600" />
          </div>
          <p className="text-2xl font-extrabold text-slate-900 mt-2">{avgProgress}%</p>
          <p className="text-xs text-slate-500 mt-1">94.5% Semester Attendance</p>
        </div>
      </div>

      {/* Grades Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h2 className="text-base font-bold text-slate-900">Course-wise Assessment Breakdown</h2>
          <span className="inline-flex items-center text-xs font-semibold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-full">
            <CheckCircle2 className="w-3.5 h-3.5 mr-1" /> Good Academic Standing
          </span>
        </div>
        {loading ? (
          <div className="p-10 text-center text-sm text-slate-500">Loading academic records...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-xs uppercase text-slate-500">
                <tr>
                  <th className="px-6 py-3">Course</th>
                  <th className="px-6 py-3">Credits</th>
                  <th className="px-6 py-3">Progress</th>
                  <th className="px-6 py-3">Avg Assignment</th>
                  <th className="px-6 py-3">Best Quiz</th>
                  <th className="px-6 py-3">Standing</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {grades.map((g) => (
                  <tr key={g.courseId} className="hover:bg-slate-50/70">
                    <td className="px-6 py-4">
                      <span className="text-xs font-bold text-indigo-600 block">{g.courseCode}</span>
                      <span className="font-semibold text-slate-900">{g.courseTitle}</span>
                    </td>
                    <td className="px-6 py-4 text-slate-600">{g.credits}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <div className="w-24 bg-slate-100 h-2 rounded-full overflow-hidden">
                          <div
                            className="bg-indigo-600 h-full rounded-full"
                            style={{ width: `${Math.min(100, Number(g.progressPercentage || 0))}%` }}
                          />
                        </div>
                        <span className="text-xs font-semibold text-slate-700">
                          {Number(g.progressPercentage || 0).toFixed(0)}%
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-slate-700 font-medium">
                      {Number(g.avgAssignmentScore || 0) > 0 ? `${Number(g.avgAssignmentScore).toFixed(1)}%` : '—'}
                    </td>
                    <td className="px-6 py-4 text-slate-700 font-medium">
                      {Number(g.bestQuizScore || 0) > 0 ? `${Number(g.bestQuizScore).toFixed(1)}%` : '—'}
                    </td>
                    <td className="px-6 py-4">
                      <span className="px-2.5 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-700">
                        {g.finalGradeLetter || 'In Progress'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
