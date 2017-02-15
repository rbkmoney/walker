package com.rbkmoney.walker.service;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.joda.time.DateTime;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.rbkmoney.walker.service.DescriptionBuilder.TemplateName.*;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * @since 21.12.16
 **/
@Service
public class DescriptionBuilder {

    static Logger log = LoggerFactory.getLogger(DescriptionBuilder.class);

    private Configuration cfg;

    private LinkedHashMap<TemplateName, Template> templates;

    @Autowired
    EnrichmentService enrichmentService;

    public DescriptionBuilder(Configuration configuration) throws IOException {
        this.cfg = configuration;
        templates = new LinkedHashMap<>();
        templates.put(SHOP_CREATION, cfg.getTemplate("shop_creation.ftl"));
        templates.put(SHOP_MODIFICATION, cfg.getTemplate("shop_modification.ftl"));
        templates.put(CONTRACT_CREATION, cfg.getTemplate("contract_creation.ftl"));
        templates.put(CONTRACT_MODIFICATION, cfg.getTemplate("contract_modification.ftl"));
    }

    public String buildDescription(Claim claim) {

        try {
            enrichmentService.getPartyEmail("1");
        } catch (TException e) {
            e.printStackTrace();
        }

        String description = "";
        try {
            for (PartyModification modification : claim.getChangeset()) {
                if (modification.isSetShopCreation()) {
                    description += renderShopCreation(modification.getShopCreation());
                } else if (modification.isSetShopModification()) {
                    description += renderDescription(
                            SHOP_MODIFICATION, "modification_unit", modification.getShopModification());
                } else if (modification.isSetContractCreation()) {
                    description += renderContractCreation(modification.getContractCreation());
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

    private String renderShopCreation(Shop shop) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        root.put("shop", shop);
        //TODO: get category name
        return renderFromTemplate(SHOP_CREATION, root);
    }

    private String renderContractCreation(Contract contract) throws IOException, TemplateException {
        Map<String, Object> fields = new HashMap<>();
        fields.put("contract", contract);
        fields.put("contract_valid_since", toPrettyDate(contract.getValidSince()));
        fields.put("contract_valid_until", toPrettyDate(contract.getValidUntil()));
        return renderFromTemplate(CONTRACT_CREATION, fields);
    }

    private String renderDescription(TemplateName templateName, String paramName, Object obj) throws IOException, TemplateException {
        Map<String, Object> fields = new HashMap<>();
        fields.put(paramName, obj);
        return renderFromTemplate(templateName, fields);
    }

    private String renderFromTemplate(TemplateName templateName, Map<String, Object> fields) throws IOException, TemplateException {
        StringWriter out = new StringWriter();
        templates.get(templateName).process(fields, out);
        return out.toString();
    }

    enum TemplateName {
        SHOP_CREATION,
        SHOP_MODIFICATION,
        CONTRACT_CREATION,
        CONTRACT_MODIFICATION
    }

    public static String toPrettyDate(String time) {
        try {
            Instant instant = Instant.from(ISO_INSTANT.parse(time));
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return formatter.format(localDateTime);
        } catch (DateTimeParseException | NullPointerException e) {
            return time;
        }
        return "-";
    }

}
