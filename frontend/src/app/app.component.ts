import { Component, computed } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { CartService } from './core/services/cart.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <nav class="navbar">
      <div class="nav-brand">
        <a routerLink="/"><i class="fas fa-store"></i>  E-Commerce Hexagonal</a>
      </div>
      <div class="nav-links">
        <a routerLink="/products" routerLinkActive="active">Produits</a>
        @if (authService.isLoggedIn()) {
          <a routerLink="/cart" routerLinkActive="active">
            <i class="fas fa-shopping-cart"></i> Panier <span class="badge">{{ cartService.itemCount() }}</span>
          </a>
          <a routerLink="/orders" routerLinkActive="active"><i class="fas fa-box"></i>Mes commandes</a>
          @if (authService.isAdmin()) {
            <a routerLink="/admin" routerLinkActive="active">Admin</a>
          }
          <button (click)="logout()" class="btn-logout">
            <i class="fas fa-user-circle"></i> {{ authService.currentUser()?.username }} | Déconnexion
          </button>
        } @else {
          <a routerLink="/auth/login" routerLinkActive="active"><i class="fas fa-sign-in-alt"></i> Connexion</a>
          <a routerLink="/auth/register" routerLinkActive="active"><i class="fas fa-user-plus"></i>Inscription</a>
        }
      </div>
    </nav>
    <main class="container">
      <router-outlet />
    </main>
  `,
  styles: [`
    .navbar {
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
      padding: 1rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 2px 10px rgba(0,0,0,0.3);
    }
    .nav-brand a { color: #e94560; font-size: 1.4rem; font-weight: bold; text-decoration: none; }
    .nav-links { display: flex; gap: 1.5rem; align-items: center; }
    .nav-links a { color: #eee; text-decoration: none; transition: color 0.2s; }
    .nav-links a:hover, .nav-links a.active { color: #e94560; }
    .badge { background: #e94560; color: white; border-radius: 50%; padding: 2px 7px; font-size: 0.75rem; }
    .btn-logout { background: transparent; border: 1px solid #e94560; color: #e94560; padding: 0.4rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.9rem; transition: all 0.2s; }
    .btn-logout:hover { background: #e94560; color: white; }
    .container { max-width: 1200px; margin: 2rem auto; padding: 0 1rem; }
  `]
})
export class AppComponent {
  constructor(
    public authService: AuthService,
    public cartService: CartService
  ) {}

  logout(): void {
    this.authService.logout();
  }
}
