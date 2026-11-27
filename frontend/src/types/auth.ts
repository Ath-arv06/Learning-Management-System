export type UserRole = 'ROLE_STUDENT' | 'ROLE_INSTRUCTOR' | 'ROLE_ADMIN';

export interface User {
  id: number;
  uuid: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  avatarUrl?: string;
  roles: UserRole[];
  departmentName?: string;
}

export interface AuthResponse {
  message: string;
  user: User;
}
