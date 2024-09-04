package com.boardgo.domain.user.service.response;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.RoleType;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserInfoEntity userInfoEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) () -> RoleType.USER.getCode());

        return collection;
    }

    public Long getId() {
        return userInfoEntity.getId();
    }

    @Override
    public String getPassword() {
        return userInfoEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfoEntity.getId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
