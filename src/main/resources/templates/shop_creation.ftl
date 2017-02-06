<#-- @ftlvariable name="shop" type="com.rbkmoney.damsel.domain.Shop" -->
h5. Операция: Создание магазина
* Название : ${(shop.details.name)!"-"}
* Описание: ${(shop.details.description)!"-"}
* Местоположение: ${(shop.details.location.fieldValue)!"-"}
* Id Категории: ${(shop.category.id)!"-"}
