package com.tujuhsembilan.example.controller;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.ECKey;
import com.tujuhsembilan.example.configuration.property.AuthProp;
import com.tujuhsembilan.example.controller.dto.LoginDto;
import com.tujuhsembilan.example.model.TokenStore;
import com.tujuhsembilan.example.service.TokenStoreService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BasicLoginController {
  private final ObjectMapper objMap;
  private final AuthProp authProp;
  private final ECKey ecJwk;
  private final TokenStoreService tokenStoreService;

  @GetMapping("/jwks.json")
  public Object jwk() throws JsonProcessingException {
    return ResponseEntity.ok(Map.of("keys", Set.of(objMap.readTree(ecJwk.toPublicJWK().toJSONString()))));
  }

  // You MUST login using BASIC AUTH, NOT POST BODY
  @PostMapping("/login")
  public Object login(@NotNull Authentication auth, @RequestBody boolean rememberMe) {
    Instant now = Instant.now();
    var roles = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    String access = tokenStoreService.buildToken(authProp.getUuid(), ((User) auth.getPrincipal()).getUsername(), roles,
        now.plusMillis(rememberMe ? authProp.getExtraExpiration() : authProp.getExpiration()));

    String refresh = tokenStoreService.buildToken(authProp.getUuid(), ((User) auth.getPrincipal()).getUsername(), roles,
        now.plusMillis(authProp.getRefreshExpiration()));

    tokenStoreService.saveToken(TokenStore.builder()
        .accessToken(access)
        .refreshToken(refresh)
        .username(((User) auth.getPrincipal()).getUsername())
        .roles(roles)
        .exprRefresh(now.plusMillis(authProp.getRefreshExpiration()))
        .build());

    return ResponseEntity
        .ok(LoginDto.builder().accessToken(access).refreshToken(refresh).build());
  }

  @PostMapping("/logout")
  public Object logout(HttpServletRequest request) {
    tokenStoreService.logout(request.getHeader("Authorization").substring(7));
    return ResponseEntity.ok("OK");
  }

  @PostMapping("/refresh-token")
  public Object refresh(@RequestBody(required = true) String refreshToken) {
    return ResponseEntity.ok(tokenStoreService.refreshToken(refreshToken, authProp));
  }
}
