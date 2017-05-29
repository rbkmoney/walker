<#-- @ftlvariable name="termination" type="com.rbkmoney.damsel.payment_processing.ContractTermination" -->
<#-- @ftlvariable name="contract_modification_unit" type="com.rbkmoney.damsel.payment_processing.ContractModificationUnit" -->
h5. Операция: Изменение контракта
<#if (contract_modification_unit.getModification().isSetTermination())!false>
    <#assign termintaion = contract_modification_unit.getModification().getTermination()>
* Расторжение контракта
* Идентификатор контракта:  ${contract_modification_unit.id}
* Причина расторжения:  ${(termination.reason)!"-"}
* Дата расторжения:  ${(termintaion.terminatedAt)!"-"}
</#if>
<#if (contract_modification_unit.getModification().isSetAdjustmentCreation())!false>
* Поправки к договору
* Идентификатор контракта:  ${contract_modification_unit.id}
* Идентификатор: ${contract_modification_unit.modification.adjustmentCreation.id}
* Действует с: ${(contract_modification_unit.modification.adjustmentCreation.validSince)!"-"}
* Действует до: ${(contract_modification_unit.modification.adjustmentCreation.validUntil)!"-"}
</#if>
<#if (contract_modification_unit.getModification().isSetPayoutToolCreation())!false>
h6. Способ вывода средств
    <#assign payoutTool = contract_modification_unit.modification.getPayoutToolCreation()>
* Id способа выплаты: ${payoutTool.id}
* Идентификатор: ${payoutTool.id}
* Тип валюты: ${payoutTool.currency.symbolicCode}
* Расчетный счет: ${(payoutTool.payoutToolInfo.bankAccount.account)!"-"}
* Наименование банка: ${(payoutTool.payoutToolInfo.bankAccount.bankName)!"-"}
* Корреспондентский счет: ${(payoutTool.payoutToolInfo.bankAccount.bankPostAccount)!"-"}
* БИК банка: ${(payoutTool.payoutToolInfo.bankAccount.bankBik)!"-"}
</#if>
<#if (contract_modification_unit.getModification().isSetLegalAgreementBinding())!false>
h6. Юридическое соглашение
    <#assign legalAgreementBinding = contract_modification_unit.modification.getLegalAgreementBinding()>
* Дата заключения: ${legalAgreementBinding.signedAt}
* Идентификатор договора: ${legalAgreementBinding.legalAgreementId}
</#if>