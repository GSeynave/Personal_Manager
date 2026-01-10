package gse.home.personalmanager.accounting.application.dto;

import gse.home.personalmanager.accounting.domain.model.WalletRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletPermissionRequest {
  @NotNull(message = "Role is required")
  private WalletRole role;
}
