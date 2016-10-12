# Walker
Сервис согласования заявок. 

# Участники
1. Merchant - заказчик , участник 
 * Создание заявки
 * Revoke заявки 
2. Employee - сотрудник компании 
 * Принятие заявки 
 * Отклонение заявки 
3. Security officer - сотрудник безопасности
 * Принятие заявки 
 * Отклонение заявки 
 
Жизненный цикл заявки:
 1. Заявка создается пользователем, попадает в HellGate
 2. event попадает в Bustermaze
 3. walker опрашивает Bustermaze создает задачу в Jira
 4. walker опрашивает Jira на обновление созданных заявок и отправляет событие в HellGate
 5. walker получает событие c подтверждением из HG->BM->Walker обновляет(закрывает) задачу.


# Последовательность действий пользователя 
 1. Create - создается пользователь привязанный к организации - не нужен accept от менеджмента.
 2. CreateShop - создается claim - создается магазин 
 3. UpdateShop -  создается claim - изменяется магазин

 
# Credentials
В джире создан аккаунт walker с email-ом:
email: walker@rbkmoney.com 
Пароль от Jira лежит в конфиге: jira.user.password
 
# Development 
1. Поднимаешь окружение через docker-compose в папке docker_compose
    * поднимает HG 
    * поднимает MG
    * поднимает Bustermaze
    * поднимает PostgreSQL базу для BM
    * поднимает контейнер с настроенной Jira, либо запускаешь контейнер скриптом       
2. Запускаешь Walker
3. Для генерации событий используется HellGateMethodsTest
4. Jira доступна по адресу localhost:2990/jira


