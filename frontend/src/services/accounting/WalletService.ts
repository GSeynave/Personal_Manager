import axios from 'axios';
import type { Wallet, CreateWalletRequest, UpdateWalletRequest } from '@/model/accounting/Wallet';
import type { WalletPermission, GrantWalletPermissionRequest, UpdateWalletPermissionRequest } from '@/model/accounting/WalletPermission';

const API_URL = `/api/wallets`;

export default class WalletService {

  // Wallet CRUD operations
  
  async createWallet(request: CreateWalletRequest): Promise<Wallet> {
    const response = await axios.post(API_URL, request);
    return response.data;
  }

  async updateWallet(id: number, request: UpdateWalletRequest): Promise<Wallet> {
    const response = await axios.put(`${API_URL}/${id}`, request);
    return response.data;
  }

  async deleteWallet(id: number): Promise<void> {
    await axios.delete(`${API_URL}/${id}`);
  }

  async getWallet(id: number): Promise<Wallet> {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  }

  async getWalletsForCurrentUser(): Promise<Wallet[]> {
    const response = await axios.get(API_URL);
    return response.data;
  }

  async getWalletsForTenant(tenantId: number): Promise<Wallet[]> {
    const response = await axios.get(`${API_URL}/tenant/${tenantId}`);
    return response.data;
  }

  // Permission management operations

  async grantPermission(walletId: number, request: GrantWalletPermissionRequest): Promise<WalletPermission> {
    const response = await axios.post(`${API_URL}/${walletId}/permissions`, request);
    return response.data;
  }

  async updatePermission(walletId: number, userId: number, request: UpdateWalletPermissionRequest): Promise<WalletPermission> {
    const response = await axios.put(`${API_URL}/${walletId}/permissions/${userId}`, request);
    return response.data;
  }

  async revokePermission(walletId: number, userId: number): Promise<void> {
    await axios.delete(`${API_URL}/${walletId}/permissions/${userId}`);
  }

  async getPermissionsForWallet(walletId: number): Promise<WalletPermission[]> {
    const response = await axios.get(`${API_URL}/${walletId}/permissions`);
    return response.data;
  }
}
