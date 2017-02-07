<#-- @ftlvariable name="contract" type="com.rbkmoney.damsel.domain.Contract" -->
h5. Операция: Создание контракта
* Идентификатор контракта:  ${contract.id}
<#if (contract.isSetContractor())!false>
    <#assign legalEntity = contract.contractor.entity.getRussianLegalEntity()>
* Имя контрактора:  ${(legalEntity.registeredName)!"-"}
* ОГРН:  ${(legalEntity.registeredNumber)!"-"}
* ИНН/КПП:  ${(legalEntity.inn)!"-"}
* Адрес места нахождения: ${(legalEntity.actualAddress)!"-"}
* Адрес для отправки корреспонденции (почтовый): ${(legalEntity.postAddress)!"-"}
* Наименование должности ЕИО/представителя: ${(legalEntity.representativePosition)!"-"}
* ФИО ЕИО/представителя: ${(legalEntity.representativeFullName)!"-"}
* Наименование документа, на основании которого действует ЕИО/представитель: ${(legalEntity.representativeDocument)!"-"}
* Банковский аккаунт: ${(contract.contractor.bankAccount.account)!"-"}
</#if>