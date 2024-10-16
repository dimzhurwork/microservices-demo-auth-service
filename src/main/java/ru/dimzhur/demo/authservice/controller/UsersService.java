package ru.dimzhur.demo.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.dimzhur.demo.rmqdata.model.UserData;
import ru.dimzhur.demo.authservice.model.db.redis.RedisUser;
import ru.dimzhur.demo.authservice.repository.redis.RedisUsersRepository;

/**
 * Сервис для обновления пользователей, пока только добавление, но в будущем будет и удаление и изменение
 */
@RequiredArgsConstructor
@Service
public class UsersService {

    /**
     * Redis репозиторий данных о пользователе
     */
    private final RedisUsersRepository usersRepository;

    /**
     * Добавление нового пользователя
     * Срабатывает при получении данных из очереди auth.users
     * @param data данные пользователя
     */
    @RabbitListener(queues = "auth.users")
    public void addUser(UserData data) {
        if(data == null){
            return;
        }
        var userRoles = UserRole.fromStrings(data.getRoles());
        try {
            if (!usersRepository.existsById(data.getId())) {
                var user = RedisUser.builder().id(data.getId()).roles(userRoles).build();
                user = usersRepository.save(user);
                System.out.println("Add user to db " + user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
