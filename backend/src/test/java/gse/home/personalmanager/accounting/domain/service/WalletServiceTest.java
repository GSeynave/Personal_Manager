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
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.Tenant;
import gse.home.personalmanager.user.infrastructure.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletServiceTest extends UnitTestBase {

  @Mock
  private WalletRepository walletRepository;

  @Mock
  private WalletPermissionRepository permissionRepository;

  @Mock
  private TenantRepository tenantRepository;
  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private WalletService walletService;

  private AppUser owner;
  private AppUser otherUser;
  private Tenant tenant;
  private Wallet wallet;

  @BeforeEach
  void setUp() {
    owner = new AppUser();
    owner.setId(1L);
    owner.setEmail("owner@test.com");

    otherUser = new AppUser();
    otherUser.setId(2L);
    otherUser.setEmail("other@test.com");

    tenant = new Tenant();
    tenant.setId(1L);

    wallet = new Wallet();
    wallet.setId(1L);
    wallet.setName("Test Wallet");
    wallet.setOwner(owner);
    wallet.setTenant(tenant);
  }

  @Test
  void createWallet_shouldCreateWalletSuccessfully() {
    // Given
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

    // When
    Wallet result = walletService.createWallet("Test Wallet", "Description", owner, null);

    // Then
    assertThat(result).isNotNull();
    verify(walletRepository).save(any(Wallet.class));
  }

  @Test
  void createWallet_withTenant_shouldCreateWalletWithTenant() {
    // Given
    when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

    // When
    Wallet result = walletService.createWallet("Test Wallet", "Description", owner, 1L);

    // Then
    assertThat(result).isNotNull();
    verify(tenantRepository).findById(1L);
    verify(walletRepository).save(any(Wallet.class));
  }

  @Test
  void createWallet_withInvalidTenant_shouldThrowException() {
    // Given
    when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> walletService.createWallet("Test Wallet", "Description", owner, 999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Tenant not found");
  }

  @Test
  void updateWallet_byOwner_shouldUpdateSuccessfully() {
    // Given
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

    // When
    Wallet result = walletService.updateWallet(1L, "Updated Name", "Updated Description", null, owner);

    // Then
    assertThat(result).isNotNull();
    verify(walletRepository).save(any(Wallet.class));
  }

  @Test
  void updateWallet_byNonOwner_shouldThrowSecurityException() {
    // Given
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

    // When & Then
    assertThatThrownBy(() -> walletService.updateWallet(1L, "Updated Name", null, null, otherUser))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("Only the wallet owner");
  }

  @Test
  void deleteWallet_byOwner_shouldDeleteSuccessfully() {
    // Given
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

    // When
    walletService.deleteWallet(1L, owner);

    // Then
    verify(walletRepository).delete(wallet);
    verify(permissionRepository).deleteAllByWallet(wallet);
  }

  @Test
  void deleteWallet_byNonOwner_shouldThrowSecurityException() {
    // Given
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

    // When & Then
    assertThatThrownBy(() -> walletService.deleteWallet(1L, otherUser))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("Only the wallet owner");
  }

  @Test
  void getWalletsForUser_shouldReturnOwnedAndSharedWallets() {
    // Given
    Wallet ownedWallet = new Wallet();
    ownedWallet.setId(1L);
    ownedWallet.setName("Owned Wallet");

    Wallet sharedWallet = new Wallet();
    sharedWallet.setId(2L);
    sharedWallet.setName("Shared Wallet");

    WalletPermission permission = new WalletPermission();
    permission.setWallet(sharedWallet);

    when(walletRepository.findAllByOwner(owner)).thenReturn(List.of(ownedWallet));
    when(permissionRepository.findAllByUser(owner)).thenReturn(List.of(permission));

    // When
    List<Wallet> result = walletService.getWalletsForUser(owner);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).contains(ownedWallet, sharedWallet);
  }

  @Test
  void grantPermission_byOwner_shouldGrantSuccessfully() {
    // Given
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
    when(permissionRepository.findByWalletAndUser(wallet, otherUser)).thenReturn(Optional.empty());
    when(permissionRepository.save(any(WalletPermission.class))).thenAnswer(i -> i.getArgument(0));

    // When
    WalletPermission result = walletService.grantPermission(1L, 2L, WalletRole.READ, owner, otherUser);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getRolePermission()).isEqualTo(WalletRole.READ);
    verify(permissionRepository).save(any(WalletPermission.class));
  }

  @Test
  void grantPermission_whenAlreadyExists_shouldThrowException() {
    // Given
    WalletPermission existingPermission = new WalletPermission();
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
    when(permissionRepository.findByWalletAndUser(wallet, otherUser)).thenReturn(Optional.of(existingPermission));

    // When & Then
    assertThatThrownBy(() -> walletService.grantPermission(1L, 2L, WalletRole.READ, owner, otherUser))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Permission already exists");
  }

  @Test
  void grantPermission_byNonOwner_shouldThrowSecurityException() {
    // Given
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

    // When & Then
    assertThatThrownBy(() -> walletService.grantPermission(1L, 2L, WalletRole.READ, otherUser, otherUser))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("Only the wallet owner");
  }

  @Test
  void updatePermission_byOwner_shouldUpdateSuccessfully() {
    // Given
    WalletPermission permission = new WalletPermission();
    permission.setRolePermission(WalletRole.READ);

    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
    when(permissionRepository.findByWalletAndUser(wallet, otherUser)).thenReturn(Optional.of(permission));
    when(permissionRepository.save(any(WalletPermission.class))).thenAnswer(i -> i.getArgument(0));

    // When
    WalletPermission result = walletService.updatePermission(1L, 2L, WalletRole.WRITE, owner, otherUser);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getRolePermission()).isEqualTo(WalletRole.WRITE);
    verify(permissionRepository).save(permission);
  }

  @Test
  void revokePermission_byOwner_shouldRevokeSuccessfully() {
    // Given
    when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

    // When
    walletService.revokePermission(1L, 2L, owner, otherUser);

    // Then
    verify(permissionRepository).deleteByWalletAndUser(wallet, otherUser);
  }

  @Test
  void hasWriteAccess_forOwner_shouldReturnTrue() {
    // When
    boolean result = walletService.hasWriteAccess(wallet, owner);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void hasWriteAccess_forUserWithWritePermission_shouldReturnTrue() {
    // Given
    WalletPermission permission = new WalletPermission();
    permission.setRolePermission(WalletRole.WRITE);

    when(permissionRepository.findByWalletAndUser(wallet, otherUser)).thenReturn(Optional.of(permission));

    // When
    boolean result = walletService.hasWriteAccess(wallet, otherUser);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void hasWriteAccess_forUserWithReadPermission_shouldReturnFalse() {
    // Given
    WalletPermission permission = new WalletPermission();
    permission.setRolePermission(WalletRole.READ);

    when(permissionRepository.findByWalletAndUser(wallet, otherUser)).thenReturn(Optional.of(permission));

    // When
    boolean result = walletService.hasWriteAccess(wallet, otherUser);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void hasReadAccess_forOwner_shouldReturnTrue() {
    // When
    boolean result = walletService.hasReadAccess(wallet, owner);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void hasReadAccess_forUserWithPermission_shouldReturnTrue() {
    // Given
    WalletPermission permission = new WalletPermission();
    when(permissionRepository.findByWalletAndUser(wallet, otherUser)).thenReturn(Optional.of(permission));

    // When
    boolean result = walletService.hasReadAccess(wallet, otherUser);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void hasReadAccess_forUserWithoutPermission_shouldReturnFalse() {
    // Given
    when(permissionRepository.findByWalletAndUser(wallet, otherUser)).thenReturn(Optional.empty());

    // When
    boolean result = walletService.hasReadAccess(wallet, otherUser);

    // Then
    assertThat(result).isFalse();
  }
}
