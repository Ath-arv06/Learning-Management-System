export interface QuizOption {
  id: number;
  optionText: string;
  sortOrder: number;
}

export interface QuizQuestion {
  id: number;
  questionText: string;
  questionType: string;
  points: number;
  sortOrder: number;
  options: QuizOption[];
}

export interface QuizDetail {
  id: number;
  courseId: number;
  courseCode: string;
  title: string;
  description: string;
  durationMinutes: number;
  passingScore: number;
  maxAttempts: number;
  questions: QuizQuestion[];
}

export interface QuizResult {
  attemptId: number;
  scorePercentage: number;
  passed: boolean;
  totalQuestions: number;
  correctCount: number;
  message: string;
}
