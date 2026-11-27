import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiClient } from '../../../lib/api-client';
import { QuizDetail, QuizResult } from '../types';
import {
  Clock,
  CheckCircle,
  AlertTriangle,
  ArrowRight,
  ArrowLeft,
  Award
} from 'lucide-react';

export const QuizRunnerPage: React.FC = () => {
  const { quizId } = useParams<{ quizId: string }>();
  const navigate = useNavigate();

  const [quiz, setQuiz] = useState<QuizDetail | null>(null);
  const [selectedAnswers, setSelectedAnswers] = useState<{ [questionId: number]: number }>({});
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [timeLeftSeconds, setTimeLeftSeconds] = useState<number>(1200); // 20m default
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<QuizResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!quizId) return;
    apiClient.get<QuizDetail>(`/quizzes/${quizId}`)
      .then((res) => {
        setQuiz(res.data);
        setTimeLeftSeconds(res.data.durationMinutes * 60);
      })
      .catch((err) => {
        console.error('Failed to load quiz:', err);
      })
      .finally(() => setLoading(false));
  }, [quizId]);

  // Countdown timer
  useEffect(() => {
    if (result || timeLeftSeconds <= 0) return;
    const interval = setInterval(() => {
      setTimeLeftSeconds((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [result, timeLeftSeconds]);

  const handleSelectOption = (questionId: number, optionId: number) => {
    setSelectedAnswers((prev) => ({ ...prev, [questionId]: optionId }));
  };

  const handleSubmitQuiz = async () => {
    if (!quiz || submitting) return;
    setSubmitting(true);
    try {
      const res = await apiClient.post<QuizResult>(`/quizzes/${quiz.id}/submit`, {
        answers: selectedAnswers,
      });
      setResult(res.data);
    } catch (err) {
      console.error('Error submitting quiz:', err);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
        <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
        <p className="text-sm font-medium text-slate-500">Initializing secure examination runner...</p>
      </div>
    );
  }

  if (!quiz) {
    return <div className="p-8 text-center text-red-600">Quiz not found or not published.</div>;
  }

  // Result View
  if (result) {
    return (
      <div className="max-w-xl mx-auto py-12 px-6">
        <div className="bg-white border border-slate-200 rounded-3xl p-8 text-center shadow-lg">
          <div className={`w-16 h-16 rounded-2xl mx-auto flex items-center justify-center mb-4 ${
            result.passed ? 'bg-emerald-100 text-emerald-600' : 'bg-red-100 text-red-600'
          }`}>
            {result.passed ? <Award className="w-8 h-8" /> : <AlertTriangle className="w-8 h-8" />}
          </div>

          <h2 className="text-2xl font-bold text-slate-900 mb-2">
            {result.passed ? 'Examination Passed!' : 'Requires Review'}
          </h2>
          <p className="text-sm text-slate-500 mb-6">{result.message}</p>

          <div className="grid grid-cols-2 gap-4 py-4 px-6 bg-slate-50 rounded-2xl mb-8 border border-slate-100 text-left">
            <div>
              <span className="text-xs text-slate-400 block">Final Score</span>
              <span className="text-2xl font-extrabold text-slate-900">{result.scorePercentage}%</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 block">Correct Answers</span>
              <span className="text-2xl font-extrabold text-slate-900">
                {result.correctCount} / {result.totalQuestions}
              </span>
            </div>
          </div>

          <button
            onClick={() => navigate('/student/courses')}
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold text-sm shadow transition-colors"
          >
            Return to Courses
          </button>
        </div>
      </div>
    );
  }

  const currentQ = quiz.questions[currentQuestionIndex];
  const minutes = Math.floor(timeLeftSeconds / 60);
  const seconds = timeLeftSeconds % 60;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Top Examination Status Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center justify-between">
        <div>
          <span className="text-xs font-bold text-indigo-600 uppercase tracking-wider">{quiz.courseCode}</span>
          <h2 className="text-lg font-bold text-slate-900">{quiz.title}</h2>
        </div>

        <div className={`flex items-center space-x-2 px-3 py-1.5 rounded-xl font-mono text-sm font-bold border ${
          timeLeftSeconds < 180 ? 'bg-red-50 text-red-600 border-red-200 animate-pulse' : 'bg-slate-50 text-slate-700 border-slate-200'
        }`}>
          <Clock className="w-4 h-4" />
          <span>{minutes.toString().padStart(2, '0')}:{seconds.toString().padStart(2, '0')}</span>
        </div>
      </div>

      {/* Main Question Card */}
      {currentQ && (
        <div className="bg-white border border-slate-200 rounded-3xl p-8 shadow-sm space-y-6">
          <div className="flex items-center justify-between text-xs font-semibold text-slate-400 border-b border-slate-100 pb-3">
            <span>Question {currentQuestionIndex + 1} of {quiz.questions.length}</span>
            <span>{currentQ.points} Points</span>
          </div>

          <h3 className="text-lg font-bold text-slate-900 leading-snug">
            {currentQ.questionText}
          </h3>

          {/* Options */}
          <div className="space-y-3 pt-2">
            {currentQ.options.map((opt) => {
              const isSelected = selectedAnswers[currentQ.id] === opt.id;
              return (
                <button
                  key={opt.id}
                  onClick={() => handleSelectOption(currentQ.id, opt.id)}
                  className={`w-full p-4 rounded-2xl border text-left text-sm font-medium transition-all flex items-center justify-between ${
                    isSelected
                      ? 'border-indigo-600 bg-indigo-50/70 text-indigo-900 font-bold shadow-sm ring-1 ring-indigo-600'
                      : 'border-slate-200 bg-slate-50/50 hover:bg-slate-50 text-slate-700'
                  }`}
                >
                  <span>{opt.optionText}</span>
                  <div className={`w-5 h-5 rounded-full border flex items-center justify-center ${
                    isSelected ? 'border-indigo-600 bg-indigo-600 text-white' : 'border-slate-300'
                  }`}>
                    {isSelected && <CheckCircle className="w-3.5 h-3.5" />}
                  </div>
                </button>
              );
            })}
          </div>

          {/* Navigation Controls */}
          <div className="flex items-center justify-between pt-6 border-t border-slate-100">
            <button
              disabled={currentQuestionIndex === 0}
              onClick={() => setCurrentQuestionIndex((prev) => prev - 1)}
              className="px-4 py-2 text-xs font-semibold text-slate-600 disabled:opacity-30 hover:bg-slate-100 rounded-xl transition-colors flex items-center space-x-1"
            >
              <ArrowLeft className="w-4 h-4 mr-1" />
              <span>Previous</span>
            </button>

            {currentQuestionIndex < quiz.questions.length - 1 ? (
              <button
                onClick={() => setCurrentQuestionIndex((prev) => prev + 1)}
                className="px-5 py-2.5 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-bold transition-colors flex items-center space-x-1"
              >
                <span>Next Question</span>
                <ArrowRight className="w-4 h-4 ml-1" />
              </button>
            ) : (
              <button
                onClick={handleSubmitQuiz}
                disabled={submitting}
                className="px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors shadow flex items-center space-x-1.5"
              >
                <CheckCircle className="w-4 h-4" />
                <span>{submitting ? 'Scoring Answers...' : 'Submit Final Examination'}</span>
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
