import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { InstructorCourse } from '../types';
import { Users, Plus, CheckCircle } from 'lucide-react';

export const InstructorCoursesPage: React.FC = () => {
  const [courses, setCourses] = useState<InstructorCourse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient.get<InstructorCourse[]>('/instructor/courses')
      .then((res) => {
        setCourses(res.data || []);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="p-8 text-center text-slate-500">Loading your courses...</div>;
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Assigned Courses & Curricula</h1>
          <p className="text-sm text-slate-500">Manage syllabus, modules, assignments, and gradebook.</p>
        </div>
        <button className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-semibold shadow-sm transition-all flex items-center space-x-1.5">
          <Plus className="w-4 h-4" />
          <span>New Course</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {courses.map((course) => (
          <div key={course.courseId} className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow">
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
              <span className="text-slate-500 flex items-center">
                <Users className="w-3.5 h-3.5 mr-1 text-slate-400" /> Students
              </span>
              <span className="font-bold text-slate-900">{course.enrolledStudentsCount} / {course.capacity}</span>
            </div>

            <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
              <span className="px-2 py-0.5 bg-emerald-50 text-emerald-700 font-medium rounded text-[11px] flex items-center">
                <CheckCircle className="w-3 h-3 mr-1" /> {course.status}
              </span>
              <span className="text-blue-600 font-semibold cursor-pointer hover:underline">
                Open Course →
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
