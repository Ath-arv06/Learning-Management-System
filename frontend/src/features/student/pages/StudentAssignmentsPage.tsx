import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import {
  FileText,
  Clock,
  CheckCircle2,
  AlertTriangle,
  Send,
  Search,
  Award,
  X
} from 'lucide-react';

interface StudentAssignmentItem {
  id: number;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  title: string;
  description: string;
  maxScore: number;
  dueDate: string;
  submissionId: number | null;
  submissionStatus: string | null;
  submissionText: string | null;
  fileUrl: string | null;
  score: number | null;
  feedback: string | null;
  submittedAt: string | null;
}

export const StudentAssignmentsPage: React.FC = () => {
  const [assignments, setAssignments] = useState<StudentAssignmentItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'ALL' | 'PENDING' | 'SUBMITTED' | 'GRADED'>('ALL');
  const [search, setSearch] = useState('');
  const [activeItem, setActiveItem] = useState<StudentAssignmentItem | null>(null);
  const [submissionText, setSubmissionText] = useState('');
  const [fileUrl, setFileUrl] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const loadAssignments = () => {
    setLoading(true);
    apiClient
      .get<StudentAssignmentItem[]>('/portal/student/assignments')
      .then((res) => setAssignments(Array.isArray(res.data) ? res.data : []))
      .catch((err) => console.error('Failed to load assignments:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadAssignments();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeItem) return;
    setSubmitting(true);
    try {
      await apiClient.post(`/portal/student/assignments/${activeItem.id}/submit`, {
        submissionText,
        fileUrl: fileUrl.trim() || null
      });
      setActiveItem(null);
      setSubmissionText('');
      setFileUrl('');
      loadAssignments();
    } catch (err) {
      console.error('Failed to submit assignment:', err);
    } finally {
      setSubmitting(false);
    }
  };

  const getEffectiveStatus = (item: StudentAssignmentItem) => {
    if (item.submissionStatus === 'GRADED') return 'GRADED';
    if (item.submissionStatus === 'SUBMITTED') return 'SUBMITTED';
    return 'PENDING';
  };

  const filtered = assignments.filter((a) => {
    const status = getEffectiveStatus(a);
    if (filter !== 'ALL' && status !== filter) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        a.title.toLowerCase().includes(q) ||
        a.courseCode.toLowerCase().includes(q) ||
        a.courseTitle.toLowerCase().includes(q)
      );
    }
    return true;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Assignments & Deadlines</h1>
          <p className="text-sm text-slate-500 mt-1">
            Track pending coursework, submit solutions, and review faculty grading feedback.
          </p>
        </div>
        <div className="relative w-full sm:w-72">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by course or title..."
            className="w-full pl-9 pr-4 py-2 text-sm bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex flex-wrap gap-2">
        {(['ALL', 'PENDING', 'SUBMITTED', 'GRADED'] as const).map((tab) => (
          <button
            key={tab}
            onClick={() => setFilter(tab)}
            className={`px-4 py-2 rounded-xl text-xs font-semibold transition ${
              filter === tab
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            {tab === 'ALL' ? 'All Assignments' : tab.charAt(0) + tab.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center text-sm text-slate-500">
          Loading assignments...
        </div>
      ) : filtered.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center">
          <FileText className="w-10 h-10 text-slate-300 mx-auto mb-3" />
          <p className="text-sm font-semibold text-slate-700">No assignments match this filter.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-4">
          {filtered.map((item) => {
            const status = getEffectiveStatus(item);
            const isOverdue = status === 'PENDING' && new Date(item.dueDate) < new Date();
            return (
              <div
                key={item.id}
                className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4"
              >
                <div className="space-y-2 flex-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <span className="text-xs font-bold px-2.5 py-0.5 rounded-lg bg-indigo-50 text-indigo-700">
                      {item.courseCode}
                    </span>
                    {status === 'GRADED' && (
                      <span className="inline-flex items-center text-xs font-semibold px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-700">
                        <Award className="w-3.5 h-3.5 mr-1" /> Graded: {item.score}/{item.maxScore}
                      </span>
                    )}
                    {status === 'SUBMITTED' && (
                      <span className="inline-flex items-center text-xs font-semibold px-2.5 py-0.5 rounded-full bg-blue-100 text-blue-700">
                        <CheckCircle2 className="w-3.5 h-3.5 mr-1" /> Submitted
                      </span>
                    )}
                    {status === 'PENDING' && isOverdue && (
                      <span className="inline-flex items-center text-xs font-semibold px-2.5 py-0.5 rounded-full bg-rose-100 text-rose-700">
                        <AlertTriangle className="w-3.5 h-3.5 mr-1" /> Overdue
                      </span>
                    )}
                    {status === 'PENDING' && !isOverdue && (
                      <span className="inline-flex items-center text-xs font-semibold px-2.5 py-0.5 rounded-full bg-amber-100 text-amber-700">
                        <Clock className="w-3.5 h-3.5 mr-1" /> Pending
                      </span>
                    )}
                  </div>
                  <h3 className="text-base font-bold text-slate-900">{item.title}</h3>
                  <p className="text-sm text-slate-600">{item.description}</p>
                  {item.feedback && (
                    <div className="mt-2 p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-xs text-emerald-800">
                      <span className="font-bold">Instructor Feedback:</span> {item.feedback}
                    </div>
                  )}
                  <div className="flex flex-wrap items-center gap-4 text-xs text-slate-500 pt-1">
                    <span>Due: {new Date(item.dueDate).toLocaleString()}</span>
                    <span>Max Marks: {item.maxScore}</span>
                    {item.submittedAt && (
                      <span>Submitted: {new Date(item.submittedAt).toLocaleString()}</span>
                    )}
                  </div>
                </div>

                <div className="shrink-0">
                  <button
                    onClick={() => {
                      setActiveItem(item);
                      setSubmissionText(item.submissionText || '');
                      setFileUrl(item.fileUrl || '');
                    }}
                    className="inline-flex items-center px-4 py-2.5 rounded-xl text-sm font-semibold bg-indigo-600 text-white hover:bg-indigo-700 transition shadow-sm"
                  >
                    <Send className="w-4 h-4 mr-2" />
                    {status === 'PENDING' ? 'Submit Work' : 'Update Submission'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Submission Modal */}
      {activeItem && (
        <div className="fixed inset-0 bg-slate-900/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-xl">
            <div className="flex items-center justify-between mb-4">
              <div>
                <span className="text-xs font-bold text-indigo-600">{activeItem.courseCode}</span>
                <h2 className="text-lg font-bold text-slate-900">{activeItem.title}</h2>
              </div>
              <button
                onClick={() => setActiveItem(null)}
                className="p-1.5 text-slate-400 hover:text-slate-600 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase mb-1">
                  Solution Notes / Summary
                </label>
                <textarea
                  rows={4}
                  required
                  value={submissionText}
                  onChange={(e) => setSubmissionText(e.target.value)}
                  placeholder="Describe your implementation, approach, or paste your solution..."
                  className="w-full p-3 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase mb-1">
                  Repository / Document Link (Optional)
                </label>
                <input
                  type="url"
                  value={fileUrl}
                  onChange={(e) => setFileUrl(e.target.value)}
                  placeholder="https://github.com/..."
                  className="w-full px-3 py-2 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setActiveItem(null)}
                  className="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-5 py-2 text-sm font-semibold bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 disabled:opacity-60"
                >
                  {submitting ? 'Submitting...' : 'Submit Assignment'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
