## Задание
Напишите приложение, которое по REST принимает запрос вида
POST api/v1/wallet
{
valletId: UUID,
operationType: DEPOSIT or WITHDRAW,
amount: 1000
}
после выполнять логику по изменению счета в базе данных
также есть возможность получить баланс кошелька
GET api/v1/wallets/{WALLET_UUID}
стек:
java 8-17
Spring 3
Postgresql
Должны быть написаны миграции для базы данных с помощью liquibase
Обратите особое внимание проблемам при работе в конкурентной среде (1000 RPS по
одному кошельку). Ни один запрос не должен быть не обработан (50Х error)
Предусмотрите соблюдение формата ответа для заведомо неверных запросов, когда
кошелька не существует, не валидный json, или недостаточно средств.
приложение должно запускаться в докер контейнере, база данных тоже, вся система
должна подниматься с помощью docker-compose
предусмотрите возможность настраивать различные параметры как на стороне
приложения так и базы данных без пересборки контейнеров.
эндпоинты должны быть покрыты тестами.
## Запросы
POST запрос /api/v1/wallets, чтобы добавить новый кошелек в базу данных (UUID кошелька будет сгенерированно случайно, amount будет равен 0).
DELETE запрос /api/v1/wallets/{id} удаляет кошелек с заданным UUID.
GET запрос /api/v1/wallets/{id} возвращает UUID и amount кошелька в формате JSON.
POST запрос /api/v1/wallets/operation увеличивает или уменьшает amount кошелька с заданным UUID, принимает JSON следующего вида:
{
valletId: UUID,
operationType: DEPOSIT or WITHDRAW,
amount: 1000
}
## Тестирование
Тестирование проводилось с использованием Postman. Изначально в базе данных 3 кошелька:
550e8400-e29b-41d4-a716-446655440000 1000
550e8400-e29b-41d4-a716-446655440001 2000
550e8400-e29b-41d4-a716-446655440002 3000
### Добавление нового кошелька
![image](https://github.com/user-attachments/assets/931521d2-92e7-47eb-8313-9fe72a3d3871)
### Удаление кошелька по UUID
![image](https://github.com/user-attachments/assets/54bb2eb1-f2a0-4bc2-83e7-a5945e4c8279)
### Попытка удаления кошелька с UUID, которого не существует
![image](https://github.com/user-attachments/assets/cbdedf1c-8616-4065-9d83-3b2489ece3f9)
### Просмотр состояния кошелька
![image](https://github.com/user-attachments/assets/d50911f8-c8f4-44ec-ad45-d95c9b9d9476)
### Операция DEPOSIT
![image](https://github.com/user-attachments/assets/dd5ab5ba-ba5b-41df-b795-70202c4ff017)
### Операция WITHDRAW
![image](https://github.com/user-attachments/assets/7c29c64d-78e4-41d6-b4eb-005a3b0cb3de)
### Операция WITHDRAW, если недостаточно средств
![image](https://github.com/user-attachments/assets/5db1fe8e-3823-4351-ac4f-a62cf7f7d423)
