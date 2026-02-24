import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <h1><i class="fas fa-boxes"></i> Mes commandes</h1>

      @if (loading()) {
        <div class="loading"><i class="fas fa-clock"></i> Chargement...</div>
      } @else if (orders().length === 0) {
        <div class="empty">Aucune commande pour le moment</div>
      } @else {
        @for (order of orders(); track order.id) {
          <div class="order-card">
            <div class="order-header">
              <div>
                <span class="order-id">#{{ order.id.substring(0, 8).toUpperCase() }}</span>
                <span class="order-date">{{ order.createdAt | date:'dd/MM/yyyy HH:mm' }}</span>
              </div>
              <span class="status-badge" [class]="'status-' + order.status.toLowerCase()">
                {{ getStatusLabel(order.status) }}
              </span>
            </div>
            <div class="order-items">
              @for (item of order.items; track item.productId) {
                <div class="order-item">
                  <span class="supplier-badge" [class.f2]="item.supplierId === 'f2'">
                    {{ item.supplierId.toUpperCase() }}
                  </span>
                  <span>{{ item.productName }} × {{ item.quantity }}</span>
                  <span>{{ (item.unitPrice * item.quantity) | currency:'FCFA' }}</span>
                </div>
              }
            </div>
            <div class="order-footer">
              <span class="shipping"><i class="fas fa-map-marker-alt"></i> {{ order.shippingAddress }}</span>
              <span class="total">Total : {{ calculateTotal(order) | currency:'FCFA' }}</span>
            </div>
            @if (order.status === 'CONFIRMED' || order.status === 'PENDING') {
              <button (click)="cancelOrder(order.id)" class="btn-cancel">Annuler</button>
            }
          </div>
        }
      }
    </div>
  `,
  styles: [`
    h1 { color: #1a1a2e; margin-bottom: 2rem; }
    .loading, .empty { text-align: center; padding: 3rem; color: #999; }
    .order-card { background: white; border-radius: 12px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
    .order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .order-id { font-weight: bold; color: #1a1a2e; font-family: monospace; }
    .order-date { color: #999; font-size: 0.9rem; margin-left: 1rem; }
    .status-badge { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }
    .status-confirmed { background: #e8f8e8; color: #27ae60; }
    .status-pending { background: #fff3cd; color: #f39c12; }
    .status-cancelled { background: #fde8e8; color: #e74c3c; }
    .status-shipped { background: #e8f0fe; color: #3498db; }
    .status-delivered { background: #e8f8e8; color: #27ae60; }
    .order-items { border-top: 1px solid #f0f0f0; border-bottom: 1px solid #f0f0f0; padding: 1rem 0; margin: 1rem 0; }
    .order-item { display: flex; align-items: center; gap: 1rem; padding: 0.4rem 0; font-size: 0.9rem; color: #555; }
    .supplier-badge { background: #3498db; color: white; font-size: 0.7rem; padding: 0.1rem 0.4rem; border-radius: 10px; font-weight: bold; }
    .supplier-badge.f2 { background: #8e44ad; }
    .order-item span:last-child { margin-left: auto; font-weight: 600; color: #1a1a2e; }
    .order-footer { display: flex; justify-content: space-between; align-items: center; }
    .shipping { color: #999; font-size: 0.85rem; }
    .total { font-weight: bold; font-size: 1.1rem; color: #e94560; }
    .btn-cancel { margin-top: 1rem; padding: 0.5rem 1.5rem; background: none; border: 1px solid #e74c3c; color: #e74c3c; border-radius: 6px; cursor: pointer; transition: all 0.2s; }
    .btn-cancel:hover { background: #e74c3c; color: white; }
  `]
})
export class OrderListComponent implements OnInit {
  orders = signal<Order[]>([]);
  loading = signal(true);

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (orders) => { this.orders.set(orders); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  calculateTotal(order: Order): number {
    return order.items.reduce((sum, i) => sum + i.unitPrice * i.quantity, 0);
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: ' En attente', CONFIRMED: ' Confirmée',
      PROCESSING: ' En traitement', SHIPPED: ' Expédiée',
      DELIVERED: ' Livrée', CANCELLED: ' Annulée'
    };
    return labels[status] || status;
  }

  cancelOrder(id: string): void {
    this.orderService.cancel(id).subscribe({
      next: (updated) => {
        this.orders.update(orders => orders.map(o => o.id === id ? updated : o));
      }
    });
  }
}
