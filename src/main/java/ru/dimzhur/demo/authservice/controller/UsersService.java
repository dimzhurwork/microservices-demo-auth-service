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
     * Срабатывает при получении данных из очереди auth.users.add
     *
     * @param data данные пользователя
     */
    @RabbitListener(queues = "auth.users.add")
    public void addUser(UserData data) {
        if (data == null) {
            return;
        }
        try {
            if (!usersRepository.existsById(data.getId())) {
                var user = RedisUser.builder()
                        .id(data.getId())
                        .roles(data.getRoles())
                        .deleted(data.isDeleted())
                        .blocked(data.isBlock())
                        .build();
                user = usersRepository.save(user);
                System.out.println("Add user to db " + user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Изменение пользователя
     * Срабатывает при получении данных из очереди auth.users.update
     *
     * @param data данные пользователя
     */
    @RabbitListener(queues = "auth.users.update")
    public void updateUser(UserData data) {
        if (data == null) {
            return;
        }
        try {
            var user = usersRepository.findById(data.getId()).orElse(null);
            if (user == null) {
                user = RedisUser.builder()
                        .id(data.getId())
                        .roles(data.getRoles())
                        .blocked(data.isBlock())
                        .deleted(data.isDeleted())
                        .build();

            } else {
                user.setRoles(data.getRoles());
                user.setDeleted(data.isDeleted());
                user.setBlocked(data.isBlock());
            }
            usersRepository.save(user);
            System.out.println("Update user in db " + user.getId());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
