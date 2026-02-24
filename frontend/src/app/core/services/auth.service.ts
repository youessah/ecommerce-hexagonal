import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse, RegisterRequest, User } from '../models/user.model';

/**
 * SERVICE - Gestion de l'authentification côté Angular.
 * Communique avec le backend via l'API REST.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API_URL = '/api/auth';

  // Signal pour l'état de l'utilisateur connecté (Angular Signals)
  currentUser = signal<User | null>(this.loadUserFromStorage());
  isLoggedIn = signal<boolean>(!!this.loadToken());

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        this.saveSession(response);
      })
    );
  }

  register(data: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/register`, data);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUser.set(null);
    this.isLoggedIn.set(false);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAdmin(): boolean {
    return this.currentUser()?.roles?.includes('ADMIN') ?? false;
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem('token', response.token);
    const user: User = {
      id: response.userId,
      username: response.username,
      roles: response.roles,
      email: ''
    };
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUser.set(user);
    this.isLoggedIn.set(true);
  }

  private loadToken(): string | null {
    return localStorage.getItem('token');
  }

  private loadUserFromStorage(): User | null {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  }
}
