export interface Certificate {
  id: number;
  certificateUuid: string;
  studentId: number;
  studentName: string;
  studentEmail: string;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  issuerId: number;
  issuerName: string;
  issueDate: string;
  finalGrade: number;
  verificationHash: string;
  status: string;
  valid: boolean;
}
