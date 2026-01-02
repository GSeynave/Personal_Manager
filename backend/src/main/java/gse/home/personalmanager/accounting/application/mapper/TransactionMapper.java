package gse.home.personalmanager.accounting.application.mapper;

import gse.home.personalmanager.accounting.application.dto.TransactionDTO;
import gse.home.personalmanager.accounting.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "wallet.id", target = "walletId")
  @Mapping(source = "wallet.name", target = "walletName")
  @Mapping(source = "relatedTransaction.id", target = "relatedTransactionId")
  TransactionDTO toDto(Transaction todo);

  @Mapping(source = "walletId", target = "wallet.id")
  @Mapping(target = "relatedTransaction", ignore = true)
  @Mapping(target = "user", ignore = true)
  Transaction toEntity(TransactionDTO dto);

}
