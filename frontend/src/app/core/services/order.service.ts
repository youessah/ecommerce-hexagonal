import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, PlaceOrderRequest } from '../models/order.model';

/**
 * SERVICE - Communication avec l'API commandes.
 */
@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly API_URL = '/api/orders';

  constructor(private http: HttpClient) {}

  placeOrder(request: PlaceOrderRequest): Observable<Order> {
    return this.http.post<Order>(this.API_URL, request);
  }

  getById(id: string): Observable<Order> {
    return this.http.get<Order>(`${this.API_URL}/${id}`);
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.API_URL}/my-orders`);
  }

  cancel(id: string): Observable<Order> {
    return this.http.delete<Order>(`${this.API_URL}/${id}/cancel`);
  }
}
