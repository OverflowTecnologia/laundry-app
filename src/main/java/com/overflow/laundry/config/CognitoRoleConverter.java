package com.overflow.laundry.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class CognitoRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(Jwt source) {
    ArrayList<String> cognitoGroups = (ArrayList<String>) source.getClaims().get("cognito:groups");
    if (cognitoGroups == null || cognitoGroups.isEmpty()) {
      return new ArrayList<>();
    }
    return cognitoGroups
        .stream().map(roleName -> "ROLE_" + roleName)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
