/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.api.utils;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;

/**
 * Helper service for automatically generating numbers for entities
 * 
 * @since 0.4.0
 */
@Service
public class NumberGeneratorService {

    public static final int DEFAULT_NUM_OF_DIGITS = 6;

    public static final String DEFAULT_NUMBER_FIELD_NAME = "number";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberGeneratorModelHelper numberGeneratorModelHelper;

    /**
     * Generates and inserts new number to entity's form
     * 
     * @param state
     *            main view state definition
     * @param plugin
     *            plugin identifier of entity
     * @param entityName
     *            name of entity
     * @param formFieldReferenceName
     *            reference name of form
     * @param numberFieldReferenceName
     *            reference name of field into which generated number should be inserted
     */
    public void generateAndInsertNumber(final ViewDefinitionState state, final String plugin, final String entityName,
            final String formFieldReferenceName, final String numberFieldReferenceName) {
        if (!checkIfShouldInsertNumber(state, formFieldReferenceName, numberFieldReferenceName)) {
            return;
        }
        FieldComponent number = (FieldComponent) state.getComponentByReference(numberFieldReferenceName);
        number.setFieldValue(generateNumber(plugin, entityName));
    }

    /**
     * Checks if new entity number should be generated and inserted
     * 
     * @param state
     *            main view state definition
     * @param formFieldReferenceName
     *            reference name of form
     * @param numberFieldReferenceName
     *            reference name of field into which generated number should be inserted
     * @return true if new entity number should be generated and inserted
     */
    public boolean checkIfShouldInsertNumber(final ViewDefinitionState state, final String formFieldReferenceName,
            final String numberFieldReferenceName) {
        FormComponent form = (FormComponent) state.getComponentByReference(formFieldReferenceName);
        FieldComponent number = (FieldComponent) state.getComponentByReference(numberFieldReferenceName);
        if (form.getEntityId() != null) {
            // form is already saved
            return false;
        }
        if (StringUtils.isNotBlank((String) number.getFieldValue())) {
            // number is already chosen
            return false;
        }
        if (number.isHasError()) {
            // there is a validation message for that field
            return false;
        }
        return true;
    }

    /**
     * Generate new 6-digits number of entity
     * 
     * @param pluginIdentifier
     *            plugin identifier of entity
     * @param modelName
     *            name of entity
     * @return new number of entity
     */
    // TODO MAKU move this responsibility to the qcadoo-model
    public String generateNumber(final String pluginIdentifier, final String modelName) {
        return generateNumber(pluginIdentifier, modelName, DEFAULT_NUM_OF_DIGITS);
    }

    /**
     * Generate new number of entity with specified digits number
     * 
     * @param pluginIdentifier
     *            plugin identifier of entity
     * @param modelName
     *            name of entity
     * @param numOfDigits
     *            number of digits of generated number
     * @return new number of entity
     */
    // TODO MAKU move this responsibility to the qcadoo-model
    public String generateNumber(final String pluginIdentifier, final String modelName, final int numOfDigits) {
        return generateNumberWithPrefix(pluginIdentifier, modelName, numOfDigits, null);
    }

    /**
     * Generate new number of entity with specified digits number
     * 
     * @param pluginIdentifier
     *            plugin identifier of entity
     * @param modelName
     *            name of entity
     * @param numOfDigits
     *            number of digits of generated number
     * @param prefix
     *            number prefix
     * @return new number of entity
     */
    // TODO MAKU move this responsibility to the qcadoo-model
    public String generateNumberWithPrefix(final String pluginIdentifier, final String modelName, final int numOfDigits,
            final String prefix) {
        String generatedNumber = generateNumberWithExtension(pluginIdentifier, modelName, numOfDigits, prefix, "", "");
        return prependPrefix(prefix, generatedNumber);
    }

    private String prependPrefix(final String prefix, final String generatedNumber) {
        if (prefix == null) {
            return generatedNumber;
        }
        return prefix + generatedNumber;
    }

    /**
     * Generate new number of entity with specified digits number
     *
     * @param pluginIdentifier
     *            plugin identifier of entity
     * @param modelName
     *            name of entity
     * @param numOfDigits
     *            number of digits of generated number
     * @param suffix
     *            number suffix
     * @return new number of entity
     */
    public String generateNumberWithSuffix(final String pluginIdentifier, final String modelName, final int numOfDigits,
                                           final String suffix, final String numberFieldName) {
        String generatedNumber = generateNumberWithExtension(pluginIdentifier, modelName, numOfDigits, "", suffix, numberFieldName);
        return appendSuffix(suffix, generatedNumber);
    }

    private String generateNumberWithExtension(String pluginIdentifier, String modelName, int numOfDigits, String prefix, String suffix, String numberFieldName) {
        Collection<Entity> numberProjections = numberGeneratorModelHelper.getNumbersProjection(pluginIdentifier, modelName,
                StringUtils.isEmpty(numberFieldName) ? DEFAULT_NUMBER_FIELD_NAME : numberFieldName, prefix, suffix);
        Collection<Long> numericValues = extractNumericValues(numberProjections);
        Long greatestNumber = 0L;
        if (!numericValues.isEmpty()) {
            greatestNumber = Ordering.natural().max(numericValues);
        }
        return String.format("%0" + numOfDigits + "d", greatestNumber + 1);
    }

    private String appendSuffix(final String suffix, final String generatedNumber) {
        if (suffix == null) {
            return generatedNumber;
        }
        return generatedNumber + suffix;
    }

    private Collection<Long> extractNumericValues(final Iterable<Entity> numberProjections) {
        List<Long> numericValues = Lists.newArrayList();
        for (Entity projection : numberProjections) {
            String numberFieldValue = projection.getStringField(NumberGeneratorModelHelper.NUM_PROJECTION_ALIAS);
            if (StringUtils.isNumeric(numberFieldValue)) {
                numericValues.add(Long.valueOf(numberFieldValue));
            }
        }
        return numericValues;
    }

}
