import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateProductRequest, Product } from '../models/product.model';

/**
 * SERVICE - Communication avec l'API produits.
 */
@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly API_URL = '/api/products';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.API_URL);
  }

  getById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/${id}`);
  }

  getBySupplier(supplierId: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API_URL}/supplier/${supplierId}`);
  }

  getByCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API_URL}/category/${category}`);
  }

  create(product: CreateProductRequest): Observable<Product> {
    return this.http.post<Product>(this.API_URL, product);
  }

  update(id: string, product: CreateProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.API_URL}/${id}`, product);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
