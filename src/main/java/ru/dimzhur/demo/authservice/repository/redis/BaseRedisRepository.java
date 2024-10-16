package ru.dimzhur.demo.authservice.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Базовый репозиторий Redis для наследования всеми остальными
 * @param <I> модель данных
 * @param <ID> ID данных
 */
@NoRepositoryBean
public interface BaseRedisRepository<I, ID> extends CrudRepository<I, ID> {
}
