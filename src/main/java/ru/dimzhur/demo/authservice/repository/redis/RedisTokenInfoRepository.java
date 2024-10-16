package ru.dimzhur.demo.authservice.repository.redis;

import ru.dimzhur.demo.authservice.model.db.redis.RedisTokenInfo;

import java.util.Date;

/**
 * Репозиторий для быстрого доступа к информации о токенах
 */
public interface RedisTokenInfoRepository extends BaseRedisRepository<RedisTokenInfo, String> {

    /**
     * Удаление всех просроченных токенов
     * @param date дата, до которой необходимо удалить токены
     */
    void deleteByExpiresAtBefore(Date date);
}
