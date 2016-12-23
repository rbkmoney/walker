<#-- @ftlvariable name="modification" type="com.rbkmoney.damsel.payment_processing.ShopModification" -->
h5. Операция: Редактирование магазина
* Идентификатор магазина:  ${shop_id}
<#----->
<#if modification_type == "blocking">
    <#if modification.getBlocking().isSetBlocked()>
        <#assign blk = modification.getBlocking().getBlocked()>
    * Заблокирован магазин по причине : ${(blk.reason)!"-"}
    <#else>
        <#assign ublk = modification.getBlocking().getUnblocked()>
    * Разблокирован магазин по причине : ${(ublk.reason)!"-"}
    </#if>
</#if>
<#----->
<#if modification_type == "suspension">
    <#if (modification.suspension.isSetSuspended())!false >
    * Приастановлен магазин
    <#else>
    * Приастановленный магазин активирован
    </#if>
</#if>
<#----->
<#if modification_type == "update">
<#--по какойто причине ломается на геттере '.update'-->
    <#assign shop_update = modification.getUpdate()>
* Изменен магазин : ${(shop_update.details.name)!"-"}
* Описание : ${(shop_update.details.description)!"-"}
* Местоположение : ${(shop_update.details.location.fieldValue)!"-"}
* Категория : ${(shop_update.category.id)!"-"}
</#if>
<#----->
<#if modification_type == "accounts_created">
* Созданы счета:
В валюте: ${modification.accountsCreated.accounts.currency.symbolicCode}
Освновной счет: ${modification.accountsCreated.accounts.general}
Гарантийный счет: ${modification.accountsCreated.accounts.guarantee}
</#if>
