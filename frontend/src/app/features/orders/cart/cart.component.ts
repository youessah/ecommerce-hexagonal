import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="cart-page">
      <h1><i class="fas fa-shopping-basket"></i> Mon Panier</h1>

      @if (cartService.items().length === 0) {
        <div class="empty-cart">
          <p><i class="fas fa-shopping-cart"></i>Votre panier est vide</p>
          <a routerLink="/products" class="btn-shop">Parcourir les produits</a>
        </div>
      } @else {
        <div class="cart-layout">
          <div class="cart-items">
            @for (item of cartService.items(); track item.product.id) {
              <div class="cart-item">
                <div class="item-info">
                  <h3>{{ item.product.name }}</h3>
                  <span class="supplier-tag">
                    {{ item.product.supplierId === 'f1' ? ' F1' : ' F2' }}
                  </span>
                  <p class="item-price">{{ item.product.price | currency:'FCFA' }} / unité</p>
                </div>
                <div class="item-quantity">
                  <button (click)="decreaseQty(item.product.id)" class="qty-btn">−</button>
                  <span>{{ item.quantity }}</span>
                  <button (click)="increaseQty(item)" class="qty-btn">+</button>
                </div>
                <div class="item-total">
                  {{ (item.product.price * item.quantity) | currency:'FCFA' }}
                </div>
                <button (click)="removeItem(item.product.id)" class="btn-remove">✕</button>
              </div>
            }
          </div>

          <div class="cart-summary">
            <h3>Résumé de la commande</h3>
            <div class="summary-row">
              <span>Articles ({{ cartService.itemCount() }})</span>
              <span>{{ cartService.total() | currency:'FCFA' }}</span>
            </div>
            <div class="summary-row">
              <span>Livraison</span>
              <span>Gratuite</span>
            </div>
            <div class="summary-total">
              <span>Total</span>
              <span>{{ cartService.total() | currency:'FCFA' }}</span>
            </div>

            <div class="form-group">
              <label>Adresse de livraison</label>
              <textarea [(ngModel)]="shippingAddress" placeholder="Douala parcours vita" rows="3"></textarea>
            </div>

            @if (errorMessage()) {
              <div class="alert-error">{{ errorMessage() }}</div>
            }

            <button (click)="placeOrder()" [disabled]="loading() || !shippingAddress" class="btn-order">
              
              {{ loading() ? 'Traitement...' : ' Passer la commande' }}
            </button>
            <p class="note">
              <i class="fas fa-tools"></i> Le stock est mis à jour dans la base du fournisseur correspondant
            </p>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    h1 { color: #1a1a2e; margin-bottom: 2rem; }
    .empty-cart { text-align: center; padding: 4rem; color: #999; }
    .empty-cart p { font-size: 1.5rem; margin-bottom: 1.5rem; }
    .btn-shop { background: #e94560; color: white; padding: 0.8rem 2rem; border-radius: 8px; text-decoration: none; font-weight: 600; }
    .cart-layout { display: grid; grid-template-columns: 1fr 350px; gap: 2rem; }
    .cart-items { display: flex; flex-direction: column; gap: 1rem; }
    .cart-item { background: white; padding: 1.5rem; border-radius: 12px; display: flex; align-items: center; gap: 1.5rem; box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
    .item-info { flex: 1; }
    .item-info h3 { margin: 0 0 0.25rem; color: #1a1a2e; }
    .supplier-tag { background: #3498db; color: white; font-size: 0.75rem; padding: 0.15rem 0.5rem; border-radius: 10px; }
    .item-price { color: #666; font-size: 0.9rem; margin-top: 0.5rem; }
    .item-quantity { display: flex; align-items: center; gap: 0.75rem; }
    .qty-btn { width: 30px; height: 30px; border: 1px solid #ddd; background: white; border-radius: 50%; cursor: pointer; font-size: 1.2rem; display: flex; align-items: center; justify-content: center; }
    .item-total { font-weight: bold; color: #e94560; font-size: 1.1rem; min-width: 80px; text-align: right; }
    .btn-remove { background: none; border: none; color: #ccc; font-size: 1.2rem; cursor: pointer; padding: 0.25rem; transition: color 0.2s; }
    .btn-remove:hover { color: #e94560; }
    .cart-summary { background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); height: fit-content; position: sticky; top: 1rem; }
    .cart-summary h3 { margin-top: 0; color: #1a1a2e; }
    .summary-row { display: flex; justify-content: space-between; padding: 0.75rem 0; border-bottom: 1px solid #f0f0f0; color: #555; }
    .summary-total { display: flex; justify-content: space-between; padding: 1rem 0; font-weight: bold; font-size: 1.2rem; color: #1a1a2e; }
    .form-group { margin: 1.5rem 0; }
    label { display: block; margin-bottom: 0.5rem; color: #333; font-weight: 500; font-size: 0.9rem; }
    textarea { width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 6px; font-size: 0.9rem; resize: vertical; box-sizing: border-box; }
    .alert-error { background: #fde8e8; color: #c0392b; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; font-size: 0.9rem; }
    .btn-order { width: 100%; padding: 1rem; background: linear-gradient(135deg, #27ae60, #2ecc71); color: white; border: none; border-radius: 8px; font-size: 1rem; font-weight: 600; cursor: pointer; transition: opacity 0.2s; }
    .btn-order:disabled { opacity: 0.6; cursor: not-allowed; }
    .note { font-size: 0.75rem; color: #999; text-align: center; margin-top: 0.75rem; }
    @media (max-width: 768px) { .cart-layout { grid-template-columns: 1fr; } }
  `]
})
export class CartComponent {
  shippingAddress = '';
  loading = signal(false);
  errorMessage = signal('');

  constructor(
    public cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {}

  increaseQty(item: any): void {
    if (item.quantity < item.product.stock) {
      this.cartService.updateQuantity(item.product.id, item.quantity + 1);
    }
  }

  decreaseQty(productId: string): void {
    const item = this.cartService.items().find(i => i.product.id === productId);
    if (item) this.cartService.updateQuantity(productId, item.quantity - 1);
  }

  removeItem(productId: string): void {
    this.cartService.removeFromCart(productId);
  }

  placeOrder(): void {
    if (!this.shippingAddress.trim()) return;
    this.loading.set(true);
    this.errorMessage.set('');

    const request = {
      items: this.cartService.toOrderItems(),
      shippingAddress: this.shippingAddress
    };

    this.orderService.placeOrder(request).subscribe({
      next: (order) => {
        this.loading.set(false);
        this.cartService.clearCart();
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Erreur lors de la commande');
      }
    });
  }
}
