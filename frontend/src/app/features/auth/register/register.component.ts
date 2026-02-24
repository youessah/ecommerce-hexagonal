import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2><i class="fas fa-user-edit"></i> Inscription</h2>
        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Nom d'utilisateur</label>
            <input type="text" formControlName="username" placeholder="johndoe" />
          </div>
          <div class="form-group">
            <label>Email</label>
            <input type="email" formControlName="email" placeholder="john@example.com" />
          </div>
          <div class="form-group">
            <label>Mot de passe</label>
            <input type="password" formControlName="password" placeholder="Min. 6 caractères" />
          </div>
          @if (errorMessage()) {
            <div class="alert-error">{{ errorMessage() }}</div>
          }
          @if (successMessage()) {
            <div class="alert-success">{{ successMessage() }}</div>
          }
          <button type="submit" [disabled]="loading() || registerForm.invalid" class="btn-primary">
            {{ loading() ? 'Inscription...' : "S'inscrire" }}
          </button>
        </form>
        <p class="link">Déjà inscrit ? <a routerLink="/auth/login">Se connecter</a></p>
      </div>
    </div>
  `,
  styles: [`
    .auth-container { display: flex; justify-content: center; align-items: center; min-height: 70vh; }
    .auth-card { background: white; padding: 2.5rem; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); width: 100%; max-width: 420px; }
    h2 { text-align: center; color: #1a1a2e; margin-bottom: 2rem; }
    .form-group { margin-bottom: 1.2rem; }
    label { display: block; margin-bottom: 0.4rem; color: #333; font-weight: 500; }
    input { width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 6px; font-size: 1rem; box-sizing: border-box; }
    input:focus { outline: none; border-color: #e94560; }
    .alert-error { background: #fde8e8; color: #c0392b; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
    .alert-success { background: #e8f8e8; color: #27ae60; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
    .btn-primary { width: 100%; padding: 0.9rem; background: linear-gradient(135deg, #e94560, #c0392b); color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .link { text-align: center; margin-top: 1rem; color: #666; }
    .link a { color: #e94560; text-decoration: none; font-weight: 600; }
  `]
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;
    this.loading.set(true);
    this.errorMessage.set('');

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMessage.set('Compte créé ! Redirection vers la connexion...');
        setTimeout(() => this.router.navigate(['/auth/login']), 2000);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Erreur lors de l\'inscription');
      }
    });
  }
}
