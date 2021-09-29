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
package com.qcadoo.view.internal.ribbon.templates.model;

import java.util.LinkedList;
import java.util.List;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.api.ribbon.RibbonComboItem;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.ribbon.RibbonUtils;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonActionItem;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;
import com.qcadoo.view.internal.ribbon.model.RibbonGroupImpl;
import com.qcadoo.view.internal.ribbon.templates.RibbonTemplateParameters;

public class TemplateRibbonGroup {

    private final String name;

    private final String condition;

    private final String pluginIdentifier;

    private final List<InternalRibbonActionItem> items = new LinkedList<InternalRibbonActionItem>();

    public TemplateRibbonGroup(final String name, final String pluginIdentifier, final String condition) {
        this.name = name;
        this.pluginIdentifier = pluginIdentifier;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void addActionItem(final InternalRibbonActionItem item) {
        items.add(item);
    }

    public InternalRibbonGroup getRibbonGroup(final RibbonTemplateParameters parameters, final ViewDefinition viewDefinition) {
        if (!PluginUtils.isEnabled(pluginIdentifier)) {
            return null;
        }
        List<InternalRibbonActionItem> itemsToApply = getFilteredList(parameters);
        if (itemsToApply.isEmpty()) {
            return null;
        }
        if (!checkCondition(condition, viewDefinition)) {
            return null;
        }
        InternalRibbonGroup group = new RibbonGroupImpl(name);
        for (InternalRibbonActionItem item : itemsToApply) {
            InternalRibbonActionItem itemCopy = item.getCopy();
            translateRibbonAction(itemCopy, viewDefinition);
            group.addItem(itemCopy);
        }
        return group;
    }

    private boolean checkCondition(final String condition, final ViewDefinition viewDefinition) {
        if (condition == null) {
            return true;
        }

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(condition);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("view", viewDefinition);
        return exp.getValue(context, Boolean.class);
    }

    private void translateRibbonAction(final RibbonActionItem item, final ViewDefinition viewDefinition) {
        InternalRibbonActionItem internalItem = (InternalRibbonActionItem) item;
        String translatedAction = RibbonUtils.translateRibbonAction(internalItem.getAction(), viewDefinition);
        internalItem.setAction(translatedAction);

        if (internalItem instanceof RibbonComboItem) {
            RibbonComboItem comboItem = (RibbonComboItem) internalItem;
            for (RibbonActionItem comboItemElement : comboItem.getItems()) {
                translateRibbonAction(comboItemElement, viewDefinition);
            }
        }
    }

    private List<InternalRibbonActionItem> getFilteredList(final RibbonTemplateParameters parameters) {
        List<InternalRibbonActionItem> filteredList = new LinkedList<InternalRibbonActionItem>();
        if (parameters.getExcludeItems() != null) {
            for (InternalRibbonActionItem item : items) {
                if (!parameters.getExcludeItems().contains(name + "." + item.getName())) {
                    filteredList.add(item);
                }
            }
            return filteredList;
        }
        if (parameters.getIncludeItems() != null) {
            for (InternalRibbonActionItem item : items) {
                if (parameters.getIncludeItems().contains(name + "." + item.getName())) {
                    filteredList.add(item);
                }
            }
            return filteredList;
        }
        return items;
    }

    public boolean containItem(final String itemName) {
        for (InternalRibbonActionItem item : items) {
            if (itemName.equals(name + "." + item.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TemplateRibbonGroup)) {
            return false;
        }
        TemplateRibbonGroup other = (TemplateRibbonGroup) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TemplateRibbonGroup [name=" + name + "]";
    }

}
