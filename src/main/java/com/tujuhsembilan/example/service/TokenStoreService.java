package com.tujuhsembilan.example.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.example.configuration.property.AuthProp;
import com.tujuhsembilan.example.controller.dto.LoginDto;
import com.tujuhsembilan.example.model.TokenStore;
import com.tujuhsembilan.example.repository.TokenStoreRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenStoreService {
    private final TokenStoreRepo tokenStoreRepo;
    private final JwtEncoder jwtEncoder;

    public void saveToken(TokenStore tokenStore) {
        tokenStoreRepo.save(tokenStore);
    }

    public void logout(String token) {
        tokenStoreRepo.deleteById(tokenStoreRepo.findByAccessToken(token).get().getId());
    }

    public Object refreshToken(String refreshToken, AuthProp authProp) {
        Instant now = Instant.now();
        Optional<TokenStore> tokenStore = tokenStoreRepo.findByRefreshToken(refreshToken);
        if (tokenStore.get() == null || tokenStore.get().getExprRefresh().isBefore(now)) {
            return OAuth2TokenValidatorResult.failure();
        }
        TokenStore data = tokenStore.get();
        tokenStoreRepo.deleteById(data.getId());
        String access = buildToken(authProp.getUuid(), data.getUsername(), data.getRoles(),
                now.plusMillis(authProp.getExpiration()));
        String refresh = buildToken(authProp.getUuid(), data.getUsername(), data.getRoles(),
                now.plusMillis(authProp.getRefreshExpiration()));
        saveToken(TokenStore.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .username(data.getUsername())
                .roles(data.getRoles())
                .exprRefresh(now.plusMillis(authProp.getRefreshExpiration()))
                .build());
        return LoginDto.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    public String buildToken(String uuid, String subject, String roles, Instant expr) {
        return jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.ES512).build(),
                        JwtClaimsSet.builder()
                                .issuer(uuid)
                                .audience(List.of(uuid))
                                .subject(subject)
                                .claim("roles", roles)
                                .expiresAt(expr)
                                .build()))
                .getTokenValue();
    }
}
