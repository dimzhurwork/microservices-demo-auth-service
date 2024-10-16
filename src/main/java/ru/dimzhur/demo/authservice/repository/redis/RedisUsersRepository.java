package ru.dimzhur.demo.authservice.repository.redis;

import org.springframework.stereotype.Repository;
import ru.dimzhur.demo.authservice.model.db.redis.RedisUser;

@Repository
public interface RedisUsersRepository extends BaseRedisRepository<RedisUser, String> {
}
