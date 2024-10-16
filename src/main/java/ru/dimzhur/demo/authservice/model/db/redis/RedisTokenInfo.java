package ru.dimzhur.demo.authservice.model.db.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

/**
 * Данные пользовательского токена для хранения в Redis
 */
@Data
@RedisHash("TokenInfos")
public class RedisTokenInfo extends BaseHash {

    /**
     * ID токена
     */
    @Id
    String id;
    /**
     * ID пользователя
     */
    String userId;
    /**
     * Флаг отклонения
     */
    boolean reject;
    /**
     * Дата завершения действия
     */
    Date expiresAt;
}
