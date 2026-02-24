import { Injectable, signal, computed } from '@angular/core';
import { Product } from '../models/product.model';
import { CartItem } from '../models/order.model';

/**
 * SERVICE - Gestion du panier côté client.
 * Utilise les Angular Signals pour la réactivité.
 */
@Injectable({ providedIn: 'root' })
export class CartService {
  private _items = signal<CartItem[]>([]);

  items = this._items.asReadonly();
  itemCount = computed(() => this._items().reduce((sum, i) => sum + i.quantity, 0));
  total = computed(() =>
    this._items().reduce((sum, i) => sum + i.product.price * i.quantity, 0)
  );

  addToCart(product: Product, quantity: number = 1): void {
    this._items.update(items => {
      const existing = items.find(i => i.product.id === product.id);
      if (existing) {
        return items.map(i =>
          i.product.id === product.id
            ? { ...i, quantity: i.quantity + quantity }
            : i
        );
      }
      return [...items, { product, quantity }];
    });
  }

  removeFromCart(productId: string): void {
    this._items.update(items => items.filter(i => i.product.id !== productId));
  }

  updateQuantity(productId: string, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    this._items.update(items =>
      items.map(i => i.product.id === productId ? { ...i, quantity } : i)
    );
  }

  clearCart(): void {
    this._items.set([]);
  }

  toOrderItems(): { productId: string; supplierId: string; quantity: number }[] {
    return this._items().map(item => ({
      productId: item.product.id,
      supplierId: item.product.supplierId,
      quantity: item.quantity
    }));
  }
}
