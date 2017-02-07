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

import static com.rbkmoney.walker.service.DescriptionBuilder.TemplateName.*;

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
        templates.put(CONTRACT_CREATION, cfg.getTemplate("contract_creation.ftl"));
        templates.put(CONTRACT_MODIFICATION, cfg.getTemplate("contract_modification.ftl"));
    }

    public String buildDescription(Claim claim) {
        String description = "";
        try {
            for (PartyModification modification : claim.getChangeset()) {
                if (modification.isSetShopCreation()) {
                    description += renderDescription(
                            SHOP_CREATION, "shop", modification.getShopCreation());
                } else if (modification.isSetShopModification()) {
                    description += renderDescription(
                            SHOP_MODIFICATION, "modification_unit", modification.getShopModification());
                } else if (modification.isSetContractCreation()) {
                    description += renderDescription(
                            CONTRACT_CREATION, "contract", modification.getContractCreation());
                } else if (modification.isSetContractModification()) {
                    description += renderDescription(
                            CONTRACT_MODIFICATION, "contract_modification_unit", modification.getContractModification());
                } else {
                    description += modification.getFieldValue().toString();
                }
                description += "\n ";
            }
        } catch (NullPointerException | TemplateException | IOException e) {
            description += "\n Cant build correct Description. " + claim.toString()
                    + " Error: " + e.getMessage();
            log.error("Cant build correct description: {} ", claim.toString(), e);
        }
        return description;
    }

    private String renderDescription(TemplateName templateName, String paramName, Object obj) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        root.put(paramName, obj);
        StringWriter out = new StringWriter();
        templates.get(templateName).process(root, out);
        return out.toString();
    }

    enum TemplateName {
        SHOP_CREATION,
        SHOP_MODIFICATION,
        CONTRACT_CREATION,
        CONTRACT_MODIFICATION
    }

}
