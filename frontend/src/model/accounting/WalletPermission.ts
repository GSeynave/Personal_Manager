export enum WalletRole {
  READ = 'READ',
  WRITE = 'WRITE'
}

export interface WalletPermission {
  id: number;
  walletId: number;
  walletName: string;
  userId: number;
  userEmail: string;
  role: WalletRole;
  createdAt: number;
}

export interface GrantWalletPermissionRequest {
  userId: number;
  role: WalletRole;
}

export interface UpdateWalletPermissionRequest {
  role: WalletRole;
}
