package gse.home.personalmanager.core.utils;

import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtils {
    public static Long getUserId(AppUserPrincipal userPrincipal) {
        return userPrincipal.getUser().getId();
    }
}
