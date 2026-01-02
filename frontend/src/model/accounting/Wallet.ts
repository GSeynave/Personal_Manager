export interface Wallet {
  id: number;
  name: string;
  description?: string;
  ownerId: number;
  ownerEmail: string;
  tenantId?: number;
  tenantName?: string;
  createdAt: number;
  updatedAt: number;
  balance?: number;
}

export interface CreateWalletRequest {
  name: string;
  description?: string;
  tenantId?: number;
}

export interface UpdateWalletRequest {
  name?: string;
  description?: string;
  tenantId?: number;
}
