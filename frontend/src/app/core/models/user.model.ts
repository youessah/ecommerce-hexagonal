// models/user.model.ts
export interface User {
  id: string;
  username: string;
  email: string;
  roles: string[];
}

export interface AuthRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userId: string;
  username: string;
  roles: string[];
  expiresIn: number;
}
