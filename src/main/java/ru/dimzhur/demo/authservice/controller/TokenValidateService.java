package ru.dimzhur.demo.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.dimzhur.demo.authservice.model.db.redis.RedisTokenInfo;
import ru.dimzhur.demo.authservice.repository.redis.RedisTokenInfoRepository;
import ru.dimzhur.demo.rmqdata.model.UserData;
import ru.dimzhur.demo.authservice.repository.mongo.MongoTokenInfosRepository;

import java.util.Date;

/**
 * Сервис для проверки токена пользователя
 */
@Service
@RequiredArgsConstructor
public class TokenValidateService {

    /**
     * Сервис для получения пользователей
     */
    private final UserLoadService userLoadService;

    /**
     * Redis репозиторий информации о выданных токенах
     */
    private final RedisTokenInfoRepository redisTokenRepository;

    /**
     * Mongo репозиторий информации о выданных токенах
     */
    private final MongoTokenInfosRepository mongoTokenRepository;

    /**
     * Сервис для обработки токенов JWT
     */
    private final JwtService jwtService;

    /**
     * Проверка токена на корректность и отправка данных о пользователе из него
     *
     * @param token токен пользователя
     * @return данные о пользователе
     */
    @RabbitListener(queues = "auth.rpc.validate", messageConverter = "messageConverter")
    public UserData validateToken(String token) {
        System.out.println("VALIDATE TOKEN=" + token);
        if (token == null || token.isEmpty()) {
            return null;
        }
        String tokenId;
        try {
            tokenId = jwtService.getTokenId(token);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
        var tokenInfo = getTokenInfo(tokenId);
        if (tokenInfo == null) {
            System.out.println("TOKEN NOT FOUND");
            return null;
        }

        if (tokenInfo.isReject()) {
            System.out.println("REJECT TOKEN");
            return null;
        }

        if (tokenInfo.getExpiresAt().before(new Date())) {
            System.out.println("EXPIRED TOKEN " + tokenInfo.getExpiresAt() + " NEW DATE=" + new Date());
            return null;
        }

        var user = userLoadService.getUser(tokenInfo.getUserId());


        if (user == null) {
            System.out.println("USER NOT FOUND");
            return null;
        }

        if (!jwtService.isTokenValid(token, user)) {
            System.out.println("INVALID TOKEN");
            return null;
        }

        var userData = new UserData();
        userData.setId(user.getId());
        userData.setRoles(user.getRolesNames());
        return userData;
    }

    /**
     * Получение информации о токене из базы данных
     *
     * @param tokenId ID токена
     * @return Redis модель данных информации о токене
     */
    private RedisTokenInfo getTokenInfo(String tokenId) {
        var redisToken = redisTokenRepository.findById(tokenId).orElse(null);
        if (redisToken == null) {
            var mongoToken = mongoTokenRepository.findById(tokenId).orElse(null);
            if (mongoToken != null) {
                redisToken = new RedisTokenInfo();
                redisToken.setId(mongoToken.getId());
                redisToken.setUserId(mongoToken.getUserId());
                redisToken.setExpiresAt(mongoToken.getExpiresAt());
                redisToken.setReject(mongoToken.isReject());
                try {
                    return redisTokenRepository.save(redisToken);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    return redisToken;
                }
            } else {
                return null;
            }
        } else {
            return redisToken;
        }
    }
}
