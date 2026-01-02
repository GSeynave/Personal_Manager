package gse.home.personalmanager.accounting.application.service;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.mapper.WalletMapper;
import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import gse.home.personalmanager.accounting.domain.service.WalletService;
import gse.home.personalmanager.accounting.infrastructure.repository.WalletRepository;
import gse.home.personalmanager.core.exception.ForbiddenException;
import gse.home.personalmanager.core.exception.ResourceNotFoundException;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use case service layer - orchestrates wallet operations
 */
@Service
@Slf4j
@AllArgsConstructor
public class WalletUseCaseService {

  private final WalletService walletService;
  private final WalletRepository walletRepository;
  private final UserRepository userRepository;
  private final WalletMapper walletMapper;

  /**
   * Creates a new wallet for the current user
   */
  @Transactional
  public WalletDTO createWallet(CreateWalletRequest request, Long currentUserId) {
    log.debug("Creating wallet for user id={}", currentUserId);

    AppUser owner = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    Wallet wallet = walletService.createWallet(
        request.getName(),
        request.getDescription(),
        owner,
        request.getTenantId());

    log.info("Wallet created successfully: id={}, name={}", wallet.getId(), wallet.getName());
    return toDtoWithBalance(wallet);
  }

  /**
   * Updates an existing wallet
   */
  @Transactional
  public WalletDTO updateWallet(Long walletId, UpdateWalletRequest request, Long currentUserId) {
    log.debug("Updating wallet id={} by user id={}", walletId, currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    Wallet wallet = walletService.updateWallet(
        walletId,
        request.getName(),
        request.getDescription(),
        request.getTenantId(),
        currentUser);

    log.info("Wallet updated successfully: id={}", wallet.getId());
    return toDtoWithBalance(wallet);
  }

  /**
   * Deletes a wallet
   */
  @Transactional
  public void deleteWallet(Long walletId, Long currentUserId) {
    log.debug("Deleting wallet id={} by user id={}", walletId, currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    walletService.deleteWallet(walletId, currentUser);

    log.info("Wallet deleted successfully: id={}", walletId);
  }

  /**
   * Retrieves a wallet by ID
   */
  public WalletDTO getWallet(Long walletId, Long currentUserId) {
    log.debug("Retrieving wallet id={} for user id={}", walletId, currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    Wallet wallet = walletRepository.findById(walletId)
        .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));

    // Check if user has access to this wallet
    if (!walletService.hasReadAccess(wallet, currentUser)) {
      throw new ForbiddenException("User does not have access to this wallet");
    }

    return toDtoWithBalance(wallet);
  }

  /**
   * Retrieves all wallets for the current user
   */
  public List<WalletDTO> getWalletsForCurrentUser(Long currentUserId) {
    log.debug("Retrieving all wallets for user id={}", currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    List<Wallet> wallets = walletService.getWalletsForUser(currentUser);

    return wallets.stream()
        .map(this::toDtoWithBalance)
        .toList();
  }

  /**
   * Retrieves all wallets for a tenant
   */
  public List<WalletDTO> getWalletsForTenant(Long tenantId) {
    log.debug("Retrieving all wallets for tenant id={}", tenantId);

    List<Wallet> wallets = walletService.getWalletsForTenant(tenantId);

    return wallets.stream()
        .map(this::toDtoWithBalance)
        .toList();
  }

  /**
   * Grants permission to a user for a wallet
   */
  @Transactional
  public WalletPermissionDTO grantPermission(Long walletId, GrantWalletPermissionRequest request, Long currentUserId) {
    log.debug("Granting permission on wallet id={} to user id={} by user id={}",
        walletId, request.getUserId(), currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    AppUser targetUser = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

    WalletPermission permission = walletService.grantPermission(
        walletId,
        request.getUserId(),
        request.getRole(),
        currentUser,
        targetUser);

    log.info("Permission granted successfully on wallet id={} to user id={}", walletId, request.getUserId());
    return walletMapper.toDto(permission);
  }

  /**
   * Updates permission for a user on a wallet
   */
  @Transactional
  public WalletPermissionDTO updatePermission(Long walletId, Long userId, UpdateWalletPermissionRequest request,
      Long currentUserId) {
    log.debug("Updating permission on wallet id={} for user id={} by user id={}",
        walletId, userId, currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    AppUser targetUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));

    WalletPermission permission = walletService.updatePermission(
        walletId,
        userId,
        request.getRole(),
        currentUser,
        targetUser);

    log.info("Permission updated successfully on wallet id={} for user id={}", walletId, userId);

    // The user will need to get a notification about the role change in real
    // application
    // using the current notification service. event publish + websocket
    // notification.

    return walletMapper.toDto(permission);
  }

  /**
   * Revokes permission from a user for a wallet
   */
  @Transactional
  public void revokePermission(Long walletId, Long userId, Long currentUserId) {
    log.debug("Revoking permission on wallet id={} from user id={} by user id={}",
        walletId, userId, currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    AppUser targetUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));

    walletService.revokePermission(walletId, userId, currentUser, targetUser);

    log.info("Permission revoked successfully on wallet id={} from user id={}", walletId, userId);
  }

  /**
   * Gets all permissions for a wallet
   */
  public List<WalletPermissionDTO> getPermissionsForWallet(Long walletId, Long currentUserId) {
    log.debug("Retrieving permissions for wallet id={} by user id={}", walletId, currentUserId);

    AppUser currentUser = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

    List<WalletPermission> permissions = walletService.getPermissionsForWallet(walletId, currentUser);

    return permissions.stream()
        .map(walletMapper::toDto)
        .toList();
  }

  /**
   * Helper method to convert a Wallet to WalletDTO with balance populated
   * from the most recent transaction's currentBalance field.
   */
  private WalletDTO toDtoWithBalance(Wallet wallet) {
    WalletDTO dto = walletMapper.toDto(wallet);
    dto.setBalance(walletService.getCurrentBalance(wallet.getId()));
    return dto;
  }
}
