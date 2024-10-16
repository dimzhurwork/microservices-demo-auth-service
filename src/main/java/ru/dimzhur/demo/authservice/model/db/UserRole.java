package ru.dimzhur.demo.authservice.model.db;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * Модель роли пользователя
 */
@RequiredArgsConstructor
public class UserRole implements GrantedAuthority {

    /**
     * Название роли
     */
    final String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
