package ru.dimzhur.demo.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.dimzhur.demo.authservice.model.db.redis.RedisTokenInfo;
import ru.dimzhur.demo.authservice.repository.redis.RedisTokenInfoRepository;
import ru.dimzhur.demo.rmqdata.model.UserTokensData;
import ru.dimzhur.demo.authservice.model.db.mongo.MongoTokenInfo;
import ru.dimzhur.demo.authservice.model.db.redis.RedisUser;
import ru.dimzhur.demo.authservice.repository.mongo.MongoTokenInfosRepository;

import java.util.Date;

/**
 * Сервис для создания токенов доступа пользователей
 */
@Service
@RequiredArgsConstructor
public class TokenCreateService {

    /**
     * Сервис для генерации JWT
     */
    private final JwtService jwtService;
    /**
     * Сервис получения информации о пользователях
     */
    private final UserLoadService userLoadService;
    /**
     * Репозиторий информации о токенов для долговременного их хранения
     */
    private final MongoTokenInfosRepository mongoTokenRepository;
    /**
     * Репозиторий Redis для быстрого доступа к информации о токенах
     */
    private final RedisTokenInfoRepository redisTokenInfoRepository;

    /**
     * Генерация новой пары токенов для доступа пользователя к системе
     * @param userId ID пользователя
     * @return новая пара токенов
     */
    @RabbitListener(queues = "auth.rpc.tokens", messageConverter = "messageConverter")
    public UserTokensData createToken(String userId) {
        RedisUser user = userLoadService.getUser(userId);
        if (user == null) {
            return null;
        }
        var creationTime = System.currentTimeMillis();
        var mongoToken = createNewToken(user.getId(), creationTime);
        var data = new UserTokensData();
        try {
            String access = jwtService.generateToken(user, false, mongoToken.getId(), creationTime);
            String refresh = jwtService.generateToken(user, true, mongoToken.getId(), creationTime);
            data.setAccess(access);
            data.setRefresh(refresh);
            saveRedisToken(mongoToken);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            mongoTokenRepository.delete(mongoToken);
            return null;
        }
        return data;
    }

    /**
     * Создание токена
     * @param userId ID пользователя
     * @param creationTime время генерации
     * @return созданный токен доступа
     */
    private MongoTokenInfo createNewToken(String userId, long creationTime) {
        MongoTokenInfo mongoTokenInfo = new MongoTokenInfo();
        mongoTokenInfo.setUserId(userId);
        mongoTokenInfo.setExpiresAt(new Date(creationTime + JwtService.ACCESS_LIVE));
        return mongoTokenRepository.save(mongoTokenInfo);
    }

    /**
     * Сохранение информации о токене в Redis
     * @param mongoTokenInfo информация о токене
     */
    private void saveRedisToken(MongoTokenInfo mongoTokenInfo) {
        var token = new RedisTokenInfo();
        token.setId(mongoTokenInfo.getId());
        token.setExpiresAt(mongoTokenInfo.getExpiresAt());
        token.setReject(mongoTokenInfo.isReject());
        token.setUserId(mongoTokenInfo.getUserId());
        redisTokenInfoRepository.save(token);
    }
}
