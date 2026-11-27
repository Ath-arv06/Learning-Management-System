import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { AttendanceSession, StudentAttendanceRecord } from '../types';
import { Clock } from 'lucide-react';

export const FacultyAttendancePage: React.FC = () => {
  const [sessions, setSessions] = useState<AttendanceSession[]>([]);
  const [selectedSessionId, setSelectedSessionId] = useState<number | null>(null);
  const [roster, setRoster] = useState<StudentAttendanceRecord[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Load sessions for primary course (CS101 - ID 1)
    apiClient.get<AttendanceSession[]>('/attendance/course/1/sessions')
      .then((res) => {
        setSessions(res.data);
        if (res.data.length > 0) {
          setSelectedSessionId(res.data[0].id);
        }
      })
      .catch((err) => console.error('Failed to load attendance sessions:', err))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!selectedSessionId) return;
    apiClient.get<StudentAttendanceRecord[]>(`/attendance/sessions/${selectedSessionId}/roster`)
      .then((res) => setRoster(res.data))
      .catch((err) => console.error('Failed to load session roster:', err));
  }, [selectedSessionId]);

  const handleUpdateStatus = async (studentId: number, status: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED') => {
    if (!selectedSessionId) return;
    try {
      await apiClient.put(`/attendance/sessions/${selectedSessionId}/students/${studentId}`, { status });
      setRoster((prev) => prev.map((s) => (s.studentId === studentId ? { ...s, status } : s)));
    } catch (e) {
      console.error('Failed to update status', e);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-slate-500">Loading attendance register...</div>;
  }

  const selectedSession = sessions.find((s) => s.id === selectedSessionId);

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Attendance Register & Class Records</h1>
          <p className="text-sm text-slate-500">Mark student presence, record excused absences, and monitor section attendance rates.</p>
        </div>
      </div>

      {/* Sessions Carousel Tabs */}
      <div className="flex space-x-3 overflow-x-auto pb-2">
        {sessions.map((sess) => (
          <button
            key={sess.id}
            onClick={() => setSelectedSessionId(sess.id)}
            className={`p-4 rounded-2xl border text-left shrink-0 w-64 transition-all ${
              selectedSessionId === sess.id
                ? 'border-blue-600 bg-blue-50/70 shadow-sm'
                : 'border-slate-200 bg-white hover:bg-slate-50'
            }`}
          >
            <div className="flex items-center justify-between text-xs mb-1">
              <span className="font-bold text-blue-600">{sess.courseCode}</span>
              <span className="text-slate-400 font-medium">{sess.sessionDate}</span>
            </div>
            <h4 className="font-semibold text-slate-800 text-sm truncate">{sess.topic}</h4>
            <div className="mt-3 flex items-center justify-between text-xs text-slate-500">
              <span className="flex items-center">
                <Clock className="w-3.5 h-3.5 mr-1 text-slate-400" />
                {sess.startTime.slice(0, 5)} - {sess.endTime.slice(0, 5)}
              </span>
              <span className="font-bold text-emerald-600">
                {sess.totalPresent}/{sess.totalStudents} Present
              </span>
            </div>
          </button>
        ))}
      </div>

      {/* Active Session Roster Table */}
      <div className="bg-white border border-slate-200 rounded-3xl overflow-hidden shadow-sm">
        <div className="p-6 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-slate-900">
              Session Roster — {selectedSession?.topic}
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">
              {roster.length} enrolled students registered in this section
            </p>
          </div>
        </div>

        <table className="w-full text-left text-sm">
          <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-500 uppercase tracking-wider">
            <tr>
              <th className="px-6 py-4">Student</th>
              <th className="px-6 py-4">Student ID</th>
              <th className="px-6 py-4">Attendance Status</th>
              <th className="px-6 py-4">Quick Toggle</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {roster.map((record) => (
              <tr key={record.studentId} className="hover:bg-slate-50/60 transition-colors">
                <td className="px-6 py-4 font-semibold text-slate-900">{record.studentName}</td>
                <td className="px-6 py-4 font-mono text-xs text-slate-500">{record.studentIdNumber}</td>
                <td className="px-6 py-4">
                  <span className={`px-2.5 py-1 rounded-full text-xs font-bold ${
                    record.status === 'PRESENT'
                      ? 'bg-emerald-50 text-emerald-700'
                      : record.status === 'LATE'
                      ? 'bg-amber-50 text-amber-700'
                      : record.status === 'EXCUSED'
                      ? 'bg-blue-50 text-blue-700'
                      : 'bg-red-50 text-red-700'
                  }`}>
                    {record.status}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="flex space-x-1.5">
                    <button
                      onClick={() => handleUpdateStatus(record.studentId, 'PRESENT')}
                      className={`p-1.5 rounded-lg border text-xs font-semibold ${
                        record.status === 'PRESENT'
                          ? 'bg-emerald-600 text-white border-emerald-600'
                          : 'bg-white text-slate-600 hover:bg-slate-100 border-slate-200'
                      }`}
                      title="Mark Present"
                    >
                      P
                    </button>
                    <button
                      onClick={() => handleUpdateStatus(record.studentId, 'LATE')}
                      className={`p-1.5 rounded-lg border text-xs font-semibold ${
                        record.status === 'LATE'
                          ? 'bg-amber-500 text-white border-amber-500'
                          : 'bg-white text-slate-600 hover:bg-slate-100 border-slate-200'
                      }`}
                      title="Mark Late"
                    >
                      L
                    </button>
                    <button
                      onClick={() => handleUpdateStatus(record.studentId, 'ABSENT')}
                      className={`p-1.5 rounded-lg border text-xs font-semibold ${
                        record.status === 'ABSENT'
                          ? 'bg-red-600 text-white border-red-600'
                          : 'bg-white text-slate-600 hover:bg-slate-100 border-slate-200'
                      }`}
                      title="Mark Absent"
                    >
                      A
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
