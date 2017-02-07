<#-- @ftlvariable name="contract" type="com.rbkmoney.damsel.domain.Contract" -->
h5. Операция: Создание контракта

<#if (contract.isSetContractor())!false>
    <#assign legalEntity = contract.contractor.entity.getRussianLegalEntity()>
h6. Банковские реквизиты
* Расчетный счет: ${(contract.contractor.bankAccount.account)!"-"}
* Наименование банка: ${(contract.contractor.bankAccount.bankName)!"-"}
* Корреспондентский счет: ${(contract.contractor.bankAccount.bankPostAccount)!"-"}
* БИК банка: ${(contract.contractor.bankAccount.bankBik)!"-"}

h6. Реквизиты юр. лица
* Идентификатор контракта:  ${contract.id}
* Наименование юр. лица:  ${(legalEntity.registeredName)!"-"}
* ОГРН:  ${(legalEntity.registeredNumber)!"-"}
* ИНН/КПП:  ${(legalEntity.inn)!"-"}
* Фактический адрес: ${(legalEntity.actualAddress)!"-"}
* Адрес для отправки корреспонденции (почтовый): ${(legalEntity.postAddress)!"-"}
* Должность ЕИО/представителя: ${(legalEntity.representativePosition)!"-"}
* ФИО ЕИО/представителя: ${(legalEntity.representativeFullName)!"-"}
* Документ ЕИО/представителя: ${(legalEntity.representativeDocument)!"-"}
</#if>
<#if (contract.isSetPayoutTools())!false>
    <#list contract.payoutTools as payoutTool>
    h6. Способ вывода стредств
    * Идентификатор: ${payoutTool.id}
    * Тип валюты: ${payoutTool.currency.symbolicCode}
    * Расчетный счет: ${(payoutTool.payoutToolInfo.bankAccount.account)!"-"}
    * Наименование банка: ${(payoutTool.payoutToolInfo.bankAccount.bankName)!"-"}
    * Корреспондентский счет: ${(payoutTool.payoutToolInfo.bankAccount.bankPostAccount)!"-"}
    * БИК банка: ${(payoutTool.payoutToolInfo.bankAccount.bankBik)!"-"}
    </#list>
</#if>
<#if (contract.isSetLegalAgreement())!false>
h6. Юридическое соглашение
* Подписанно: ${contract.legalAgreement.signedAt}
* Идентификатор: ${contract.legalAgreement.legalAgreementId}
</#if>
* Контракт действует с ${(contract.validSince)!"-"} по ${(contract.validUntil)!"-"}
