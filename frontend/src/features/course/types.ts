export interface LessonMaterial {
  id: number;
  title: string;
  fileUrl: string;
  fileType: string;
  fileSizeBytes: number;
}

export interface LessonDetail {
  id: number;
  title: string;
  contentType: string;
  contentBody: string | null;
  videoUrl: string | null;
  durationMinutes: number;
  sortOrder: number;
  isCompleted: boolean;
  materials: LessonMaterial[];
}

export interface CourseModule {
  id: number;
  title: string;
  description: string;
  sortOrder: number;
  lessons: LessonDetail[];
}

export interface CourseClassroomData {
  courseId: number;
  code: string;
  title: string;
  description: string;
  departmentName: string;
  credits: number;
  modules: CourseModule[];
}
