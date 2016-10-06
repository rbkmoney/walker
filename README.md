# Walker
Сервис согласования заявок. 
Заявка:
 1. Заявка создается пользователем, попадает в HellGate
 2. event попадает в Bustermaze
 3. walker опрашивает Bustermaze создает задачу в Jira
 4. walker опрашивает Jira на обновление созданных заявок и отправляет событие в HellGate
 5. walker получает событие c подтверждением из HG->BM->Walker обновляет(закрывает) задачу.

# Участники
1. merchant - заказчик , учстник 
 * Создание заявки
 * Revoke заявки 
2. employee - сотрудник компании 
 * Принятие заявки 
 * Отклонение заявки 
3. security officer - сотрудник безопасности
 * Принятие заявки 
 * Отклонение заявки 

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
2. поднимаешь Jira
3. запускаешь Walker

Для работы c HG можно использовать тесты



