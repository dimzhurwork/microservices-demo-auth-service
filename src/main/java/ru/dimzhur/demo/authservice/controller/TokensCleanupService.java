package ru.dimzhur.demo.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.dimzhur.demo.authservice.repository.redis.RedisTokenInfoRepository;
import ru.dimzhur.demo.authservice.repository.mongo.MongoTokenInfosRepository;

import java.util.Date;

/**
 * Сервис удаления просроченных токенов из базы данных
 */
@RequiredArgsConstructor
@Service
@EnableScheduling
public class TokensCleanupService {

    /**
     * Mongo репозиторий токенов
     */
    private final MongoTokenInfosRepository mongoTokenInfosRepository;

    /**
     * Redis репозиторий токенов
     */
    private final RedisTokenInfoRepository redisTokenInfoRepository;

    /**
     * Удаление старых токенов.
     * Проходит каждую минуту
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void cleanUp() {
        var now = new Date();
        try{
            mongoTokenInfosRepository.deleteByExpiresAtBefore(now);
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
        try{
            redisTokenInfoRepository.deleteByExpiresAtBefore(now);
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
    }
}
