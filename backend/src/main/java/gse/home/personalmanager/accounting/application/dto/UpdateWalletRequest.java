package gse.home.personalmanager.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletRequest {
    private String name;
    private String description;
    private Long tenantId;
}
