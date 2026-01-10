package gse.home.personalmanager.accounting.application.mapper;

import gse.home.personalmanager.accounting.application.dto.WalletDTO;
import gse.home.personalmanager.accounting.application.dto.WalletPermissionDTO;
import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import gse.home.personalmanager.accounting.domain.model.WalletRole;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.Tenant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class WalletMapperTest extends UnitTestBase {

  private WalletMapper walletMapper = Mappers.getMapper(WalletMapper.class);

  @Test
  void toDto_withWallet_shouldMapCorrectly() {
    // Given
    AppUser owner = new AppUser();
    owner.setId(1L);
    owner.setEmail("owner@test.com");

    Tenant tenant = new Tenant();
    tenant.setId(1L);

    Wallet wallet = new Wallet();
    wallet.setId(1L);
    wallet.setName("Test Wallet");
    wallet.setDescription("Test Description");
    wallet.setOwner(owner);
    wallet.setTenant(tenant);
    wallet.setCreatedAt(1000L);
    wallet.setUpdatedAt(2000L);

    // When
    WalletDTO result = walletMapper.toDto(wallet);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("Test Wallet");
    assertThat(result.getDescription()).isEqualTo("Test Description");
    assertThat(result.getOwnerId()).isEqualTo(1L);
    assertThat(result.getOwnerEmail()).isEqualTo("owner@test.com");
    assertThat(result.getTenantId()).isEqualTo(1L);
    assertThat(result.getCreatedAt()).isEqualTo(1000L);
    assertThat(result.getUpdatedAt()).isEqualTo(2000L);
  }

  @Test
  void toDto_withNullWallet_shouldReturnNull() {
    // When
    WalletDTO result = walletMapper.toDto((Wallet) null);

    // Then
    assertThat(result).isNull();
  }

  @Test
  void toDto_withWalletWithoutTenant_shouldMapCorrectly() {
    // Given
    AppUser owner = new AppUser();
    owner.setId(1L);
    owner.setEmail("owner@test.com");

    Wallet wallet = new Wallet();
    wallet.setId(1L);
    wallet.setName("Test Wallet");
    wallet.setOwner(owner);

    // When
    WalletDTO result = walletMapper.toDto(wallet);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTenantId()).isNull();
  }

  @Test
  void toDto_withPermission_shouldMapCorrectly() {
    // Given
    AppUser user = new AppUser();
    user.setId(2L);
    user.setEmail("user@test.com");

    Wallet wallet = new Wallet();
    wallet.setId(1L);
    wallet.setName("Test Wallet");

    WalletPermission permission = new WalletPermission();
    permission.setId(1L);
    permission.setWallet(wallet);
    permission.setUser(user);
    permission.setRolePermission(WalletRole.READ);
    permission.setCreatedAt(1000L);

    // When
    WalletPermissionDTO result = walletMapper.toDto(permission);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getWalletId()).isEqualTo(1L);
    assertThat(result.getWalletName()).isEqualTo("Test Wallet");
    assertThat(result.getUserId()).isEqualTo(2L);
    assertThat(result.getUserEmail()).isEqualTo("user@test.com");
    assertThat(result.getRole()).isEqualTo(WalletRole.READ);
    assertThat(result.getCreatedAt()).isEqualTo(1000L);
  }

  @Test
  void toDto_withNullPermission_shouldReturnNull() {
    // When
    WalletPermissionDTO result = walletMapper.toDto((WalletPermission) null);

    // Then
    assertThat(result).isNull();
  }
}
