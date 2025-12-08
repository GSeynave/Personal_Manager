package gse.home.personalmanager.core.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class JwtUtils {

    @Value("${firebase.project-id}")
    private String firebaseProjectId;


    public DecodedJWT validateToken(String token) throws Exception {
        System.out.println("Project id: " + firebaseProjectId);

        DecodedJWT decodedJWT = JWT.decode(token);
        String kid = decodedJWT.getKeyId();

        // Fetch Firebase certs
        URL url = new URL("https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com");
        InputStream is = url.openStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> certs = mapper.readValue(is, Map.class);

        String certPem = certs.get(kid);
        if (certPem == null) {
            throw new JWTVerificationException("No public key found for kid: " + kid);
        }

        // Convert PEM to PublicKey
        certPem = certPem.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(certPem);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new java.io.ByteArrayInputStream(decoded));
        PublicKey publicKey = cert.getPublicKey();

        Algorithm algorithm = Algorithm.RSA256((java.security.interfaces.RSAPublicKey) publicKey, null);
        JWTVerifier verifier = JWT.require(algorithm)
                .withAudience(firebaseProjectId)
                .withIssuer("https://securetoken.google.com/" + firebaseProjectId)
                .build();

        return verifier.verify(token);
    }
}