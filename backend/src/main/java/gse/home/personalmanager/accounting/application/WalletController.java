package gse.home.personalmanager.accounting.application;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.service.WalletUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("v1/wallets")
@AllArgsConstructor
public class WalletController {

  private final WalletUseCaseService useCaseService;

  @PostMapping
  public ResponseEntity<WalletDTO> createWallet(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @Valid @RequestBody CreateWalletRequest request) {
    log.info("Request to create wallet: name={} by user id={}", request.getName(), principal.getUser().getId());
    WalletDTO wallet = useCaseService.createWallet(request, principal.getUser().getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
  }

  @PutMapping("/{id}")
  public ResponseEntity<WalletDTO> updateWallet(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody UpdateWalletRequest request) {
    log.info("Request to update wallet id={} by user id={}", id, principal.getUser().getId());
    WalletDTO wallet = useCaseService.updateWallet(id, request, principal.getUser().getId());
    return ResponseEntity.ok(wallet);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWallet(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @PathVariable Long id) {
    log.info("Request to delete wallet id={} by user id={}", id, principal.getUser().getId());
    useCaseService.deleteWallet(id, principal.getUser().getId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<WalletDTO> getWallet(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @PathVariable Long id) {
    log.debug("Request to get wallet id={} by user id={}", id, principal.getUser().getId());
    WalletDTO wallet = useCaseService.getWallet(id, principal.getUser().getId());
    return ResponseEntity.ok(wallet);
  }

  @GetMapping
  public ResponseEntity<List<WalletDTO>> getWalletsForCurrentUser(
      @AuthenticationPrincipal AppUserPrincipal principal) {
    log.debug("Request to get all wallets for user id={}", principal.getUser().getId());
    List<WalletDTO> wallets = useCaseService.getWalletsForCurrentUser(principal.getUser().getId());
    return ResponseEntity.ok(wallets);
  }

  @GetMapping("/tenant/{tenantId}")
  public ResponseEntity<List<WalletDTO>> getWalletsForTenant(@PathVariable Long tenantId) {
    log.debug("Request to get all wallets for tenant id={}", tenantId);
    List<WalletDTO> wallets = useCaseService.getWalletsForTenant(tenantId);
    return ResponseEntity.ok(wallets);
  }

  @PostMapping("/{walletId}/permissions")
  public ResponseEntity<WalletPermissionDTO> grantPermission(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @PathVariable Long walletId,
      @Valid @RequestBody GrantWalletPermissionRequest request) {
    log.info("Request to grant permission on wallet id={} to user id={} by user id={}",
        walletId, request.getUserId(), principal.getUser().getId());
    WalletPermissionDTO permission = useCaseService.grantPermission(
        walletId, request, principal.getUser().getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(permission);
  }

  @PutMapping("/{walletId}/permissions/{userId}")
  public ResponseEntity<WalletPermissionDTO> updatePermission(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @PathVariable Long walletId,
      @PathVariable Long userId,
      @Valid @RequestBody UpdateWalletPermissionRequest request) {
    log.info("Request to update permission on wallet id={} for user id={} by user id={}",
        walletId, userId, principal.getUser().getId());
    WalletPermissionDTO permission = useCaseService.updatePermission(
        walletId, userId, request, principal.getUser().getId());
    return ResponseEntity.ok(permission);
  }

  @DeleteMapping("/{walletId}/permissions/{userId}")
  public ResponseEntity<Void> revokePermission(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @PathVariable Long walletId,
      @PathVariable Long userId) {
    log.info("Request to revoke permission on wallet id={} from user id={} by user id={}",
        walletId, userId, principal.getUser().getId());
    useCaseService.revokePermission(walletId, userId, principal.getUser().getId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{walletId}/permissions")
  public ResponseEntity<List<WalletPermissionDTO>> getPermissionsForWallet(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @PathVariable Long walletId) {
    log.debug("Request to get permissions for wallet id={} by user id={}",
        walletId, principal.getUser().getId());
    List<WalletPermissionDTO> permissions = useCaseService.getPermissionsForWallet(
        walletId, principal.getUser().getId());
    return ResponseEntity.ok(permissions);
  }
}
