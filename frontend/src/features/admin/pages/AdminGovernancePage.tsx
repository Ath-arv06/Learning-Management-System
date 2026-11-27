import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import {
  Building2,
  BookOpen,
  Calendar,
  ShieldAlert,
  Megaphone,
  Plus,
  AlertCircle
} from 'lucide-react';

interface DepartmentRow {
  id: number;
  code: string;
  name: string;
  description: string;
  courseCount: number;
  studentCount: number;
  facultyCount: number;
}

interface CourseRow {
  id: number;
  code: string;
  title: string;
  departmentName: string;
  credits: number;
  capacity: number;
  enrolledCount: number;
  status: string;
}

interface TermRow {
  id: number;
  code: string;
  name: string;
  academicYear: string;
  semesterLabel: string;
  startDate: string;
  endDate: string;
  status: string;
}

interface AuditLogRow {
  id: number;
  eventType: string;
  actorEmail: string;
  resourceTarget: string;
  outcome: string;
  details: string;
  isDemo: boolean;
  createdAt: string;
}

export const AdminGovernancePage: React.FC<{ initialTab?: 'departments' | 'courses' | 'terms' | 'audit' }> = ({
  initialTab = 'departments'
}) => {
  const [tab, setTab] = useState<'departments' | 'courses' | 'terms' | 'audit'>(initialTab);
  const [departments, setDepartments] = useState<DepartmentRow[]>([]);
  const [courses, setCourses] = useState<CourseRow[]>([]);
  const [terms, setTerms] = useState<TermRow[]>([]);
  const [auditLogs, setAuditLogs] = useState<AuditLogRow[]>([]);
  const [annTitle, setAnnTitle] = useState('');
  const [annBody, setAnnBody] = useState('');
  const [statusMsg, setStatusMsg] = useState<string | null>(null);

  useEffect(() => {
    setTab(initialTab);
  }, [initialTab]);

  useEffect(() => {
    Promise.all([
      apiClient.get<DepartmentRow[]>('/portal/departments'),
      apiClient.get<CourseRow[]>('/portal/courses'),
      apiClient.get<TermRow[]>('/portal/terms'),
      apiClient.get<AuditLogRow[]>('/portal/audit-logs')
    ])
      .then(([dRes, cRes, tRes, aRes]) => {
        setDepartments(Array.isArray(dRes.data) ? dRes.data : []);
        setCourses(Array.isArray(cRes.data) ? cRes.data : []);
        setTerms(Array.isArray(tRes.data) ? tRes.data : []);
        setAuditLogs(Array.isArray(aRes.data) ? aRes.data : []);
      })
      .catch((err) => console.error('Failed to load governance data:', err));
  }, []);

  const handlePublishGlobalNotice = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!annTitle.trim() || !annBody.trim()) return;
    setStatusMsg(null);
    try {
      await apiClient.post('/portal/announcements', {
        title: annTitle.trim(),
        content: annBody.trim(),
        category: 'SYSTEM',
        priority: 'HIGH',
        isPinned: true
      });
      setAnnTitle('');
      setAnnBody('');
      setStatusMsg('Institutional announcement published across all portals.');
    } catch {
      setStatusMsg('Failed to publish announcement.');
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Institutional Governance & Registry</h1>
        <p className="text-sm text-slate-500 mt-1">
          Manage university departments, academic courses, term calendars, and demo security audit telemetry.
        </p>
      </div>

      {/* Tabs */}
      <div className="flex flex-wrap gap-2">
        {[
          { id: 'departments', label: 'Departments', icon: Building2 },
          { id: 'courses', label: 'Course Directory', icon: BookOpen },
          { id: 'terms', label: 'Academic Terms & Notices', icon: Calendar },
          { id: 'audit', label: 'Audit Logs (Demo)', icon: ShieldAlert }
        ].map((item) => (
          <button
            key={item.id}
            onClick={() => setTab(item.id as typeof tab)}
            className={`inline-flex items-center px-4 py-2 rounded-xl text-xs font-semibold transition ${
              tab === item.id
                ? 'bg-purple-600 text-white shadow-sm'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            <item.icon className="w-3.5 h-3.5 mr-2" />
            {item.label}
          </button>
        ))}
      </div>

      {tab === 'departments' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {departments.map((d) => (
            <div key={d.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
              <span className="text-xs font-bold px-2.5 py-0.5 rounded-lg bg-purple-50 text-purple-700">
                {d.code}
              </span>
              <h3 className="text-base font-bold text-slate-900 mt-2">{d.name}</h3>
              <p className="text-xs text-slate-500 mt-1">{d.description}</p>
              <div className="grid grid-cols-3 gap-2 mt-4 pt-3 border-t border-slate-100 text-center">
                <div>
                  <p className="text-sm font-bold text-slate-900">{d.courseCount}</p>
                  <p className="text-[10px] text-slate-400 uppercase">Courses</p>
                </div>
                <div>
                  <p className="text-sm font-bold text-slate-900">{d.studentCount}</p>
                  <p className="text-[10px] text-slate-400 uppercase">Students</p>
                </div>
                <div>
                  <p className="text-sm font-bold text-slate-900">{d.facultyCount}</p>
                  <p className="text-[10px] text-slate-400 uppercase">Faculty</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {tab === 'courses' && (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase text-slate-500">
              <tr>
                <th className="px-6 py-3">Code</th>
                <th className="px-6 py-3">Course Title</th>
                <th className="px-6 py-3">Department</th>
                <th className="px-6 py-3">Credits</th>
                <th className="px-6 py-3">Enrolled</th>
                <th className="px-6 py-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {courses.map((c) => (
                <tr key={c.id} className="hover:bg-slate-50">
                  <td className="px-6 py-3 font-bold text-purple-700">{c.code}</td>
                  <td className="px-6 py-3 font-semibold text-slate-900">{c.title}</td>
                  <td className="px-6 py-3 text-slate-600">{c.departmentName}</td>
                  <td className="px-6 py-3 text-slate-600">{c.credits}</td>
                  <td className="px-6 py-3 text-slate-600">
                    {c.enrolledCount} / {c.capacity}
                  </td>
                  <td className="px-6 py-3">
                    <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-700">
                      {c.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === 'terms' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="space-y-3">
            {terms.map((t) => (
              <div key={t.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm flex items-center justify-between">
                <div>
                  <span className="text-xs font-bold text-purple-600">{t.code} • {t.academicYear}</span>
                  <h3 className="text-base font-bold text-slate-900 mt-0.5">{t.name}</h3>
                  <p className="text-xs text-slate-500 mt-1">
                    {t.semesterLabel} ({t.startDate} to {t.endDate})
                  </p>
                </div>
                <span
                  className={`px-3 py-1 rounded-full text-xs font-bold ${
                    t.status === 'ACTIVE'
                      ? 'bg-emerald-100 text-emerald-700'
                      : t.status === 'COMPLETED'
                      ? 'bg-slate-100 text-slate-600'
                      : 'bg-indigo-100 text-indigo-700'
                  }`}
                >
                  {t.status}
                </span>
              </div>
            ))}
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
            <h2 className="text-base font-bold text-slate-900 flex items-center mb-4">
              <Megaphone className="w-4 h-4 text-purple-600 mr-2" />
              Broadcast Institutional Announcement
            </h2>
            {statusMsg && (
              <div className="mb-3 p-3 rounded-xl bg-emerald-50 text-emerald-700 text-xs font-medium">
                {statusMsg}
              </div>
            )}
            <form onSubmit={handlePublishGlobalNotice} className="space-y-3">
              <input
                type="text"
                required
                value={annTitle}
                onChange={(e) => setAnnTitle(e.target.value)}
                placeholder="Official University Notice Title..."
                className="w-full px-3 py-2 text-sm border border-slate-200 rounded-xl"
              />
              <textarea
                rows={4}
                required
                value={annBody}
                onChange={(e) => setAnnBody(e.target.value)}
                placeholder="Write announcement details..."
                className="w-full p-3 text-sm border border-slate-200 rounded-xl"
              />
              <button
                type="submit"
                className="inline-flex items-center px-4 py-2 bg-purple-600 text-white text-sm font-semibold rounded-xl hover:bg-purple-700"
              >
                <Plus className="w-4 h-4 mr-1.5" /> Broadcast Notice
              </button>
            </form>
          </div>
        </div>
      )}

      {tab === 'audit' && (
        <div className="space-y-3">
          <div className="p-3.5 rounded-xl bg-amber-50 border border-amber-200 flex items-center text-xs text-amber-800">
            <AlertCircle className="w-4 h-4 mr-2 shrink-0" />
            <span>
              <strong>Demo Telemetry Notice:</strong> Records below marked <code>[DEMO]</code> are seeded development/sample audit events for UI verification.
            </span>
          </div>
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-xs uppercase text-slate-500">
                <tr>
                  <th className="px-6 py-3">Event</th>
                  <th className="px-6 py-3">Actor</th>
                  <th className="px-6 py-3">Resource</th>
                  <th className="px-6 py-3">Details</th>
                  <th className="px-6 py-3">Outcome</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {auditLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50">
                    <td className="px-6 py-3 font-mono text-xs font-bold text-purple-700">{log.eventType}</td>
                    <td className="px-6 py-3 text-xs text-slate-600">{log.actorEmail}</td>
                    <td className="px-6 py-3 text-xs font-semibold text-slate-800">{log.resourceTarget}</td>
                    <td className="px-6 py-3 text-xs text-slate-600">{log.details}</td>
                    <td className="px-6 py-3">
                      <span className="px-2 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-100 text-emerald-700">
                        {log.outcome}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
