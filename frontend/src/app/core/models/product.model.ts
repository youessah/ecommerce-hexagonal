// models/product.model.ts
export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stock: number;
  supplierId: string;
  storeId: string;
  category: string;
  available: boolean;
}

export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  stock: number;
  supplierId: string;
  storeId: string;
  category: string;
  available: boolean;
}
