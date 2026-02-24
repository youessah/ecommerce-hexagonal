// models/order.model.ts
export interface OrderItem {
  productId: string;
  productName: string;
  supplierId: string;
  quantity: number;
  unitPrice: number;
}

export interface Order {
  id: string;
  customerId: string;
  items: OrderItem[];
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
  shippingAddress: string;
  total?: number;
}

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface PlaceOrderRequest {
  items: { productId: string; supplierId: string; quantity: number }[];
  shippingAddress: string;
}

export interface CartItem {
  product: import('./product.model').Product;
  quantity: number;
}
