import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { DiscussionThread, LiveClass } from '../types';
import {
  MessageSquare,
  Pin,
  Send,
  Video,
  Plus,
  ExternalLink
} from 'lucide-react';

export const DiscussionsPage: React.FC = () => {
  const [threads, setThreads] = useState<DiscussionThread[]>([]);
  const [liveClasses, setLiveClasses] = useState<LiveClass[]>([]);
  const [selectedThread, setSelectedThread] = useState<DiscussionThread | null>(null);
  const [replyText, setReplyText] = useState('');
  const [newTitle, setNewTitle] = useState('');
  const [newContent, setNewContent] = useState('');
  const [showNewThreadModal, setShowNewThreadModal] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Load for CS101 (Course ID 1)
    Promise.all([
      apiClient.get<DiscussionThread[]>('/communication/courses/1/threads'),
      apiClient.get<LiveClass[]>('/communication/courses/1/live-classes')
    ]).then(([threadsRes, liveRes]) => {
      setThreads(threadsRes.data);
      setLiveClasses(liveRes.data);
      if (threadsRes.data.length > 0) {
        loadThreadDetails(threadsRes.data[0].id);
      }
    }).catch((err) => console.error('Failed to load discussions:', err))
      .finally(() => setLoading(false));
  }, []);

  const loadThreadDetails = (threadId: number) => {
    apiClient.get<DiscussionThread>(`/communication/threads/${threadId}`)
      .then((res) => setSelectedThread(res.data))
      .catch((err) => console.error('Failed to load thread details:', err));
  };

  const handlePostReply = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedThread || !replyText.trim()) return;

    try {
      await apiClient.post(`/communication/threads/${selectedThread.id}/replies`, {
        content: replyText.trim()
      });
      setReplyText('');
      loadThreadDetails(selectedThread.id);
    } catch (err) {
      console.error('Failed to add reply:', err);
    }
  };

  const handleCreateThread = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim() || !newContent.trim()) return;

    try {
      const res = await apiClient.post<DiscussionThread>('/communication/courses/1/threads', {
        title: newTitle.trim(),
        content: newContent.trim()
      });
      setNewTitle('');
      setNewContent('');
      setShowNewThreadModal(false);
      setThreads((prev) => [res.data, ...prev]);
      loadThreadDetails(res.data.id);
    } catch (err) {
      console.error('Failed to create thread:', err);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-slate-500">Loading course discussions & live lecture links...</div>;
  }

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      {/* Header and Live Classroom Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center space-x-2">
            <MessageSquare className="w-6 h-6 text-indigo-600" />
            <span>Course Discussions & Virtual Lectures</span>
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Participate in academic Q&A, form peer revision groups, and join live video workshops.
          </p>
        </div>

        {liveClasses.length > 0 && liveClasses[0].isActive && (
          <a
            href={liveClasses[0].meetingUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center space-x-2 px-4 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold shadow-md shadow-rose-600/30 transition-all animate-pulse"
          >
            <Video className="w-4 h-4" />
            <span>Join Live Workshop ({liveClasses[0].title})</span>
            <ExternalLink className="w-3.5 h-3.5 ml-1" />
          </a>
        )}
      </div>

      {/* Main Dual-Column: Threads List & Active Thread Reading/Reply Stage */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 1 Col: Thread Index */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-bold text-slate-900">Discussion Boards</h2>
            <button
              onClick={() => setShowNewThreadModal(true)}
              className="p-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold flex items-center space-x-1 transition-colors shadow-sm"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>New Thread</span>
            </button>
          </div>

          <div className="space-y-2.5">
            {threads.map((t) => (
              <button
                key={t.id}
                onClick={() => loadThreadDetails(t.id)}
                className={`w-full p-4 rounded-2xl border text-left transition-all ${
                  selectedThread?.id === t.id
                    ? 'border-indigo-600 bg-indigo-50/70 shadow-sm'
                    : 'border-slate-200 bg-white hover:bg-slate-50'
                }`}
              >
                <div className="flex items-center justify-between text-xs mb-1">
                  <div className="flex items-center space-x-1.5">
                    {t.isPinned && <Pin className="w-3 h-3 text-indigo-600 fill-indigo-600" />}
                    <span className="font-bold text-slate-800 line-clamp-1">{t.title}</span>
                  </div>
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-100 text-slate-600">
                    {t.replyCount} replies
                  </span>
                </div>
                <p className="text-xs text-slate-500 line-clamp-2 mt-1">{t.content}</p>
                <div className="mt-3 flex items-center justify-between text-[11px] text-slate-400">
                  <span>by {t.authorName} ({t.authorRole})</span>
                </div>
              </button>
            ))}
          </div>
        </div>

        {/* Right 2 Cols: Active Thread Discussion Reader & Reply Box */}
        <div className="lg:col-span-2 space-y-6">
          {selectedThread ? (
            <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm space-y-6">
              {/* Original Thread Post */}
              <div className="border-b border-slate-100 pb-6">
                <div className="flex items-center space-x-2 mb-2">
                  {selectedThread.isPinned && (
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-50 text-indigo-700 border border-indigo-200 flex items-center">
                      <Pin className="w-3 h-3 mr-1 fill-indigo-600" /> Pinned
                    </span>
                  )}
                  <span className="text-xs text-slate-400">
                    Posted by <strong>{selectedThread.authorName}</strong> ({selectedThread.authorRole})
                  </span>
                </div>
                <h2 className="text-xl font-bold text-slate-900">{selectedThread.title}</h2>
                <p className="text-sm text-slate-700 mt-3 leading-relaxed whitespace-pre-line">
                  {selectedThread.content}
                </p>
              </div>

              {/* Replies Thread */}
              <div className="space-y-4">
                <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                  Discussion Replies ({selectedThread.posts?.length || 0})
                </h3>

                {selectedThread.posts && selectedThread.posts.length > 0 ? (
                  <div className="space-y-3">
                    {selectedThread.posts.map((post) => (
                      <div key={post.id} className="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-2">
                        <div className="flex items-center justify-between text-xs">
                          <span className="font-bold text-slate-900">{post.authorName}</span>
                          <span className="text-[10px] px-2 py-0.5 bg-slate-200 rounded-full font-semibold text-slate-700">
                            {post.authorRole}
                          </span>
                        </div>
                        <p className="text-xs text-slate-700 leading-relaxed">{post.content}</p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-6 text-slate-400 text-xs">
                    No replies yet. Be the first to contribute to this discussion!
                  </div>
                )}
              </div>

              {/* Reply Form */}
              <form onSubmit={handlePostReply} className="pt-4 border-t border-slate-100 space-y-3">
                <textarea
                  rows={3}
                  value={replyText}
                  onChange={(e) => setReplyText(e.target.value)}
                  placeholder="Share your perspective, clarify a formula, or answer a peer's question..."
                  className="w-full p-4 bg-slate-50 border border-slate-200 rounded-2xl text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 resize-none"
                />
                <div className="flex justify-end">
                  <button
                    type="submit"
                    disabled={!replyText.trim()}
                    className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 text-white rounded-xl text-xs font-bold shadow-sm transition-colors flex items-center space-x-1.5"
                  >
                    <Send className="w-3.5 h-3.5" />
                    <span>Post Reply</span>
                  </button>
                </div>
              </form>
            </div>
          ) : (
            <div className="bg-white border border-slate-200 rounded-3xl p-12 text-center text-slate-400 text-sm">
              Select a discussion thread from the left to read posts and contribute replies.
            </div>
          )}
        </div>
      </div>

      {/* New Thread Modal */}
      {showNewThreadModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl p-6 max-w-lg w-full shadow-2xl space-y-4">
            <h2 className="text-lg font-bold text-slate-900">Start New Discussion Thread</h2>
            <form onSubmit={handleCreateThread} className="space-y-4">
              <div>
                <label className="text-xs font-bold text-slate-700 block mb-1">Thread Title</label>
                <input
                  type="text"
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="e.g. Asymptotics proof clarification on Slide 12"
                  className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="text-xs font-bold text-slate-700 block mb-1">Content / Question</label>
                <textarea
                  rows={4}
                  value={newContent}
                  onChange={(e) => setNewContent(e.target.value)}
                  placeholder="Describe your question or discussion topic in detail..."
                  className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 resize-none"
                />
              </div>
              <div className="flex justify-end space-x-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowNewThreadModal(false)}
                  className="px-4 py-2 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-xl transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={!newTitle.trim() || !newContent.trim()}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 text-white rounded-xl text-xs font-bold shadow-sm transition-colors"
                >
                  Publish Thread
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
