<#-- @ftlvariable name="modification" type="com.rbkmoney.damsel.payment_processing.ShopModification" -->
<#-- @ftlvariable name="modification_unit" type="com.rbkmoney.damsel.payment_processing.ShopModificationUnit" -->
h5. Операция: Редактирование магазина
* Идентификатор магазина:  ${modification_unit.id}
<#----->
<#assign modification = modification_unit.getModification() >
<#if modification.isSetBlocking()>
    <#if modification.getBlocking().isSetBlocked()>
        <#assign blk = modification.getBlocking().getBlocked()>
    * Заблокирован магазин по причине : ${(blk.reason)!"-"}
    <#else>
        <#assign ublk = modification.getBlocking().getUnblocked()>
    * Разблокирован магазин по причине : ${(ublk.reason)!"-"}
    </#if>
</#if>
<#----->
<#if modification.isSetSuspension()>
    <#if (modification.getSuspension().isSetSuspended())!false >
    * Приастановлен магазин
    <#else>
    * Приостановленный магазин активирован
    </#if>
</#if>
<#----->
<#if modification.isSetUpdate()>
<#--по какойто причине ломается на геттере '.update'-->
    <#assign shop_update = modification.getUpdate()>
* Изменен магазин : ${(shop_update.details.name)!"-"}
* Описание : ${(shop_update.details.description)!"-"}
* Местоположение : ${(shop_update.details.location.fieldValue)!"-"}
* Категория : ${(shop_update.category.id)!"-"}
</#if>
<#----->

<#if modification.isSetAccountCreated()>
* Созданы счета:
В валюте: ${modification.accountCreated.account.currency.symbolicCode}
Основной счет: ${modification.accountCreated.account.settlement}
Гарантийный счет: ${modification.accountCreated.account.guarantee}
</#if>
