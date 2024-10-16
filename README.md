# Сервис авторизации пользователей

## Описание

Сервис предназначен для валидации пользовательских токенов, для дальнейшего доступа к другим частям системы, а так же для генерации токенов для обращения пользователей к системе

## Взаимодействие с другими модулями

Взаимодействие происходит через RabbitMQ

Есть следующие слушатели очередей
* auth.rpc.tokens - на вход принимает ID пользователя, на выход дает пару токенов JWT (UserTokensData)
* auth.rpc.validate - на вход принимает токен JWT, на выход отдает данные пользователя (UserData)
* auth.users.add - добавление нового пользователя в систему, на вход получает UserData


## License

The MIT License (MIT)

Copyright (c) 2024 Dmitriy Zhuravlev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
