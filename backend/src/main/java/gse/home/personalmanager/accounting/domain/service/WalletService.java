package gse.home.personalmanager.accounting.domain.service;

import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import gse.home.personalmanager.accounting.domain.model.WalletRole;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionRepository;
import gse.home.personalmanager.accounting.infrastructure.repository.WalletPermissionRepository;
import gse.home.personalmanager.accounting.infrastructure.repository.WalletRepository;
import gse.home.personalmanager.core.exception.ConflictException;
import gse.home.personalmanager.core.exception.ForbiddenException;
import gse.home.personalmanager.core.exception.ResourceNotFoundException;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.Tenant;
import gse.home.personalmanager.user.infrastructure.repository.TenantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Domain service layer - handles business rules for wallet management
 */
@Service
@Slf4j
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletPermissionRepository permissionRepository;
    private final TenantRepository tenantRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Creates a new wallet with the given owner
     */
    @Transactional
    public Wallet createWallet(String name, String description, AppUser owner, Long tenantId) {
        Wallet wallet = new Wallet();
        wallet.setName(name);
        wallet.setDescription(description);
        wallet.setOwner(owner);
        
        if (tenantId != null) {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));
            wallet.setTenant(tenant);
        }
        
        return walletRepository.save(wallet);
    }

    /**
     * Updates an existing wallet
     * Business rule: Only the owner can update the wallet
     */
    @Transactional
    public Wallet updateWallet(Long walletId, String name, String description, Long tenantId, AppUser currentUser) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));
        
        validateOwnership(wallet, currentUser);
        
        if (name != null) {
            wallet.setName(name);
        }
        if (description != null) {
            wallet.setDescription(description);
        }
        if (tenantId != null) {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));
            wallet.setTenant(tenant);
        }
        
        return walletRepository.save(wallet);
    }

    /**
     * Deletes a wallet
     * Business rule: Only the owner can delete the wallet
     */
    @Transactional
    public void deleteWallet(Long walletId, AppUser currentUser) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));
        
        validateOwnership(wallet, currentUser);
        
        // Delete all permissions associated with this wallet (single query)

        transactionRepository.deleteAllByWallet(wallet);
        permissionRepository.deleteAllByWallet(wallet);
        
        walletRepository.delete(wallet);
    }

    /**
     * Retrieves all wallets accessible by the user (owned + shared)
     */
    public List<Wallet> getWalletsForUser(AppUser user) {
        // Get wallets owned by the user
        List<Wallet> ownedWallets = walletRepository.findAllByOwner(user);
        
        // Get wallets shared with the user through permissions
        List<WalletPermission> sharedPermissions = permissionRepository.findAllByUser(user);
        List<Wallet> sharedWallets = sharedPermissions.stream()
                .map(WalletPermission::getWallet)
                .toList();
        
        // Combine and deduplicate
        return java.util.stream.Stream.concat(ownedWallets.stream(), sharedWallets.stream())
                .distinct()
                .toList();
    }

    /**
     * Retrieves all wallets for a tenant
     */
    public List<Wallet> getWalletsForTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));
        return walletRepository.findAllByTenant(tenant);
    }

    /**
     * Grants permission to a user for a wallet
     * Business rule: Only the owner can grant permissions
     */
    @Transactional
    public WalletPermission grantPermission(Long walletId, Long userId, WalletRole role, AppUser currentUser, AppUser targetUser) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));
        
        validateOwnership(wallet, currentUser);
        
        // Check if permission already exists
        permissionRepository.findByWalletAndUser(wallet, targetUser)
                .ifPresent(existing -> {
                    throw new ConflictException("Permission already exists for this user on this wallet");
                });
        
        WalletPermission permission = new WalletPermission();
        permission.setWallet(wallet);
        permission.setUser(targetUser);
        permission.setRole(role);
        
        return permissionRepository.save(permission);
    }

    /**
     * Updates permission for a user on a wallet
     * Business rule: Only the owner can update permissions
     */
    @Transactional
    public WalletPermission updatePermission(Long walletId, Long userId, WalletRole role, AppUser currentUser, AppUser targetUser) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));
        
        validateOwnership(wallet, currentUser);
        
        WalletPermission permission = permissionRepository.findByWalletAndUser(wallet, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found for this user on this wallet"));
        
        permission.setRole(role);
        return permissionRepository.save(permission);
    }

    /**
     * Revokes permission from a user for a wallet
     * Business rule: Only the owner can revoke permissions
     */
    @Transactional
    public void revokePermission(Long walletId, Long userId, AppUser currentUser, AppUser targetUser) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));
        
        validateOwnership(wallet, currentUser);
        
        permissionRepository.deleteByWalletAndUser(wallet, targetUser);
    }

    /**
     * Gets all permissions for a wallet
     * Business rule: Only the owner can view all permissions
     */
    public List<WalletPermission> getPermissionsForWallet(Long walletId, AppUser currentUser) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));
        
        validateOwnership(wallet, currentUser);
        
        return permissionRepository.findAllByWallet(wallet);
    }

    /**
     * Checks if the user has write access to the wallet
     */
    public boolean hasWriteAccess(Wallet wallet, AppUser user) {
        // Owner always has write access
        if (wallet.getOwner().getId().equals(user.getId())) {
            return true;
        }
        
        // Check if user has WRITE permission
        return permissionRepository.findByWalletAndUser(wallet, user)
                .map(permission -> WalletRole.WRITE.equals(permission.getRole()))
                .orElse(false);
    }

    /**
     * Checks if the user has read access to the wallet
     */
    public boolean hasReadAccess(Wallet wallet, AppUser user) {
        // Owner always has read access
        if (wallet.getOwner().getId().equals(user.getId())) {
            return true;
        }
        
        // Check if user has any permission (READ or WRITE)
        return permissionRepository.findByWalletAndUser(wallet, user).isPresent();
    }

    /**
     * Validates that the current user is the owner of the wallet
     */
    private void validateOwnership(Wallet wallet, AppUser currentUser) {
        if (!wallet.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the wallet owner can perform this operation");
        }
    }

    /**
     * Gets the current balance for a wallet.
     * The balance is retrieved from the most recent transaction's currentBalance field.
     * Returns null if there are no transactions for the wallet.
     */
    public Double getCurrentBalance(Long walletId) {
        return transactionRepository.findFirstByWalletIdOrderByDateDescIdDesc(walletId)
                .map(transaction -> transaction.getCurrentBalance())
                .orElse(null);
    }
}
