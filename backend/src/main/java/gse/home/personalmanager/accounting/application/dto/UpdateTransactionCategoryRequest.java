package gse.home.personalmanager.accounting.application.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTransactionCategoryRequest {
  
  @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
  private String title;
  
  @Size(max = 500, message = "Description must not exceed 500 characters")
  private String description;
  
  @Size(max = 100, message = "Icon must not exceed 100 characters")
  private String icon;
  
  private Double expectedAmount;
  
  private Integer parentCategoryId;
}
