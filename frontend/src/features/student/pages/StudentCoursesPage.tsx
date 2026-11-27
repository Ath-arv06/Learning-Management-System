import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { EnrolledCourse } from '../types';
import { CheckCircle } from 'lucide-react';

export const StudentCoursesPage: React.FC = () => {
  const [courses, setCourses] = useState<EnrolledCourse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient.get('/student/dashboard')
      .then((res) => {
        setCourses(res.data.enrolledCourses || []);
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
          <h1 className="text-2xl font-bold text-slate-900">My Registered Courses</h1>
          <p className="text-sm text-slate-500">Access your active curriculum, study materials, and lectures.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {courses.map((course) => (
          <div key={course.courseId} className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
            <div className="h-40 w-full relative">
              <img src={course.coverImageUrl} alt={course.title} className="w-full h-full object-cover" />
              <span className="absolute top-3 left-3 bg-white/95 px-2.5 py-1 rounded text-xs font-bold text-slate-900 shadow">
                {course.code}
              </span>
            </div>
            <div className="p-5">
              <span className="text-xs font-semibold text-indigo-600 uppercase tracking-wider">{course.departmentName}</span>
              <h3 className="font-bold text-slate-900 text-base mt-1 line-clamp-1">{course.title}</h3>
              <div className="mt-4 flex items-center justify-between text-xs text-slate-500">
                <span>Credits: {course.credits}</span>
                <span className="font-semibold text-emerald-600 flex items-center">
                  <CheckCircle className="w-3.5 h-3.5 mr-1" /> {course.enrollmentStatus}
                </span>
              </div>
              <div className="mt-4 pt-3 border-t border-slate-100 flex justify-end">
                <a
                  href={`/student/courses/${course.courseId}`}
                  className="text-xs font-semibold text-indigo-600 hover:text-indigo-800"
                >
                  Enter Classroom →
                </a>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
