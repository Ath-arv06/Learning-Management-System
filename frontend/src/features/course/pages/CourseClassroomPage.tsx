import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { apiClient } from '../../../lib/api-client';
import { CourseClassroomData, LessonDetail } from '../types';
import {
  PlayCircle,
  FileText,
  CheckCircle,
  Download,
  ArrowLeft,
  ChevronDown,
  ChevronRight,
  BookOpen,
  Sparkles,
  AlertCircle
} from 'lucide-react';

export const CourseClassroomPage: React.FC = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const [data, setData] = useState<CourseClassroomData | null>(null);
  const [activeLesson, setActiveLesson] = useState<LessonDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [collapsedModules, setCollapsedModules] = useState<{ [key: number]: boolean }>({});

  useEffect(() => {
    if (!courseId) return;
    apiClient.get<CourseClassroomData>(`/courses/${courseId}/classroom`)
      .then((res) => {
        setData(res.data);
        if (res.data.modules.length > 0 && res.data.modules[0].lessons.length > 0) {
          setActiveLesson(res.data.modules[0].lessons[0]);
        }
      })
      .catch((err) => {
        console.error('Failed to load classroom:', err);
        setError('Unable to load classroom curriculum.');
      })
      .finally(() => setLoading(false));
  }, [courseId]);

  const toggleModule = (modId: number) => {
    setCollapsedModules((prev) => ({ ...prev, [modId]: !prev[modId] }));
  };

  const handleMarkComplete = async (lessonId: number) => {
    try {
      await apiClient.post(`/courses/lessons/${lessonId}/complete`);
      if (activeLesson && activeLesson.id === lessonId) {
        setActiveLesson({ ...activeLesson, isCompleted: true });
      }
      if (data) {
        const updated = {
          ...data,
          modules: data.modules.map((m) => ({
            ...m,
            lessons: m.lessons.map((l) => (l.id === lessonId ? { ...l, isCompleted: true } : l)),
          })),
        };
        setData(updated);
      }
    } catch (e) {
      console.error('Failed to mark lesson complete', e);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[70vh] space-y-4">
        <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
        <p className="text-sm font-medium text-slate-500">Launching lecture hall & loading curriculum syllabus...</p>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        <div className="bg-red-50 border border-red-200 text-red-700 p-6 rounded-2xl flex items-center space-x-3">
          <AlertCircle className="w-6 h-6 shrink-0" />
          <div>
            <h3 className="font-semibold text-red-900">Classroom Unavailable</h3>
            <p className="text-sm">{error || 'Course not found.'}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      {/* Header breadcrumb */}
      <div className="flex items-center justify-between pb-4 border-b border-slate-200">
        <div className="flex items-center space-x-4">
          <Link
            to="/student/courses"
            className="p-2 text-slate-500 hover:text-slate-900 hover:bg-slate-100 rounded-xl transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </Link>
          <div>
            <div className="flex items-center space-x-2">
              <span className="text-xs font-bold text-indigo-600 uppercase tracking-wider">{data.code}</span>
              <span className="text-xs text-slate-400">•</span>
              <span className="text-xs text-slate-500">{data.departmentName}</span>
            </div>
            <h1 className="text-2xl font-bold text-slate-900">{data.title}</h1>
          </div>
        </div>

        <div className="hidden sm:flex items-center space-x-3 text-xs">
          <span className="px-3 py-1.5 bg-indigo-50 text-indigo-700 font-semibold rounded-lg border border-indigo-200/50">
            {data.credits} Academic Credits
          </span>
        </div>
      </div>

      {/* Classroom Dual Pane: Lesson Player & Syllabus Tree */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 Cols: Active Lesson Stage & Material Downloads */}
        <div className="lg:col-span-2 space-y-6">
          {activeLesson ? (
            <div className="bg-white border border-slate-200 rounded-3xl overflow-hidden shadow-sm">
              {/* Media Player */}
              <div className="aspect-video w-full bg-slate-950 relative flex items-center justify-center">
                {activeLesson.videoUrl ? (
                  <video
                    key={activeLesson.id}
                    controls
                    className="w-full h-full object-contain"
                    src={activeLesson.videoUrl}
                  />
                ) : (
                  <div className="text-center p-8 text-slate-400">
                    <BookOpen className="w-12 h-12 mx-auto mb-2 text-slate-600" />
                    <p className="text-sm">Reading Assignment</p>
                  </div>
                )}
              </div>

              {/* Lesson Info & Completion Actions */}
              <div className="p-6">
                <div className="flex items-start justify-between">
                  <div>
                    <h2 className="text-xl font-bold text-slate-900">{activeLesson.title}</h2>
                    <p className="text-xs text-slate-500 mt-1">Lecture Duration: {activeLesson.durationMinutes} minutes</p>
                  </div>

                  <button
                    onClick={() => handleMarkComplete(activeLesson.id)}
                    className={`py-2 px-4 rounded-xl text-xs font-semibold flex items-center space-x-1.5 transition-colors ${
                      activeLesson.isCompleted
                        ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                        : 'bg-indigo-600 text-white hover:bg-indigo-700 shadow-sm'
                    }`}
                  >
                    <CheckCircle className="w-4 h-4" />
                    <span>{activeLesson.isCompleted ? 'Completed' : 'Mark as Complete'}</span>
                  </button>
                </div>

                {/* Lesson Materials / Slide PDF downloads */}
                {activeLesson.materials && activeLesson.materials.length > 0 && (
                  <div className="mt-6 pt-6 border-t border-slate-100">
                    <h3 className="text-sm font-bold text-slate-900 mb-3 flex items-center space-x-1.5">
                      <FileText className="w-4 h-4 text-indigo-600" />
                      <span>Downloadable Course Resources ({activeLesson.materials.length})</span>
                    </h3>
                    <div className="space-y-2">
                      {activeLesson.materials.map((mat) => (
                        <div
                          key={mat.id}
                          className="flex items-center justify-between p-3 rounded-xl bg-slate-50 border border-slate-200/60 text-xs"
                        >
                          <div className="flex items-center space-x-2.5 truncate">
                            <FileText className="w-4 h-4 text-slate-400 shrink-0" />
                            <span className="font-medium text-slate-800 truncate">{mat.title}</span>
                          </div>
                          <a
                            href={mat.fileUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="inline-flex items-center space-x-1 text-indigo-600 font-semibold hover:text-indigo-800 shrink-0 ml-4"
                          >
                            <Download className="w-3.5 h-3.5" />
                            <span>Download PDF</span>
                          </a>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="bg-white border border-slate-200 rounded-2xl p-12 text-center text-slate-400">
              Select a lesson from the curriculum outline on the right to start studying.
            </div>
          )}
        </div>

        {/* Right 1 Col: Course Curriculum Module Tree & Gemini Helper */}
        <div className="space-y-6">
          <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm">
            <h2 className="text-base font-bold text-slate-900 mb-4 flex items-center justify-between">
              <span>Course Curriculum</span>
              <span className="text-xs font-normal text-slate-500">{data.modules.length} Modules</span>
            </h2>

            <div className="space-y-4">
              {data.modules.map((mod) => (
                <div key={mod.id} className="border border-slate-200/80 rounded-2xl overflow-hidden">
                  <button
                    onClick={() => toggleModule(mod.id)}
                    className="w-full p-4 bg-slate-50/80 hover:bg-slate-50 flex items-center justify-between text-left transition-colors"
                  >
                    <div>
                      <h3 className="font-bold text-sm text-slate-900">{mod.title}</h3>
                      <p className="text-[11px] text-slate-500 line-clamp-1">{mod.description}</p>
                    </div>
                    {collapsedModules[mod.id] ? (
                      <ChevronRight className="w-4 h-4 text-slate-400" />
                    ) : (
                      <ChevronDown className="w-4 h-4 text-slate-400" />
                    )}
                  </button>

                  {!collapsedModules[mod.id] && (
                    <div className="p-2 space-y-1">
                      {mod.lessons.map((les) => (
                        <button
                          key={les.id}
                          onClick={() => setActiveLesson(les)}
                          className={`w-full p-2.5 rounded-xl text-left text-xs flex items-center justify-between transition-colors ${
                            activeLesson?.id === les.id
                              ? 'bg-indigo-50 text-indigo-700 font-bold'
                              : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                          }`}
                        >
                          <div className="flex items-center space-x-2 truncate">
                            <PlayCircle className={`w-3.5 h-3.5 shrink-0 ${activeLesson?.id === les.id ? 'text-indigo-600' : 'text-slate-400'}`} />
                            <span className="truncate">{les.title}</span>
                          </div>
                          {les.isCompleted ? (
                            <CheckCircle className="w-3.5 h-3.5 text-emerald-500 shrink-0 ml-2" />
                          ) : (
                            <span className="text-[10px] text-slate-400 shrink-0 ml-2">{les.durationMinutes}m</span>
                          )}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* AI Classroom Companion */}
          <div className="bg-gradient-to-br from-indigo-50 to-purple-50 border border-indigo-100 rounded-3xl p-6">
            <div className="flex items-center space-x-2 text-indigo-800 font-bold text-sm mb-2">
              <Sparkles className="w-4 h-4 text-indigo-600" />
              <span>In-Class AI Assistant</span>
            </div>
            <p className="text-xs text-slate-600 leading-relaxed mb-4">
              Need a concept summarized or lecture notes explained? Tap to generate flashcard summaries and study guides.
            </p>
            <button className="w-full py-2 px-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition-colors">
              Ask AI about this Lecture
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
