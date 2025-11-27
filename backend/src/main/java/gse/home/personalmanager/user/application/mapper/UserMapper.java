package gse.home.personalmanager.user.application.mapper;

import gse.home.personalmanager.user.application.dto.UserIdentityDto;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserIdentityDto toDto(AppUser user);
}
