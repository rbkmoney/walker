<#-- @ftlvariable name="shop" type="com.rbkmoney.damsel.domain.Shop" -->
h5. Операция: Создание магазина
* Id : ${(shop.id)!"-"}
* Название : ${(shop.details.name)!"-"}
* Описание: ${(shop.details.description)!"-"}
* URL: ${(shop.details.location.fieldValue)!"-"}
* Id Категории: ${(shop.category.id)!"-"}
* Id контракта: ${(shop.contractId)!"-"}