import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    @if (product()) {
      <div class="product-detail">
        <a routerLink="/products" class="back-link">← Retour aux produits</a>
        <div class="detail-card">
          <div class="supplier-header" [class.f2]="product()!.supplierId === 'f2'">
            {{ product()!.supplierId === 'f1' ? ' Fournisseur F1' : ' Fournisseur F2' }}
            <small>Base de données associée: {{ product()!.supplierId === 'f1' ? 'MySQL instance 1' : 'MySQL instance 2' }}</small>
          </div>
          <h1>{{ product()!.name }}</h1>
          <p class="description">{{ product()!.description }}</p>
          <div class="category">{{ product()!.category }}</div>
          <div class="price">{{ product()!.price | currency:'FCFA' }}</div>
          <div class="stock" [class.low]="product()!.stock < 20">Stock disponible: {{ product()!.stock }}</div>
          @if (addedMessage()) {
            <div class="added-msg"><i class="fas fa-check-circle"></i> {{ addedMessage() }}</div>
          }
          <button (click)="addToCart()" [disabled]="!product()!.available" class="btn-add">
            <i class="fas fa-map-marker-alt"></i> Ajouter au panier
          </button>
        </div>
      </div>
    }
  `,
  styles: [`
    .back-link { color: #e94560; text-decoration: none; font-weight: 500; }
    .detail-card { background: white; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); margin-top: 1.5rem; max-width: 700px; }
    .supplier-header { padding: 0.75rem 1rem; background: #3498db; color: white; border-radius: 8px; margin-bottom: 1.5rem; display: flex; justify-content: space-between; align-items: center; }
    .supplier-header.f2 { background: #8e44ad; }
    h1 { color: #1a1a2e; }
    .description { color: #555; line-height: 1.6; }
    .category { display: inline-block; background: #f0f0f0; padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.85rem; color: #555; margin: 1rem 0; }
    .price { font-size: 2rem; font-weight: bold; color: #e94560; margin: 1rem 0; }
    .stock { color: #27ae60; margin-bottom: 1.5rem; }
    .stock.low { color: #f39c12; }
    .added-msg { background: #e8f8e8; color: #27ae60; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
    .btn-add { padding: 1rem 2.5rem; background: linear-gradient(135deg, #e94560, #c0392b); color: white; border: none; border-radius: 8px; font-size: 1.1rem; font-weight: 600; cursor: pointer; }
    .btn-add:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class ProductDetailComponent implements OnInit {
  product = signal<Product | null>(null);
  addedMessage = signal('');

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.productService.getById(id).subscribe(p => this.product.set(p));
  }

  addToCart(): void {
    const p = this.product();
    if (p) {
      this.cartService.addToCart(p);
      this.addedMessage.set(`${p.name} ajouté au panier !`);
      setTimeout(() => this.addedMessage.set(''), 2000);
    }
  }
}
