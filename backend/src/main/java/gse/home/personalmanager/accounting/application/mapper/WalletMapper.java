package gse.home.personalmanager.accounting.application.mapper;

import gse.home.personalmanager.accounting.application.dto.WalletDTO;
import gse.home.personalmanager.accounting.application.dto.WalletPermissionDTO;
import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

  @Mapping(source = "owner.id", target = "ownerId")
  @Mapping(source = "owner.email", target = "ownerEmail")
  @Mapping(source = "tenant.id", target = "tenantId")
  WalletDTO toDto(Wallet wallet);

  @Mapping(source = "wallet.id", target = "walletId")
  @Mapping(source = "wallet.name", target = "walletName")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.email", target = "userEmail")
  @Mapping(source = "rolePermission", target = "role")
  WalletPermissionDTO toDto(WalletPermission permission);
}
