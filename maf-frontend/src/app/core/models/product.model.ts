export enum ProductionType {
  USN_RK = 'USN_RK',
  INDIVIDUAL = 'INDIVIDUAL'
}

export enum AvailabilityStatus {
  AVAILABLE = 'AVAILABLE',
  RESERVED = 'RESERVED',
  NOT_PRODUCED = 'NOT_PRODUCED'
}

export interface Product {
  id: number;
  name: string;
  description: string;
  articleNumber: string;
  dimensions: string;
  price: number;
  productionType: ProductionType;
  categoryId: number;
  categoryName: string;
  subcategoryId?: number;
  subcategoryName?: string;
  imageUrl: string;
  availability: AvailabilityStatus;
}
