package com.aston.assessment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum UserRoles {
    MODERATOR(Collections.emptySet()),
    EXTERNAL_EXAMINER(Collections.emptySet()),
    INTERNAL_MODERATOR(Collections.emptySet()),
    ASSESSMENT_LEAD(Collections.emptySet()),
    PROGRAMME_DIRECTOR(Collections.emptySet()),
    NOT_SPECIFIED(Collections.emptySet());

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;

    }
}
