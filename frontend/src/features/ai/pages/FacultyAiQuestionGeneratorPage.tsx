import React, { useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { GeneratedQuestion } from '../types';
import {
  Sparkles,
  CheckCircle,
  Plus
} from 'lucide-react';

export const FacultyAiQuestionGeneratorPage: React.FC = () => {
  const [topic, setTopic] = useState('Data Structures & Algorithms');
  const [difficulty, setDifficulty] = useState('INTERMEDIATE');
  const [count, setCount] = useState(3);
  const [questions, setQuestions] = useState<GeneratedQuestion[]>([]);
  const [loading, setLoading] = useState(false);

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await apiClient.post<GeneratedQuestion[]>('/ai/instructor/generate-questions', {
        courseId: 1,
        topic,
        difficultyLevel: difficulty,
        numberOfQuestions: count
      });
      setQuestions(res.data);
    } catch (err) {
      console.error('Failed to generate questions:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-8">
      <div>
        <div className="flex items-center space-x-2 text-xs font-semibold text-blue-600 uppercase tracking-wider mb-1">
          <span>Faculty AI Copilot</span>
          <span>•</span>
          <span>Gemini Pro</span>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center space-x-2">
          <Sparkles className="w-6 h-6 text-blue-600" />
          <span>AI Examination Question Bank Generator</span>
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Generate rigorous, multi-choice examination items with automated answer keys and pedagogical explanations.
        </p>
      </div>

      {/* Generator Prompt Config Form */}
      <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm">
        <form onSubmit={handleGenerate} className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="md:col-span-2 space-y-1.5">
            <label className="text-xs font-bold text-slate-700">Course Topic / Concept</label>
            <input
              type="text"
              value={topic}
              onChange={(e) => setTopic(e.target.value)}
              placeholder="e.g. Asymptotic Bounds, Graph Traversal, Heaps"
              className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700">Difficulty Tier</label>
            <select
              value={difficulty}
              onChange={(e) => setDifficulty(e.target.value)}
              className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500"
            >
              <option value="BEGINNER">Foundational</option>
              <option value="INTERMEDIATE">Intermediate (Core)</option>
              <option value="ADVANCED">Advanced / Honors</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700">Items Count</label>
            <select
              value={count}
              onChange={(e) => setCount(Number(e.target.value))}
              className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500"
            >
              <option value={1}>1 Question</option>
              <option value={3}>3 Questions</option>
              <option value={5}>5 Questions</option>
            </select>
          </div>

          <div className="md:col-span-4 flex justify-end pt-2">
            <button
              type="submit"
              disabled={loading || !topic.trim()}
              className="py-2.5 px-6 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl text-xs font-bold shadow-md shadow-blue-600/20 transition-all flex items-center space-x-2"
            >
              <Sparkles className="w-4 h-4" />
              <span>{loading ? 'Synthesizing Questions with Gemini...' : 'Generate Questions'}</span>
            </button>
          </div>
        </form>
      </div>

      {/* Generated Results List */}
      {questions.length > 0 && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-bold text-slate-900">Generated Examination Items ({questions.length})</h2>
            <button className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-semibold flex items-center space-x-1.5 transition-colors">
              <Plus className="w-4 h-4" />
              <span>Import to Question Bank</span>
            </button>
          </div>

          <div className="space-y-4">
            {questions.map((q, idx) => (
              <div key={idx} className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm space-y-4">
                <div className="flex items-start justify-between">
                  <h3 className="font-bold text-slate-900 text-base">
                    {idx + 1}. {q.questionText}
                  </h3>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
                  {q.options.map((opt, optIdx) => (
                    <div
                      key={optIdx}
                      className={`p-3 rounded-xl border text-xs font-medium flex items-center justify-between ${
                        optIdx === q.correctOptionIndex
                          ? 'border-emerald-500 bg-emerald-50/70 text-emerald-900 font-bold'
                          : 'border-slate-200 bg-slate-50 text-slate-700'
                      }`}
                    >
                      <span>{opt}</span>
                      {optIdx === q.correctOptionIndex && (
                        <CheckCircle className="w-4 h-4 text-emerald-600 shrink-0 ml-2" />
                      )}
                    </div>
                  ))}
                </div>

                <div className="p-3 bg-blue-50/60 rounded-xl border border-blue-100 text-xs text-blue-900">
                  <strong className="block mb-0.5">Pedagogical Explanation:</strong>
                  <span>{q.explanation}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
