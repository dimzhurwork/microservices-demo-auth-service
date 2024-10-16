package ru.dimzhur.demo.authservice.repository.mongo;

import ru.dimzhur.demo.authservice.model.db.mongo.MongoTokenInfo;

import java.util.Date;

/**
 * Репозиторий для долговременного хранения информации о токенах
 */
public interface MongoTokenInfosRepository extends BaseMongoRepository<MongoTokenInfo, String> {

    /**
     * Удаление всех просроченных токенов
     * @param date дата, до которой необходимо удалить токены
     */
    void deleteByExpiresAtBefore(Date date);

}
