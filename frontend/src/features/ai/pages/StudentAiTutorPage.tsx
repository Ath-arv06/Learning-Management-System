import React, { useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { ChatMessage } from '../types';
import {
  Sparkles,
  Send,
  Bot,
  User as UserIcon,
  Lightbulb
} from 'lucide-react';

export const StudentAiTutorPage: React.FC = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: '1',
      sender: 'ai',
      text: 'Hello! I am your Opt-Out AI Academic Tutor powered by Google Gemini. Ask me to break down lecture concepts, clarify asymptotic proofs, or summarize your course materials.',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      modelUsed: 'Google Gemini 1.5 Pro'
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSend = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!input.trim() || loading) return;

    const userText = input.trim();
    const userMsg: ChatMessage = {
      id: Date.now().toString(),
      sender: 'user',
      text: userText,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const res = await apiClient.post<{ responseMessage: string; modelUsed: string }>('/ai/tutor/chat', {
        courseId: 1, // CS101 default
        userMessage: userText
      });

      const aiMsg: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: res.data.responseMessage,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        modelUsed: res.data.modelUsed
      };
      setMessages((prev) => [...prev, aiMsg]);
    } catch (err) {
      console.error('Failed to chat with AI tutor', err);
    } finally {
      setLoading(false);
    }
  };

  const handleQuickPrompt = (prompt: string) => {
    setInput(prompt);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between pb-4 border-b border-slate-200">
        <div>
          <div className="flex items-center space-x-2">
            <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-indigo-50 text-indigo-700 border border-indigo-200/60">
              Grounded Institutional Model
            </span>
            <span className="text-xs text-slate-400">•</span>
            <span className="text-xs text-slate-500">CS101: Data Structures</span>
          </div>
          <h1 className="text-2xl font-bold text-slate-900 mt-1 flex items-center space-x-2">
            <Sparkles className="w-6 h-6 text-indigo-600" />
            <span>AI Academic Study Companion</span>
          </h1>
        </div>
      </div>

      {/* Suggested Quick Starters */}
      <div className="flex items-center space-x-2 overflow-x-auto pb-1 text-xs">
        <span className="text-slate-400 shrink-0 flex items-center font-medium">
          <Lightbulb className="w-3.5 h-3.5 mr-1 text-amber-500" /> Suggestions:
        </span>
        <button
          onClick={() => handleQuickPrompt('Can you explain asymptotic complexity and Big-O notation?')}
          className="px-3 py-1.5 bg-white border border-slate-200 rounded-xl hover:border-indigo-500 hover:text-indigo-600 shrink-0 transition-colors"
        >
          Explain Big-O Complexity
        </button>
        <button
          onClick={() => handleQuickPrompt('What is the difference between BFS and DFS traversals?')}
          className="px-3 py-1.5 bg-white border border-slate-200 rounded-xl hover:border-indigo-500 hover:text-indigo-600 shrink-0 transition-colors"
        >
          BFS vs DFS Traversals
        </button>
        <button
          onClick={() => handleQuickPrompt('How does Gradient Descent minimize loss in Machine Learning?')}
          className="px-3 py-1.5 bg-white border border-slate-200 rounded-xl hover:border-indigo-500 hover:text-indigo-600 shrink-0 transition-colors"
        >
          Gradient Descent Optimization
        </button>
      </div>

      {/* Chat Messages Log */}
      <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm min-h-[480px] flex flex-col justify-between">
        <div className="space-y-4 overflow-y-auto max-h-[500px] pr-2">
          {messages.map((m) => (
            <div
              key={m.id}
              className={`flex items-start space-x-3 ${m.sender === 'user' ? 'flex-row-reverse space-x-reverse' : ''}`}
            >
              <div className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 ${
                m.sender === 'user' ? 'bg-indigo-600 text-white' : 'bg-gradient-to-tr from-indigo-500 to-purple-600 text-white'
              }`}>
                {m.sender === 'user' ? <UserIcon className="w-4 h-4" /> : <Bot className="w-4 h-4" />}
              </div>

              <div className={`max-w-[75%] rounded-2xl p-4 text-sm leading-relaxed ${
                m.sender === 'user'
                  ? 'bg-indigo-600 text-white font-medium rounded-tr-sm'
                  : 'bg-slate-50 border border-slate-200/70 text-slate-800 rounded-tl-sm'
              }`}>
                <p>{m.text}</p>
                <div className={`text-[10px] mt-2 flex items-center justify-between ${
                  m.sender === 'user' ? 'text-indigo-200' : 'text-slate-400'
                }`}>
                  <span>{m.timestamp}</span>
                  {m.modelUsed && <span className="ml-3 font-semibold">{m.modelUsed}</span>}
                </div>
              </div>
            </div>
          ))}

          {loading && (
            <div className="flex items-center space-x-3 text-slate-400 text-xs py-2">
              <div className="w-8 h-8 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center animate-pulse">
                <Sparkles className="w-4 h-4" />
              </div>
              <span>Gemini is synthesizing academic literature...</span>
            </div>
          )}
        </div>

        {/* Input Bar */}
        <form onSubmit={handleSend} className="mt-4 pt-4 border-t border-slate-100 flex items-center space-x-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Ask anything about lecture transcripts, theorems, or sample problems..."
            className="flex-1 px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all placeholder:text-slate-400"
          />
          <button
            type="submit"
            disabled={loading || !input.trim()}
            className="p-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 text-white rounded-2xl shadow-sm transition-colors"
          >
            <Send className="w-4 h-4" />
          </button>
        </form>
      </div>
    </div>
  );
};
