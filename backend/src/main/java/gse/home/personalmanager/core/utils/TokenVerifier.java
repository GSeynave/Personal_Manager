package gse.home.personalmanager.core.utils;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface TokenVerifier {

  public DecodedJWT validateToken(String token) throws Exception;
}
