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
* Заключчен: ${contract_modification_unit.modification.adjustmentCreation.concludedAt}
</#if>
