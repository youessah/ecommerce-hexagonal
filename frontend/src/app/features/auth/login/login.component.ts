import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2><i class="fas fa-lock"></i>Connexion</h2>
        <p class="subtitle">Architecture Hexagonale - Sources interchangeables</p>

        <div class="info-box">
          <strong>Sources disponibles :</strong>
          MySQL | MongoDB | JSON (configurable dans application.yml)
        </div>

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Nom d'utilisateur</label>
            <input type="text" formControlName="username" placeholder="admin ou client1" />
            @if (loginForm.get('username')?.invalid && loginForm.get('username')?.touched) {
              <span class="error">Le nom d'utilisateur est requis</span>
            }
          </div>
          <div class="form-group">
            <label>Mot de passe</label>
            <input type="password" formControlName="password" placeholder="Admin@123" />
            @if (loginForm.get('password')?.invalid && loginForm.get('password')?.touched) {
              <span class="error">Le mot de passe est requis</span>
            }
          </div>
          @if (errorMessage()) {
            <div class="alert-error">{{ errorMessage() }}</div>
          }
          <button type="submit" [disabled]="loading() || loginForm.invalid" class="btn-primary">
            {{ loading() ? 'Connexion...' : 'Se connecter' }}
          </button>
        </form>

        

        <p class="link">Pas de compte ? <a routerLink="/auth/register">S'inscrire</a></p>
      </div>
    </div>
  `,
  styles: [`
    .auth-container { display: flex; justify-content: center; align-items: center; min-height: 70vh; }
    .auth-card { background: white; padding: 2.5rem; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); width: 100%; max-width: 420px; }
    h2 { text-align: center; color: #1a1a2e; margin-bottom: 0.5rem; }
    .subtitle { text-align: center; color: #666; font-size: 0.85rem; margin-bottom: 1.5rem; }
    .info-box { background: #f0f7ff; border: 1px solid #3498db; border-radius: 6px; padding: 0.75rem; margin-bottom: 1.5rem; font-size: 0.85rem; color: #2c3e50; }
    .form-group { margin-bottom: 1.2rem; }
    label { display: block; margin-bottom: 0.4rem; color: #333; font-weight: 500; }
    input { width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 6px; font-size: 1rem; box-sizing: border-box; transition: border 0.2s; }
    input:focus { outline: none; border-color: #e94560; }
    .error { color: #e94560; font-size: 0.8rem; margin-top: 0.25rem; display: block; }
    .alert-error { background: #fde8e8; color: #c0392b; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; text-align: center; }
    .btn-primary { width: 100%; padding: 0.9rem; background: linear-gradient(135deg, #e94560, #c0392b); color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; transition: opacity 0.2s; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .demo-credentials { margin-top: 1.5rem; background: #f8f9fa; padding: 1rem; border-radius: 6px; font-size: 0.85rem; color: #555; }
    .demo-credentials p { margin: 0.25rem 0; }
    .link { text-align: center; margin-top: 1rem; color: #666; }
    .link a { color: #e94560; text-decoration: none; font-weight: 600; }
  `]
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = signal(false);
  errorMessage = signal('');

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;
    this.loading.set(true);
    this.errorMessage.set('');

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/products']);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Identifiants invalides');
      }
    });
  }
}
