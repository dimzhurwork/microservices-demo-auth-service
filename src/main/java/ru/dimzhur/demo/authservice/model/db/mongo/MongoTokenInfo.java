package ru.dimzhur.demo.authservice.model.db.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Данные пользовательского токена для хранения в Mongo
 */
@Data
@Document(collection = "TokenInfos")
public class MongoTokenInfo extends BaseDocument {

    /**
     * ID токена
     */
    @Id
    String id;
    /**
     * ID пользователя
     */
    String userId;
    /**
     * Флаг отклонения
     */
    boolean reject;
    /**
     * Дата завершения действия
     */
    Date expiresAt;
}
