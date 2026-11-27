import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { BookOpen, Layers, Megaphone, Send, Users } from 'lucide-react';

interface CatalogCourse {
  id: number;
  code: string;
  title: string;
  description: string;
  credits: number;
  capacity: number;
  departmentName: string;
  enrolledCount: number;
  moduleCount: number;
}

interface Announcement {
  id: number;
  courseCode: string | null;
  title: string;
  content: string;
  category: string;
  priority: string;
}

export const InstructorCurriculumPage: React.FC = () => {
  const [courses, setCourses] = useState<CatalogCourse[]>([]);
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [courseId, setCourseId] = useState('');
  const [publishing, setPublishing] = useState(false);

  const loadData = () => {
    Promise.all([
      apiClient.get<CatalogCourse[]>('/portal/courses'),
      apiClient.get<Announcement[]>('/portal/announcements')
    ])
      .then(([cRes, aRes]) => {
        setCourses(Array.isArray(cRes.data) ? cRes.data : []);
        setAnnouncements(Array.isArray(aRes.data) ? aRes.data : []);
      })
      .catch((err) => console.error('Failed to load curriculum data:', err));
  };

  useEffect(() => {
    loadData();
  }, []);

  const handlePublish = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim()) return;
    setPublishing(true);
    try {
      await apiClient.post('/portal/announcements', {
        title: title.trim(),
        content: content.trim(),
        courseId: courseId ? Number(courseId) : null,
        category: 'ACADEMIC',
        priority: 'HIGH'
      });
      setTitle('');
      setContent('');
      loadData();
    } catch (err) {
      console.error('Failed to publish announcement:', err);
    } finally {
      setPublishing(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Curriculum Studio & Announcements</h1>
        <p className="text-sm text-slate-500 mt-1">
          Inspect active university course modules, enrollment capacities, and publish course announcements.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 grid grid-cols-1 sm:grid-cols-2 gap-4">
          {courses.map((c) => (
            <div key={c.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold px-2.5 py-0.5 rounded-lg bg-indigo-50 text-indigo-700">
                    {c.code}
                  </span>
                  <span className="text-xs text-slate-400">{c.credits} Credits</span>
                </div>
                <h3 className="text-base font-bold text-slate-900 mt-2">{c.title}</h3>
                <p className="text-xs text-slate-500 mt-1 line-clamp-2">{c.description}</p>
              </div>
              <div className="flex items-center justify-between pt-4 mt-4 border-t border-slate-100 text-xs text-slate-600">
                <span className="flex items-center">
                  <Layers className="w-3.5 h-3.5 mr-1 text-indigo-600" /> {c.moduleCount} Modules
                </span>
                <span className="flex items-center">
                  <Users className="w-3.5 h-3.5 mr-1 text-emerald-600" /> {c.enrolledCount}/{c.capacity} Enrolled
                </span>
                <span className="flex items-center">
                  <BookOpen className="w-3.5 h-3.5 mr-1 text-slate-400" /> {c.departmentName}
                </span>
              </div>
            </div>
          ))}
        </div>

        {/* Publish Announcement */}
        <div className="space-y-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
            <h2 className="text-sm font-bold text-slate-900 flex items-center mb-3">
              <Megaphone className="w-4 h-4 text-indigo-600 mr-2" />
              Publish Course Announcement
            </h2>
            <form onSubmit={handlePublish} className="space-y-3">
              <select
                value={courseId}
                onChange={(e) => setCourseId(e.target.value)}
                className="w-full px-3 py-2 text-xs border border-slate-200 rounded-xl bg-white"
              >
                <option value="">All Courses (Institutional Notice)</option>
                {courses.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.code} — {c.title}
                  </option>
                ))}
              </select>
              <input
                type="text"
                required
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Announcement headline..."
                className="w-full px-3 py-2 text-sm border border-slate-200 rounded-xl"
              />
              <textarea
                rows={3}
                required
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="Details for students..."
                className="w-full p-3 text-sm border border-slate-200 rounded-xl"
              />
              <button
                type="submit"
                disabled={publishing}
                className="w-full inline-flex items-center justify-center px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-xl hover:bg-indigo-700"
              >
                <Send className="w-4 h-4 mr-2" />
                {publishing ? 'Publishing...' : 'Publish Notice'}
              </button>
            </form>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm space-y-2.5">
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">Recent Announcements</h3>
            {announcements.slice(0, 4).map((a) => (
              <div key={a.id} className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <span className="text-[10px] font-bold text-indigo-600">{a.courseCode || a.category}</span>
                <p className="text-xs font-bold text-slate-900 mt-0.5">{a.title}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
