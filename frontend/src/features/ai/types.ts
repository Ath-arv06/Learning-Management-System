export interface ChatMessage {
  id: string;
  sender: 'user' | 'ai';
  text: string;
  timestamp: string;
  modelUsed?: string;
}

export interface GeneratedQuestion {
  questionText: string;
  options: string[];
  correctOptionIndex: number;
  explanation: string;
}
