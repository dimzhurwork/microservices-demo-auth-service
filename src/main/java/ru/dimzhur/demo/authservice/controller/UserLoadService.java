package ru.dimzhur.demo.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.dimzhur.demo.rmqdata.model.UserData;
import ru.dimzhur.demo.authservice.model.db.redis.RedisUser;
import ru.dimzhur.demo.authservice.repository.redis.RedisUsersRepository;

/**
 * Сервис для получения пользователя
 */
@RequiredArgsConstructor
@Service
public class UserLoadService {

    /**
     * Redis репозиторий пользователей
     */
    private final RedisUsersRepository usersRepository;

    /**
     * Шаблон для обращения к RabbitMQ
     */
    private final RabbitTemplate rabbitTemplate;


    /**
     * Получение пользователя
     * Если пользователь есть в локальной базе - возьмет из нее, если нет - то попробует получить из сервиса пользователей
     * @param id ID пользователя
     * @return модель пользователя для хранения в Redis
     */
    public RedisUser getUser(String id) {
        RedisUser user = usersRepository.findById(id).orElse(null);
        if (user == null) {
            var newUser =getUserRemote(id);
            if (newUser != null) {
                user = usersRepository.save(
                        RedisUser.builder()
                                .id(newUser.getId())
                                .roles(newUser.getRoles())
                                .build()
                );
            } else {
                return null;
            }
        }
        return user;
    }

    /**
     * Получение пользователя из сервиса пользователей
     *
     * @param id ID пользователя
     * @return данные пользователя
     */
    private UserData getUserRemote(String id) {
        return (UserData) rabbitTemplate.convertSendAndReceive("users.rpc.user", id);
    }


}
