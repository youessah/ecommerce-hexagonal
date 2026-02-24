import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="page-header">
      <h1> <i class="fas fa-th-large"></i>Catalogue des produits</h1>
      <p class="subtitle">Produits de plusiFCFAs fournissFCFAs, chacun avec sa propre base de données</p>
    </div>

    <!-- Filtres -->
    <div class="filters">
      <input
        type="text"
        [(ngModel)]="searchTerm"
        (input)="filterProducts()"
        placeholder="Rechercher un produit..."
        class="search-input"
      />
      <select [(ngModel)]="selectedSupplier" (change)="filterProducts()" class="filter-select">
        <option value="">Tous les fournissFCFAs</option>
        <option value="f1">FournissFCFA F1 (base MySQL)</option>
        <option value="f2"><i class="fas fa-building"></i>FournissFCFA F2 (base MySQL #2)</option>
      </select>
      <select [(ngModel)]="selectedCategory" (change)="filterProducts()" class="filter-select">
        <option value="">Toutes les catégories</option>
        @for (cat of categories(); track cat) {
          <option [value]="cat">{{ cat }}</option>
        }
      </select>
    </div>

    @if (loading()) {
      <div class="loading">Chargement des produits...</div>
    }

    @if (addedMessage()) {
      <div class="toast-success">{{ addedMessage() }}</div>
    }

    <div class="products-grid">
      @for (product of filteredProducts(); track product.id) {
        <div class="product-card">
          <div class="supplier-badge" [class.supplier-f2]="product.supplierId === 'f2'">
            <i class="fas fa-industry"></i> {{ product.supplierId === 'f1' ? ' Fournisseur F1' : ' Fournisseur F2' }}
          </div>
          <div class="product-body">
            <h3>{{ product.name }}</h3>
            <p class="description">{{ product.description }}</p>
            <div class="category-tag">{{ product.category }}</div>
            <div class="product-footer">
              <div class="price">{{ product.price | currency:'FCFA ':'symbol':'1.2-2' }}</div>
              <div class="stock" [class.low-stock]="product.stock < 20">
                {{ product.stock > 0 ? 'En stock: ' + product.stock : 'Rupture' }}
              </div>
            </div>
          </div>
          <div class="product-actions">
            @if (product.available && product.stock > 0) {
              <button (click)="addToCart(product)" class="btn-cart">
                <i class="fas fa-cart-plus"></i> Ajouter au panier
              </button>
            } @else {
              <button disabled class="btn-cart disabled">Indisponible</button>
            }
            @if (authService.isAdmin()) {
              <button [routerLink]="['/admin']" class="btn-edit"><i class="fas fa-pen"></i> Éditer</button>
            }
          </div>
        </div>
      } @empty {
        <div class="no-products">Aucun produit trouvé</div>
      }
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 2rem; }
    .page-header h1 { color: #1a1a2e; font-size: 2rem; }
    .subtitle { color: #666; margin-top: 0.5rem; }
    .filters { display: flex; gap: 1rem; margin-bottom: 2rem; flex-wrap: wrap; }
    .search-input { flex: 1; min-width: 200px; padding: 0.75rem; border: 1px solid #ddd; border-radius: 8px; font-size: 1rem; }
    .filter-select { padding: 0.75rem; border: 1px solid #ddd; border-radius: 8px; font-size: 0.9rem; background: white; }
    .loading { text-align: center; padding: 3rem; color: #666; font-size: 1.2rem; }
    .toast-success { background: #27ae60; color: white; padding: 1rem 1.5rem; border-radius: 8px; margin-bottom: 1rem; text-align: center; animation: fadeIn 0.3s; }
    .products-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.5rem; }
    .product-card { background: white; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); overflow: hidden; transition: transform 0.2s, box-shadow 0.2s; }
    .product-card:hover { transform: translateY(-3px); box-shadow: 0 8px 25px rgba(0,0,0,0.12); }
    .supplier-badge { padding: 0.5rem 1rem; background: #3498db; color: white; font-size: 0.8rem; font-weight: 600; }
    .supplier-badge.supplier-f2 { background: #8e44ad; }
    .product-body { padding: 1.2rem; }
    h3 { margin: 0 0 0.5rem; color: #1a1a2e; font-size: 1.1rem; }
    .description { color: #666; font-size: 0.9rem; margin-bottom: 0.75rem; min-height: 40px; }
    .category-tag { display: inline-block; background: #f0f0f0; color: #555; padding: 0.2rem 0.7rem; border-radius: 20px; font-size: 0.8rem; margin-bottom: 1rem; }
    .product-footer { display: flex; justify-content: space-between; align-items: center; }
    .price { font-size: 1.3rem; font-weight: bold; color: #e94560; }
    .stock { font-size: 0.85rem; color: #27ae60; }
    .stock.low-stock { color: #f39c12; }
    .product-actions { padding: 1rem; border-top: 1px solid #f0f0f0; display: flex; gap: 0.5rem; }
    .btn-cart { flex: 1; padding: 0.7rem; background: linear-gradient(135deg, #e94560, #c0392b); color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600; transition: opacity 0.2s; }
    .btn-cart:hover { opacity: 0.9; }
    .btn-cart.disabled { background: #ddd; color: #999; cursor: not-allowed; }
    .btn-edit { padding: 0.7rem 1rem; background: #f39c12; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 0.85rem; }
    .no-products { grid-column: 1/-1; text-align: center; padding: 3rem; color: #999; font-size: 1.2rem; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
  `]
})
export class ProductListComponent implements OnInit {
  products = signal<Product[]>([]);
  filteredProducts = signal<Product[]>([]);
  categories = signal<string[]>([]);
  loading = signal(true);
  addedMessage = signal('');

  searchTerm = '';
  selectedSupplier = '';
  selectedCategory = '';

  constructor(
    private productService: ProductService,
    public cartService: CartService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.productService.getAll().subscribe({
      next: (products) => {
        this.products.set(products);
        this.filteredProducts.set(products);
        const cats = [...new Set(products.map(p => p.category).filter(Boolean))];
        this.categories.set(cats);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  filterProducts(): void {
    let filtered = this.products();
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(p =>
        p.name.toLowerCase().includes(term) || p.description?.toLowerCase().includes(term)
      );
    }
    if (this.selectedSupplier) {
      filtered = filtered.filter(p => p.supplierId === this.selectedSupplier);
    }
    if (this.selectedCategory) {
      filtered = filtered.filter(p => p.category === this.selectedCategory);
    }
    this.filteredProducts.set(filtered);
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product);
    this.addedMessage.set(`${product.name} ajouté au panier !`);
    setTimeout(() => this.addedMessage.set(''), 2000);
  }
}
