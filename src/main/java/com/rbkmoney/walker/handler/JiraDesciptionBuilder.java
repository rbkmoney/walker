package com.rbkmoney.walker.handler;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @since 21.12.16
 **/
public class JiraDesciptionBuilder {

    static Logger log = LoggerFactory.getLogger(JiraDesciptionBuilder.class);

    /**
     * 1: domain.Blocking blocking
     * 2: domain.Suspension suspension
     * 3: domain.Contract contract_creation
     * 4: ContractModificationUnit contract_modification
     * 5: domain.Shop shop_creation
     * 6: ShopModificationUnit shop_modification
     * 7: domain.PayoutAccount payout_account_creation
     *
     * @param claim
     * @return
     */
    public static String buildDescription(Claim claim) {
        String description = "";
        try {
            for (PartyModification modification : claim.getChangeset()) {
                if (modification.isSetShopCreation()) {
                    buildShopCreation(description, modification.getShopCreation());
                } else if (modification.isSetShopModification()) {
                    buildShopModification(description, modification.getShopModification().getModification());
                } else if (modification.isSetContractCreation()) {
                    buildContractCreation(description, modification.getContractCreation());
                } else if (modification.isSetContractModification()) {
                    buildContractModification(description, modification.getContractModification());
                } else {
                    description += "\n " + modification.getFieldValue().toString();
                }
            }
        } catch (NullPointerException e) {
            log.error("Cant build correct description: {} ", claim.toString(), e);
        }
        return description;
    }


    private static String buildContractCreation(String description, Contract contract) {
        description += "\n \n h5. Операция: Создание контракта ";
        description += "\n * Идентификатор контракта: " + contract.getId();
        description += "\n * Банковский аккаунт: " + contract.getContractor().getBankAccount().getAccount();
        return description;
    }

    private static String buildContractModification(String description, ContractModificationUnit contractModificationUnit) {
        description += "\n \n h5. Операция: Редактирование контракта ";
        description += "\n * Идентификатор контракта: " + contractModificationUnit.getId();
        description += "\n * Банковский аккаунт: " + contractModificationUnit.getModification().getAdjustmentCreation().getId();
        return description;
    }

    private static String buildShopCreation(String description, Shop shop) {
        description += "\n \n h5. Операция: Создание магазина ";
        description += "\n * Название: " + shop.getDetails().getName();
        description += "\n * Описание: " + shop.getDetails().getDescription();
        description += "\n * Местоположение: " + shop.getDetails().getLocation();
        description += "\n * Категория: " + shop.getCategory().getId();
        if (shop.isSetContractId()) {
            description += "\n Номер контракта: " + shop.getContractId();
//                        description += "\n ID контрактора: " + modification.getShopCreation().getContract().getSystemContractor().getId();
//                        description += "\n Контракт заключен : " + modification.getShopCreation().getContract().getConcludedAt();
//                        description += "\n Действует с : " + modification.getShopCreation().getContract().getValidSince();
//                        description += "\n Действует до : " + modification.getShopCreation().getContract().getValidUntil();
//                        description += "\n Разорван : " + modification.getShopCreation().getContract().getTerminatedAt();
//                    }
        }
        return description;
    }

    private static String buildShopModification(String description, ShopModification shopModification) {
        description += "\n \n h5. Операция: Редактирование магазина ";
        if (shopModification.isSetAccountsCreated()) {
            ShopAccountSet accounts = shopModification.getAccountsCreated().getAccounts();
            description += "\n * Созданы счета:";
            description += "\n в валюте: " + accounts.getCurrency().getSymbolicCode();
            description += "\n освновной счет: " + accounts.getGeneral();
            description += "\n гарантийный счет: " + accounts.getGuarantee();
        } else if (shopModification.isSetUpdate()) {
            ShopUpdate update = shopModification.getUpdate();
            description += "\n Изменен магазин : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getName).orElse("-");
            description += "\n Описание : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getDescription).orElse("-");
            description += "\n Местоположение : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getLocation).map(ShopLocation::getUrl).orElse("-");
            description += "\n Категория : " + Optional.ofNullable(update.getCategory()).map(CategoryRef::getId).orElse(0);
        } else {
            description += "\n " + shopModification.getFieldValue().toString();
        }
        return description;
    }
}
