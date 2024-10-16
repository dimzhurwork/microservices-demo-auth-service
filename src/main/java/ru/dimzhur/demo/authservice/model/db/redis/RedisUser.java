package ru.dimzhur.demo.authservice.model.db.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.dimzhur.demo.authservice.model.db.UserRole;
import ru.dimzhur.demo.authservice.model.db.mongo.BaseDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Модель данных пользователя для хранения в Redis
 */
@Data
@Builder
@RedisHash("Users")
public class RedisUser extends BaseDocument implements UserDetails  {

    /**
     * ID пользователя
     */
    @Id
    private String id;
    /**
     * Список ролей
     */
    private List<String> roles;
    /**
     * Флаг блокировки
     */
    private boolean blocked;
    /**
     * Флаг удаления
     */
    private boolean deleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<UserRole> userRoles = new ArrayList<>();
        if(roles != null) {
            for (String role : roles) {
                userRoles.add(new UserRole(role));
            }
        }
        return userRoles;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !blocked && !deleted;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !blocked && !deleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !blocked && !deleted;
    }

    public List<String> getRolesNames(){
        var list = new ArrayList<String>();
        for(UserRole role : roles){
            list.add(role.name());
        }
        return list;
    }
}
