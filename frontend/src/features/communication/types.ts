export interface DiscussionPost {
  id: number;
  threadId: number;
  authorId: number;
  authorName: string;
  authorRole: string;
  content: string;
  createdAt: string;
}

export interface DiscussionThread {
  id: number;
  courseId: number;
  courseCode: string;
  authorId: number;
  authorName: string;
  authorRole: string;
  title: string;
  content: string;
  isPinned: boolean;
  isLocked: boolean;
  replyCount: number;
  createdAt: string;
  posts?: DiscussionPost[];
}

export interface LiveClass {
  id: number;
  courseId: number;
  courseCode: string;
  title: string;
  meetingUrl: string;
  scheduledStart: string;
  durationMinutes: number;
  isActive: boolean;
}
