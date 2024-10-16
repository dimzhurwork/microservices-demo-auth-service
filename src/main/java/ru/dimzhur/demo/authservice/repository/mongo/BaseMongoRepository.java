package ru.dimzhur.demo.authservice.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Базовый репозиторий Mongo для наследования всеми остальными
 * @param <T> модель данных
 * @param <ID> ID данных
 */
@NoRepositoryBean
public interface BaseMongoRepository<T, ID>  extends MongoRepository<T, ID> {
}
