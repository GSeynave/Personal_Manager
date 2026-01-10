package gse.home.personalmanager.accounting.application.mapper;

import gse.home.personalmanager.accounting.application.dto.TransactionCategoryDTO;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionCategoryMapper {

  @Mapping(source = "parentCategory.id", target = "parentCategoryId")
  @Mapping(target = "subCategories", ignore = true)
  TransactionCategoryDTO toDto(TransactionCategory category);

  @Mapping(source = "parentCategory.id", target = "parentCategoryId")
  @Mapping(source = "categories", target = "subCategories", qualifiedByName = "mapSubCategories")
  TransactionCategoryDTO toDtoWithSubCategories(TransactionCategory category);

  @Named("mapSubCategories")
  default List<TransactionCategoryDTO> mapSubCategories(List<TransactionCategory> categories) {
    if (categories == null || categories.isEmpty()) {
      return null;
    }
    return categories.stream()
        .map(this::toDtoWithSubCategories)
        .toList();
  }

  @Mapping(source = "parentCategoryId", target = "parentCategory.id")
  @Mapping(target = "categories", ignore = true)
  TransactionCategory toEntity(TransactionCategoryDTO dto);
}
