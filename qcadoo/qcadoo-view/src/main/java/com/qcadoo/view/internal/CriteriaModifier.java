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
package com.qcadoo.view.internal;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Node;

import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;

/**
 * Search criteria modifier
 * 
 * @author marcinkubala
 * @since 1.2.0
 */
public class CriteriaModifier {

    private static final String NO_SUCH_METHOD_MSG = "Failed to find method '%s.%s', "
            + "please make sure that there is no typo, method returns %s and parameters' types are (%s) or (%s)";

    public static final String NODE_NAME = "criteriaModifier";

    private static final String CLASS_ATTRIBUTE = "class";

    private static final String METHOD_ATTRIBUTE = "method";

    private final CustomMethodHolder customMethodHolder;

    private final boolean sigleParameter;

    public CriteriaModifier(final Node holderNode, final ViewDefinitionParser parser, final ApplicationContext applicationContext) {
        String className = parser.getStringAttribute(holderNode, CLASS_ATTRIBUTE);
        String methodName = parser.getStringAttribute(holderNode, METHOD_ATTRIBUTE);
        Class<?>[] singleParamter = new Class[] { SearchCriteriaBuilder.class };
        Class<?>[] multibleParamters = new Class[] { SearchCriteriaBuilder.class, FilterValueHolder.class };

        if (CustomMethodHolder.methodExists(className, methodName, applicationContext, singleParamter)) {
            customMethodHolder = new CustomMethodHolder(holderNode, parser, applicationContext, Void.TYPE, singleParamter);
            sigleParameter = true;
        } else if (CustomMethodHolder.methodExists(className, methodName, applicationContext, multibleParamters)) {
            customMethodHolder = new CustomMethodHolder(holderNode, parser, applicationContext, Void.TYPE, multibleParamters);
            sigleParameter = false;
        } else {
            throw new IllegalStateException(String.format(NO_SUCH_METHOD_MSG, className, methodName, Void.TYPE,
                    Arrays.toString(singleParamter), Arrays.toString(multibleParamters)));
        }
    }

    public void modifyCriteria(final SearchCriteriaBuilder searchCriteriaBuilder,
            final FilterValueHolder criteriaModifierParameters) {
        if (sigleParameter) {
            customMethodHolder.invoke(searchCriteriaBuilder);
        } else {
            customMethodHolder.invoke(searchCriteriaBuilder, criteriaModifierParameters);
        }
    }

}
