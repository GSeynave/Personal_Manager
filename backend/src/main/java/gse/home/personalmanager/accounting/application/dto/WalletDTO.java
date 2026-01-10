package gse.home.personalmanager.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
  private Long id;
  private String name;
  private String description;
  private Long ownerId;
  private String ownerEmail;
  private Long tenantId;
  private Long createdAt;
  private Long updatedAt;
  private Double balance;
}
