package ru.dimzhur.demo.authservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import ru.dimzhur.demo.authservice.model.db.mongo.BaseDocument;
import ru.dimzhur.demo.authservice.model.db.redis.BaseHash;
import ru.dimzhur.demo.authservice.repository.mongo.BaseMongoRepository;
import ru.dimzhur.demo.authservice.repository.redis.BaseRedisRepository;

/**
 * Конфигурация для включения нескольких различных баз данных
 */
@Configuration
@EnableConfigurationProperties
@EnableMongoRepositories(basePackageClasses = {BaseDocument.class, BaseMongoRepository.class})
@EnableRedisRepositories(basePackageClasses = {BaseHash.class, BaseRedisRepository.class})
public class BasicConfig {
}
