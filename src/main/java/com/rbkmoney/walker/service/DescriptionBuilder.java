package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.rbkmoney.walker.service.DescriptionBuilder.TemplateName.SHOP_CREATION;
import static com.rbkmoney.walker.service.DescriptionBuilder.TemplateName.SHOP_MODIFICATION;

/**
 * @since 21.12.16
 **/
public class DescriptionBuilder {

    static Logger log = LoggerFactory.getLogger(DescriptionBuilder.class);

    private Configuration cfg;

    private LinkedHashMap<TemplateName, Template> templates;

    public DescriptionBuilder(Configuration configuration) throws IOException {
        this.cfg = configuration;
        templates = new LinkedHashMap<>();
        templates.put(SHOP_CREATION, cfg.getTemplate("shop_creation.ftl"));
        templates.put(SHOP_MODIFICATION, cfg.getTemplate("shop_modification.ftl"));
    }

    public String buildDescription(Claim claim) {
        String description = "";
        try {
            for (PartyModification modification : claim.getChangeset()) {
                if (modification.isSetShopCreation()) {
                    description += buildShopCreation(modification.getShopCreation());
                } else if (modification.isSetShopModification()) {
                    description += buildShopModification(modification.getShopModification());
                } else if (modification.isSetContractCreation()) {
                    buildContractCreation(description, modification.getContractCreation());
                } else if (modification.isSetContractModification()) {
                    buildContractModification(description, modification.getContractModification());
                } else {
                    description += "\n " + modification.getFieldValue().toString();
                }
            }
        } catch (NullPointerException | TemplateException | IOException e) {
            description += "\n Cant build correct Description. " + claim.toString()
                    + " Error: " + e.getMessage();
            log.error("Cant build correct description: {} ", claim.toString(), e);
        }
        return description;
    }


    private String buildContractCreation(String description, Contract contract) {
        description += "\n \n h5. Операция: Создание контракта ";
        description += "\n * Идентификатор контракта: " + contract.getId();
        description += "\n * Банковский аккаунт: " + contract.getContractor().getBankAccount().getAccount();
        return description;
    }

    private String buildContractModification(String description, ContractModificationUnit contractModificationUnit) {
        description += "\n \n h5. Операция: Редактирование контракта ";
        description += "\n * Идентификатор контракта: " + contractModificationUnit.getId();
        description += "\n * Банковский аккаунт: " + contractModificationUnit.getModification().getAdjustmentCreation().getId();
        return description;
    }

    private String buildShopCreation(Shop shop) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        root.put("shop", shop);
        StringWriter out = new StringWriter();
        templates.get(SHOP_CREATION).process(root, out);
        return out.toString();
    }

    private String buildShopModification(ShopModificationUnit shopModificationUnit) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        ShopModification modification = shopModificationUnit.getModification();
        root.put("shop_id", shopModificationUnit.getId());
        root.put("modification_type", modification.getSetField().getFieldName());
        root.put("modification", modification);
        StringWriter out = new StringWriter();
        templates.get(SHOP_MODIFICATION).process(root, out);
        return out.toString();
    }

    enum TemplateName {
        SHOP_CREATION,
        SHOP_MODIFICATION,
    }

}
