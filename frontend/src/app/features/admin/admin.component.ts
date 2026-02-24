import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { Product, CreateProductRequest } from '../../core/models/product.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div>
      <h1><i class="fas fa-tools"></i> Panneau d'administration</h1>
      <p class="subtitle">Gestion des produits (Rôle ADMIN requis)</p>

      <div class="admin-layout">
        <!-- Formulaire produit -->
        <div class="form-panel">
          <h3>{{ editingProduct ? 'Modifier le produit' : 'Ajouter un produit' }}</h3>
          <div class="form-group">
            <label>Nom</label>
            <input [(ngModel)]="productForm.name" type="text" placeholder="Nom du produit" />
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea [(ngModel)]="productForm.description" rows="2"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Prix (FCFA)</label>
              <input [(ngModel)]="productForm.price" type="number" placeholder="99.99" />
            </div>
            <div class="form-group">
              <label>Stock</label>
              <input [(ngModel)]="productForm.stock" type="number" placeholder="100" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Fournisseur</label>
              <select [(ngModel)]="productForm.supplierId">
                <option value="f1">Fournisseur F1</option>
                <option value="f2">Fournisseur F2</option>
              </select>
            </div>
            <div class="form-group">
              <label>Catégorie</label>
              <input [(ngModel)]="productForm.category" type="text" placeholder="Electronique" />
            </div>
          </div>
          <div class="form-actions">
            <button (click)="saveProduct()" class="btn-save">
              @if (editingProduct) {
                <i class="fas fa-save"></i>
              } @else {
              <i class="fas fa-plus-circle"></i>
            }
              
              {{ editingProduct ? ' Mettre à jour' : ' Créer' }}
            </button>
            @if (editingProduct) {
              <button (click)="cancelEdit()" class="btn-cancel-edit">Annuler</button>
            }
          </div>
          @if (successMessage()) {
            <div class="alert-success">{{ successMessage() }}</div>
          }
        </div>

        <!-- Liste produits admin -->
        <div class="products-panel">
          <h3>Produits ({{ products().length }})</h3>
          @for (product of products(); track product.id) {
            <div class="product-row">
              <div class="product-row-info">
                <strong>{{ product.name }}</strong>
                <span class="supplier-tag" [class.f2]="product.supplierId === 'f2'">
                  {{ product.supplierId.toUpperCase() }}
                </span>
                <span>{{ product.price | currency:'FCFA' }}</span>
                <span class="stock-info" [class.low]="product.stock < 20">Stock: {{ product.stock }}</span>
              </div>
              <div class="product-row-actions">
                <button (click)="editProduct(product)" class="btn-edit"><i class="fas fa-edit"></i></button>
                <button (click)="deleteProduct(product.id)" class="btn-delete"><i class="fas fa-trash-alt"></i></button>
              </div>
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styles: [`
    h1 { color: #1a1a2e; }
    .subtitle { color: #666; margin-bottom: 2rem; }
    .admin-layout { display: grid; grid-template-columns: 380px 1fr; gap: 2rem; }
    .form-panel, .products-panel { background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
    h3 { color: #1a1a2e; margin-top: 0; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; margin-bottom: 0.3rem; font-size: 0.9rem; color: #333; font-weight: 500; }
    input, textarea, select { width: 100%; padding: 0.6rem; border: 1px solid #ddd; border-radius: 6px; font-size: 0.9rem; box-sizing: border-box; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
    .form-actions { display: flex; gap: 0.75rem; margin-top: 1rem; }
    .btn-save { flex: 1; padding: 0.75rem; background: #27ae60; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600; }
    .btn-cancel-edit { padding: 0.75rem 1rem; background: #95a5a6; color: white; border: none; border-radius: 6px; cursor: pointer; }
    .alert-success { background: #e8f8e8; color: #27ae60; padding: 0.75rem; border-radius: 6px; margin-top: 1rem; text-align: center; }
    .product-row { display: flex; justify-content: space-between; align-items: center; padding: 0.75rem; border-bottom: 1px solid #f5f5f5; }
    .product-row:last-child { border-bottom: none; }
    .product-row-info { display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap; }
    .supplier-tag { background: #3498db; color: white; font-size: 0.7rem; padding: 0.1rem 0.4rem; border-radius: 10px; font-weight: bold; }
    .supplier-tag.f2 { background: #8e44ad; }
    .stock-info { font-size: 0.85rem; color: #27ae60; }
    .stock-info.low { color: #f39c12; }
    .product-row-actions { display: flex; gap: 0.5rem; }
    .btn-edit, .btn-delete { background: none; border: 1px solid #ddd; border-radius: 6px; padding: 0.3rem 0.6rem; cursor: pointer; font-size: 1rem; transition: all 0.2s; }
    .btn-edit:hover { border-color: #f39c12; }
    .btn-delete:hover { border-color: #e74c3c; }
    @media (max-width: 768px) { .admin-layout { grid-template-columns: 1fr; } }
  `]
})
export class AdminComponent implements OnInit {
  products = signal<Product[]>([]);
  editingProduct: Product | null = null;
  successMessage = signal('');

  productForm: CreateProductRequest = this.emptyForm();

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getAll().subscribe(p => this.products.set(p));
  }

  saveProduct(): void {
    const obs = this.editingProduct
      ? this.productService.update(this.editingProduct.id, this.productForm)
      : this.productService.create(this.productForm);

    obs.subscribe({
      next: () => {
        this.successMessage.set(this.editingProduct ? 'Produit mis à jour !' : 'Produit créé !');
        this.cancelEdit();
        this.loadProducts();
        setTimeout(() => this.successMessage.set(''), 3000);
      }
    });
  }

  editProduct(product: Product): void {
    this.editingProduct = product;
    this.productForm = {
      name: product.name, description: product.description, price: product.price,
      stock: product.stock, supplierId: product.supplierId, storeId: product.storeId,
      category: product.category, available: product.available
    };
  }

  deleteProduct(id: string): void {
    if (confirm('Supprimer ce produit ?')) {
      this.productService.delete(id).subscribe(() => this.loadProducts());
    }
  }

  cancelEdit(): void {
    this.editingProduct = null;
    this.productForm = this.emptyForm();
  }

  emptyForm(): CreateProductRequest {
    return { name: '', description: '', price: 0, stock: 0, supplierId: 'f1', storeId: '', category: '', available: true };
  }
}
