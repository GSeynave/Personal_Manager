package gse.home.personalmanager.accounting.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransactionCategoryDTO {
  private Integer id;
  private String title;
  private String description;
  private String icon;
  private Double expectedAmount;
  private Integer parentCategoryId;
  private String parentCategoryTitle;
  private List<TransactionCategoryDTO> subCategories;
}
