package gse.home.personalmanager.accounting.application.service;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.mapper.WalletMapper;
import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import gse.home.personalmanager.accounting.domain.model.WalletRole;
import gse.home.personalmanager.accounting.domain.service.WalletService;
import gse.home.personalmanager.accounting.infrastructure.repository.WalletRepository;
import gse.home.personalmanager.core.exception.ForbiddenException;
import gse.home.personalmanager.core.exception.ResourceNotFoundException;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WalletUseCaseServiceTest extends UnitTestBase {

    @Mock
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private WalletUseCaseService walletUseCaseService;

    private AppUser currentUser;
    private AppUser targetUser;
    private Wallet wallet;
    private WalletDTO walletDTO;

    @BeforeEach
    void setUp() {
        currentUser = new AppUser();
        currentUser.setId(1L);
        currentUser.setEmail("current@test.com");

        targetUser = new AppUser();
        targetUser.setId(2L);
        targetUser.setEmail("target@test.com");

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setName("Test Wallet");
        wallet.setDescription("Test Description");
        wallet.setOwner(currentUser);

        walletDTO = WalletDTO.builder()
                .id(1L)
                .name("Test Wallet")
                .description("Test Description")
                .ownerId(1L)
                .ownerEmail("current@test.com")
                .build();
    }

    @Test
    void createWallet_shouldCreateWalletSuccessfully() {
        // Given
        CreateWalletRequest request = CreateWalletRequest.builder()
                .name("New Wallet")
                .description("Description")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(walletService.createWallet(eq("New Wallet"), eq("Description"), eq(currentUser), any()))
                .thenReturn(wallet);
        when(walletMapper.toDto(wallet)).thenReturn(walletDTO);

        // When
        WalletDTO result = walletUseCaseService.createWallet(request, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Wallet");
        verify(walletService).createWallet(eq("New Wallet"), eq("Description"), eq(currentUser), any());
        verify(walletMapper).toDto(wallet);
    }

    @Test
    void createWallet_whenUserNotFound_shouldThrowException() {
        // Given
        CreateWalletRequest request = CreateWalletRequest.builder()
                .name("New Wallet")
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> walletUseCaseService.createWallet(request, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateWallet_shouldUpdateWalletSuccessfully() {
        // Given
        UpdateWalletRequest request = UpdateWalletRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(walletService.updateWallet(eq(1L), eq("Updated Name"), eq("Updated Description"), any(), eq(currentUser)))
                .thenReturn(wallet);
        when(walletMapper.toDto(wallet)).thenReturn(walletDTO);

        // When
        WalletDTO result = walletUseCaseService.updateWallet(1L, request, 1L);

        // Then
        assertThat(result).isNotNull();
        verify(walletService).updateWallet(eq(1L), eq("Updated Name"), eq("Updated Description"), any(), eq(currentUser));
    }

    @Test
    void deleteWallet_shouldDeleteWalletSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));

        // When
        walletUseCaseService.deleteWallet(1L, 1L);

        // Then
        verify(walletService).deleteWallet(1L, currentUser);
    }

    @Test
    void getWallet_withAccess_shouldReturnWallet() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletService.hasReadAccess(wallet, currentUser)).thenReturn(true);
        when(walletMapper.toDto(wallet)).thenReturn(walletDTO);

        // When
        WalletDTO result = walletUseCaseService.getWallet(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(walletService).hasReadAccess(wallet, currentUser);
    }

    @Test
    void getWallet_withoutAccess_shouldThrowSecurityException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletService.hasReadAccess(wallet, currentUser)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> walletUseCaseService.getWallet(1L, 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("does not have access");
    }

    @Test
    void getWallet_whenNotFound_shouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(walletRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> walletUseCaseService.getWallet(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Wallet not found");
    }

    @Test
    void getWalletsForCurrentUser_shouldReturnUserWallets() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(walletService.getWalletsForUser(currentUser)).thenReturn(List.of(wallet));
        when(walletMapper.toDto(wallet)).thenReturn(walletDTO);

        // When
        List<WalletDTO> result = walletUseCaseService.getWalletsForCurrentUser(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getWalletsForTenant_shouldReturnTenantWallets() {
        // Given
        when(walletService.getWalletsForTenant(1L)).thenReturn(List.of(wallet));
        when(walletMapper.toDto(wallet)).thenReturn(walletDTO);

        // When
        List<WalletDTO> result = walletUseCaseService.getWalletsForTenant(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(walletService).getWalletsForTenant(1L);
    }

    @Test
    void grantPermission_shouldGrantPermissionSuccessfully() {
        // Given
        GrantWalletPermissionRequest request = GrantWalletPermissionRequest.builder()
                .userId(2L)
                .role(WalletRole.READ)
                .build();

        WalletPermission permission = new WalletPermission();
        WalletPermissionDTO permissionDTO = WalletPermissionDTO.builder()
                .id(1L)
                .walletId(1L)
                .userId(2L)
                .role(WalletRole.READ)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(walletService.grantPermission(1L, 2L, WalletRole.READ, currentUser, targetUser))
                .thenReturn(permission);
        when(walletMapper.toDto(permission)).thenReturn(permissionDTO);

        // When
        WalletPermissionDTO result = walletUseCaseService.grantPermission(1L, request, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getRole()).isEqualTo(WalletRole.READ);
    }

    @Test
    void grantPermission_whenTargetUserNotFound_shouldThrowException() {
        // Given
        GrantWalletPermissionRequest request = GrantWalletPermissionRequest.builder()
                .userId(999L)
                .role(WalletRole.READ)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> walletUseCaseService.grantPermission(1L, request, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updatePermission_shouldUpdatePermissionSuccessfully() {
        // Given
        UpdateWalletPermissionRequest request = UpdateWalletPermissionRequest.builder()
                .role(WalletRole.WRITE)
                .build();

        WalletPermission permission = new WalletPermission();
        WalletPermissionDTO permissionDTO = WalletPermissionDTO.builder()
                .id(1L)
                .walletId(1L)
                .userId(2L)
                .role(WalletRole.WRITE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(walletService.updatePermission(1L, 2L, WalletRole.WRITE, currentUser, targetUser))
                .thenReturn(permission);
        when(walletMapper.toDto(permission)).thenReturn(permissionDTO);

        // When
        WalletPermissionDTO result = walletUseCaseService.updatePermission(1L, 2L, request, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(WalletRole.WRITE);
    }

    @Test
    void revokePermission_shouldRevokePermissionSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        // When
        walletUseCaseService.revokePermission(1L, 2L, 1L);

        // Then
        verify(walletService).revokePermission(1L, 2L, currentUser, targetUser);
    }

    @Test
    void getPermissionsForWallet_shouldReturnPermissions() {
        // Given
        WalletPermission permission = new WalletPermission();
        WalletPermissionDTO permissionDTO = WalletPermissionDTO.builder()
                .id(1L)
                .walletId(1L)
                .userId(2L)
                .role(WalletRole.READ)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(walletService.getPermissionsForWallet(1L, currentUser)).thenReturn(List.of(permission));
        when(walletMapper.toDto(permission)).thenReturn(permissionDTO);

        // When
        List<WalletPermissionDTO> result = walletUseCaseService.getPermissionsForWallet(1L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWalletId()).isEqualTo(1L);
    }
}
