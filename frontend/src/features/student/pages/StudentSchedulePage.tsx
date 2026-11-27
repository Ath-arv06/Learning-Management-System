import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { Calendar, Clock, MapPin, User, Megaphone } from 'lucide-react';

interface TimetableSlot {
  id: number;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  instructorName: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  roomCode: string;
  sessionType: string;
  termCode: string;
}

interface Announcement {
  id: number;
  courseCode: string | null;
  title: string;
  content: string;
  category: string;
  priority: string;
  isPinned: boolean;
  publishedAt: string;
}

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

export const StudentSchedulePage: React.FC = () => {
  const [slots, setSlots] = useState<TimetableSlot[]>([]);
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [selectedDay, setSelectedDay] = useState<string>('ALL');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      apiClient.get<TimetableSlot[]>('/portal/timetable'),
      apiClient.get<Announcement[]>('/portal/announcements')
    ])
      .then(([ttRes, annRes]) => {
        setSlots(Array.isArray(ttRes.data) ? ttRes.data : []);
        setAnnouncements(Array.isArray(annRes.data) ? annRes.data : []);
      })
      .catch((err) => console.error('Failed to load schedule:', err))
      .finally(() => setLoading(false));
  }, []);

  const visibleDays = selectedDay === 'ALL' ? DAYS : [selectedDay];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Academic Schedule & Calendar</h1>
          <p className="text-sm text-slate-500 mt-1">
            Spring 2026 weekly lectures, laboratory sessions, and institutional announcements.
          </p>
        </div>
        <div className="flex flex-wrap gap-1.5">
          <button
            onClick={() => setSelectedDay('ALL')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
              selectedDay === 'ALL' ? 'bg-indigo-600 text-white' : 'bg-white border border-slate-200 text-slate-600'
            }`}
          >
            All Week
          </button>
          {DAYS.map((d) => (
            <button
              key={d}
              onClick={() => setSelectedDay(d)}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                selectedDay === d ? 'bg-indigo-600 text-white' : 'bg-white border border-slate-200 text-slate-600'
              }`}
            >
              {d.slice(0, 3)}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center text-sm text-slate-500">
          Loading timetable...
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-4">
            {visibleDays.map((day) => {
              const daySlots = slots.filter((s) => s.dayOfWeek === day);
              return (
                <div key={day} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
                  <div className="flex items-center justify-between mb-3">
                    <h2 className="text-sm font-bold text-slate-900 flex items-center">
                      <Calendar className="w-4 h-4 text-indigo-600 mr-2" />
                      {day.charAt(0) + day.slice(1).toLowerCase()}
                    </h2>
                    <span className="text-xs text-slate-400">{daySlots.length} session(s)</span>
                  </div>
                  {daySlots.length === 0 ? (
                    <p className="text-xs text-slate-400 py-2">No scheduled classes for this day.</p>
                  ) : (
                    <div className="space-y-2.5">
                      {daySlots.map((slot) => (
                        <div
                          key={slot.id}
                          className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-2"
                        >
                          <div>
                            <div className="flex items-center gap-2">
                              <span className="text-xs font-bold text-indigo-600">{slot.courseCode}</span>
                              <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-indigo-100 text-indigo-700">
                                {slot.sessionType}
                              </span>
                            </div>
                            <p className="text-sm font-bold text-slate-800 mt-0.5">{slot.courseTitle}</p>
                            <div className="flex flex-wrap items-center gap-4 text-xs text-slate-500 mt-1">
                              <span className="flex items-center">
                                <User className="w-3.5 h-3.5 mr-1" /> {slot.instructorName}
                              </span>
                              <span className="flex items-center">
                                <MapPin className="w-3.5 h-3.5 mr-1" /> {slot.roomCode}
                              </span>
                            </div>
                          </div>
                          <div className="flex items-center text-xs font-semibold text-slate-700 bg-white px-3 py-1.5 rounded-lg border border-slate-200 shrink-0">
                            <Clock className="w-3.5 h-3.5 mr-1.5 text-indigo-600" />
                            {slot.startTime} – {slot.endTime}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              );
            })}
          </div>

          {/* Academic Announcements */}
          <div className="space-y-4">
            <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
              <h2 className="text-sm font-bold text-slate-900 flex items-center mb-4">
                <Megaphone className="w-4 h-4 text-indigo-600 mr-2" />
                Academic & Exam Notices
              </h2>
              <div className="space-y-3">
                {announcements.map((ann) => (
                  <div key={ann.id} className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="text-[11px] font-bold uppercase tracking-wider text-indigo-600">
                        {ann.courseCode || ann.category}
                      </span>
                      {ann.priority === 'HIGH' && (
                        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-amber-100 text-amber-800">
                          Important
                        </span>
                      )}
                    </div>
                    <p className="text-sm font-bold text-slate-900">{ann.title}</p>
                    <p className="text-xs text-slate-600 leading-relaxed">{ann.content}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
