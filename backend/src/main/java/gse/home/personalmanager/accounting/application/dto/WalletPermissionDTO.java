package gse.home.personalmanager.accounting.application.dto;

import gse.home.personalmanager.accounting.domain.model.WalletRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletPermissionDTO {
    private Long id;
    private Long walletId;
    private String walletName;
    private Long userId;
    private String userEmail;
    private WalletRole role;
    private Long createdAt;
}
