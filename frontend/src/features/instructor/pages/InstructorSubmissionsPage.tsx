import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { CheckCircle2, Clock, Award, ExternalLink, Send } from 'lucide-react';

interface SubmissionItem {
  submissionId: number;
  assignmentId: number;
  assignmentTitle: string;
  maxScore: number;
  courseCode: string;
  courseTitle: string;
  studentId: number;
  studentName: string;
  studentEmail: string;
  submissionText: string;
  fileUrl: string | null;
  status: string;
  score: number | null;
  feedback: string | null;
  submittedAt: string;
}

export const InstructorSubmissionsPage: React.FC = () => {
  const [submissions, setSubmissions] = useState<SubmissionItem[]>([]);
  const [selected, setSelected] = useState<SubmissionItem | null>(null);
  const [score, setScore] = useState('');
  const [feedback, setFeedback] = useState('');
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);

  const loadSubmissions = () => {
    setLoading(true);
    apiClient
      .get<SubmissionItem[]>('/portal/instructor/submissions')
      .then((res) => {
        const list = Array.isArray(res.data) ? res.data : [];
        setSubmissions(list);
        if (list.length > 0 && !selected) {
          setSelected(list[0]);
          setScore(list[0].score != null ? String(list[0].score) : String(list[0].maxScore));
          setFeedback(list[0].feedback || '');
        }
      })
      .catch((err) => console.error('Failed to load submissions:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadSubmissions();
  }, []);

  const handleGrade = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selected) return;
    setSaving(true);
    try {
      await apiClient.post(`/portal/instructor/submissions/${selected.submissionId}/grade`, {
        score: Number(score),
        feedback
      });
      loadSubmissions();
    } catch (err) {
      console.error('Failed to grade submission:', err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Evaluation & Grading Queue</h1>
        <p className="text-sm text-slate-500 mt-1">
          Review student assignment submissions, inspect repository links, and publish grades with rubric feedback.
        </p>
      </div>

      {loading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center text-sm text-slate-500">
          Loading student submissions...
        </div>
      ) : submissions.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center">
          <Award className="w-10 h-10 text-slate-300 mx-auto mb-3" />
          <p className="text-sm font-semibold text-slate-700">
            No student submissions in the queue yet.
          </p>
          <p className="text-xs text-slate-500 mt-1">
            When students submit coursework from the Student Portal, their work appears here for split-screen grading.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="space-y-3">
            {submissions.map((sub) => (
              <button
                key={sub.submissionId}
                onClick={() => {
                  setSelected(sub);
                  setScore(sub.score != null ? String(sub.score) : String(sub.maxScore));
                  setFeedback(sub.feedback || '');
                }}
                className={`w-full text-left p-4 rounded-2xl border transition ${
                  selected?.submissionId === sub.submissionId
                    ? 'bg-indigo-50 border-indigo-300 shadow-sm'
                    : 'bg-white border-slate-200 hover:border-indigo-200'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-indigo-600">{sub.courseCode}</span>
                  {sub.status === 'GRADED' ? (
                    <span className="inline-flex items-center text-[11px] font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700">
                      <CheckCircle2 className="w-3 h-3 mr-1" /> {sub.score}/{sub.maxScore}
                    </span>
                  ) : (
                    <span className="inline-flex items-center text-[11px] font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700">
                      <Clock className="w-3 h-3 mr-1" /> Needs Review
                    </span>
                  )}
                </div>
                <p className="font-bold text-slate-900 text-sm mt-1">{sub.studentName}</p>
                <p className="text-xs text-slate-500 truncate">{sub.assignmentTitle}</p>
              </button>
            ))}
          </div>

          {selected && (
            <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-5">
              <div className="border-b border-slate-100 pb-4">
                <span className="text-xs font-bold text-indigo-600">
                  {selected.courseCode} — {selected.courseTitle}
                </span>
                <h2 className="text-xl font-bold text-slate-900 mt-1">{selected.assignmentTitle}</h2>
                <p className="text-xs text-slate-500 mt-1">
                  Submitted by <span className="font-semibold text-slate-700">{selected.studentName}</span> ({selected.studentEmail}) on{' '}
                  {new Date(selected.submittedAt).toLocaleString()}
                </p>
              </div>

              <div className="bg-slate-50 rounded-xl p-4 border border-slate-200 space-y-2">
                <p className="text-xs font-bold uppercase tracking-wider text-slate-400">Student Submission</p>
                <p className="text-sm text-slate-800 whitespace-pre-wrap">{selected.submissionText}</p>
                {selected.fileUrl && (
                  <a
                    href={selected.fileUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex items-center text-xs font-semibold text-indigo-600 hover:underline pt-1"
                  >
                    <ExternalLink className="w-3.5 h-3.5 mr-1" /> Open Attached Repository / Document
                  </a>
                )}
              </div>

              <form onSubmit={handleGrade} className="space-y-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 uppercase mb-1">
                      Score (Max {selected.maxScore})
                    </label>
                    <input
                      type="number"
                      step="0.5"
                      min="0"
                      max={selected.maxScore}
                      required
                      value={score}
                      onChange={(e) => setScore(e.target.value)}
                      className="w-full px-3 py-2 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 uppercase mb-1">
                    Evaluation Feedback
                  </label>
                  <textarea
                    rows={3}
                    value={feedback}
                    onChange={(e) => setFeedback(e.target.value)}
                    placeholder="Provide constructive rubric feedback..."
                    className="w-full p-3 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <button
                  type="submit"
                  disabled={saving}
                  className="inline-flex items-center px-5 py-2.5 bg-indigo-600 text-white text-sm font-semibold rounded-xl hover:bg-indigo-700 transition"
                >
                  <Send className="w-4 h-4 mr-2" />
                  {saving ? 'Saving Grade...' : 'Publish Grade & Feedback'}
                </button>
              </form>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
