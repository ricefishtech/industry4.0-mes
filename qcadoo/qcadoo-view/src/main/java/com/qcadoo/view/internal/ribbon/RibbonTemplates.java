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
package com.qcadoo.view.internal.ribbon;

import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonActionItem;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;
import com.qcadoo.view.internal.ribbon.model.RibbonActionItemImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonGroupImpl;

public class RibbonTemplates {

    private static final String ACTIONS = "actions";

    private static final String STATES = "states";

    private static final String GENERIC_EXPORT = "genericExport";

    public InternalRibbonGroup getGroupTemplate(final String templateName, final ViewDefinition viewDefinition,
            final SecurityRole role) {
        if ("navigation".equals(templateName)) {
            return createNavigationTemplate(viewDefinition, role);
        } else if ("gridNewAndRemoveAction".equals(templateName)) {
            return createGridNewAndRemoveActionsTemplate(viewDefinition, role);
        } else if ("gridNewCopyAndRemoveAction".equals(templateName)) {
            return createGridNewCopyAndRemoveActionsTemplate(viewDefinition, role);
        } else if ("gridNewAndCopyAction".equals(templateName)) {
            return createGridNewAndCopyActionsTemplate(viewDefinition, role);
        } else if ("gridNewAction".equals(templateName)) {
            return createGridNewActionTemplate(viewDefinition, role);
        } else if ("gridRemoveAction".equals(templateName)) {
            return createGridRemoveActionTemplate(viewDefinition, role);
        } else if ("gridActivateAndDeactivateAction".equals(templateName)) {
            return createGridActivateAndDeactivateActionsTemplate(viewDefinition, role);
        } else if ("gridGenericExportAction".equals(templateName)) {
            return createGridGenericExportActionsTemplate(viewDefinition, role);
        } else if ("formSaveCopyAndRemoveActions".equals(templateName)) {
            return createFormSaveCopyAndRemoveActionsTemplate(viewDefinition, role);
        } else if ("formSaveAndRemoveActions".equals(templateName)) {
            return createFormSaveAndRemoveActionsTemplate(viewDefinition, role);
        } else if ("formCopyAndSaveNewActions".equals(templateName)) {
            return createFormCopyAndSaveNewActionsTemplate(viewDefinition, role);
        } else if ("formSaveAndBackAndRemoveActions".equals(templateName)) {
            return createFormSaveAndBackAndRemoveActionsTemplate(viewDefinition, role);
        } else if ("formSaveAndCancelActions".equals(templateName)) {
            return createFormSaveAndCancelActionsTemplate(viewDefinition, role);
        } else if ("formSaveAction".equals(templateName)) {
            return createFormSaveActionTemplate(viewDefinition, role);
        } else if ("formActivateAndDeactivateAction".equals(templateName)) {
            return createFormActivateAndDeactivateActionsTemplate(viewDefinition, role);
        } else {
            throw new IllegalStateException("Unsupported ribbon template : " + templateName);
        }
    }

    private InternalRibbonGroup createNavigationTemplate(final ViewDefinition viewDefinition, final SecurityRole role) {
        InternalRibbonActionItem ribbonBackAction = new RibbonActionItemImpl();
        ribbonBackAction.setAction(RibbonUtils.translateRibbonAction("#{window}.performBack", viewDefinition));
        ribbonBackAction.setIcon("backIcon24.png");
        ribbonBackAction.setName("back");
        ribbonBackAction.setEnabled(true);
        ribbonBackAction.setType(RibbonActionItem.Type.BIG_BUTTON);

        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl("navigation", role);
        ribbonGroup.addItem(ribbonBackAction);

        return ribbonGroup;
    }

    private InternalRibbonGroup createGridNewAndRemoveActionsTemplate(final ViewDefinition viewDefinition, final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createGridNewAction(viewDefinition));
        ribbonGroup.addItem(createGridDeleteAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createGridRemoveActionTemplate(final ViewDefinition viewDefinition, final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createGridDeleteAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createGridNewAndCopyActionsTemplate(final ViewDefinition viewDefinition, final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createGridNewAction(viewDefinition));
        ribbonGroup.addItem(createGridCopyAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createGridNewActionTemplate(final ViewDefinition viewDefinition, final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createGridNewAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createGridNewCopyAndRemoveActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createGridNewAction(viewDefinition));
        ribbonGroup.addItem(createGridCopyAction(viewDefinition));
        ribbonGroup.addItem(createGridDeleteAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createGridActivateAndDeactivateActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(STATES, role);
        ribbonGroup.addItem(createGridActivateAction(viewDefinition));
        ribbonGroup.addItem(createGridDeactivateAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createGridGenericExportActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(GENERIC_EXPORT, role);
        ribbonGroup.addItem(createGridExportCsvAction(viewDefinition));
        ribbonGroup.addItem(createGridExportPdfAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonActionItem createGridDeleteAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonDeleteAction = new RibbonActionItemImpl();
        ribbonDeleteAction.setAction(RibbonUtils.translateRibbonAction("#{grid}.performDelete;", viewDefinition));
        ribbonDeleteAction.setIcon("deleteIcon16.png");
        ribbonDeleteAction.setName("delete");
        ribbonDeleteAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        ribbonDeleteAction.setEnabled(false);
        ribbonDeleteAction.setDefaultEnabled(false);
        ribbonDeleteAction.setScript("var listener = {onChange: function(selectedArray) {if (selectedArray.length == 0) {"
                + "this.disable();} else {this.enable();}}}; #{grid}.addOnChangeListener(listener);");
        return ribbonDeleteAction;
    }

    private InternalRibbonActionItem createGridCopyAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonCopyAction = new RibbonActionItemImpl();
        ribbonCopyAction.setAction(RibbonUtils.translateRibbonAction("#{grid}.performCopy;", viewDefinition));
        ribbonCopyAction.setIcon("copyIcon16.png");
        ribbonCopyAction.setName("copy");
        ribbonCopyAction.setEnabled(false);
        ribbonCopyAction.setDefaultEnabled(false);
        ribbonCopyAction.setScript("var listener = {onChange: function(selectedArray) {if (selectedArray.length == 0) {"
                + "this.disable();} else {this.enable();}}}; #{grid}.addOnChangeListener(listener);");
        ribbonCopyAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonCopyAction;
    }

    private InternalRibbonActionItem createGridNewAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonNewAction = new RibbonActionItemImpl();
        ribbonNewAction.setAction(RibbonUtils.translateRibbonAction("#{grid}.performNew;", viewDefinition));
        ribbonNewAction.setIcon("newIcon24.png");
        ribbonNewAction.setName("new");
        ribbonNewAction.setEnabled(true);
        ribbonNewAction.setType(RibbonActionItem.Type.BIG_BUTTON);
        return ribbonNewAction;
    }

    private InternalRibbonActionItem createGridActivateAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonActivateAction = new RibbonActionItemImpl();
        ribbonActivateAction.setAction(RibbonUtils.translateRibbonAction("#{grid}.performActivate;", viewDefinition));
        ribbonActivateAction.setIcon("unactiveVisibleIcon.png");
        ribbonActivateAction.setName("activate");
        ribbonActivateAction.setEnabled(false);
        ribbonActivateAction.setScript("var listener = {onChange: function(selectedArray) {if (selectedArray.length == 0) {"
                + "this.disable();} else {this.enable();}}}; #{grid}.addOnChangeListener(listener);");
        ribbonActivateAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonActivateAction;
    }

    private InternalRibbonActionItem createGridDeactivateAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonDeactivateAction = new RibbonActionItemImpl();
        ribbonDeactivateAction.setAction(RibbonUtils.translateRibbonAction("#{grid}.performDeactivate;", viewDefinition));
        ribbonDeactivateAction.setIcon("unactiveNotVisibleIcon.png");
        ribbonDeactivateAction.setName("deactivate");
        ribbonDeactivateAction.setEnabled(false);
        ribbonDeactivateAction.setScript("var listener = {onChange: function(selectedArray) {if (selectedArray.length == 0) {"
                + "this.disable();} else {this.enable();}}}; #{grid}.addOnChangeListener(listener);");
        ribbonDeactivateAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonDeactivateAction;
    }

    private InternalRibbonActionItem createGridExportPdfAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonExportPdfAction = new RibbonActionItemImpl();
        ribbonExportPdfAction.setIcon("pdfIcon16.png");
        ribbonExportPdfAction.setName("pdf");
        ribbonExportPdfAction.setEnabled(true);
        ribbonExportPdfAction
                .setScript("var listener = {onClick: function() {#{grid}.performEvent('exportToPdf', [], 'exportToPdf');}};"
                        + " this.addOnChangeListener(listener);");
        ribbonExportPdfAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonExportPdfAction;
    }

    private InternalRibbonActionItem createGridExportCsvAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonExportCsvAction = new RibbonActionItemImpl();
        ribbonExportCsvAction.setIcon("exportToCsvIcon16.png");
        ribbonExportCsvAction.setName("csv");
        ribbonExportCsvAction.setEnabled(true);
        ribbonExportCsvAction
                .setScript("var listener = {onClick: function() {#{grid}.performEvent('exportToCsv', [], 'exportToCsv');}};"
                        + " this.addOnChangeListener(listener);");
        ribbonExportCsvAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonExportCsvAction;
    }

    private InternalRibbonGroup createFormSaveCopyAndRemoveActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createFormSaveAction(viewDefinition));
        ribbonGroup.addItem(createFormSaveAndBackAction(viewDefinition));
        ribbonGroup.addItem(createFormSaveAndNewAction(viewDefinition));
        ribbonGroup.addItem(createFormCopyAction(viewDefinition));
        ribbonGroup.addItem(createFormCancelAction(viewDefinition));
        ribbonGroup.addItem(createFormDeleteAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createFormCopyAndSaveNewActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createFormSaveAndNewAction(viewDefinition));
        ribbonGroup.addItem(createFormCopyAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createFormSaveAndRemoveActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createFormSaveAction(viewDefinition));
        ribbonGroup.addItem(createFormSaveAndBackAction(viewDefinition));
        ribbonGroup.addItem(createFormCancelAction(viewDefinition));
        ribbonGroup.addItem(createFormDeleteAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createFormSaveAndCancelActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createFormSaveAction(viewDefinition));
        ribbonGroup.addItem(createFormSaveAndBackAction(viewDefinition));
        ribbonGroup.addItem(createFormCancelAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createFormSaveAndBackAndRemoveActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(createFormSaveAndBackAction(viewDefinition));
        ribbonGroup.addItem(createFormCancelAction(viewDefinition));
        ribbonGroup.addItem(createFormDeleteAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonGroup createFormSaveActionTemplate(final ViewDefinition viewDefinition, final SecurityRole role) {
        InternalRibbonActionItem ribbonSaveAction = new RibbonActionItemImpl();
        ribbonSaveAction.setAction(RibbonUtils
                .translateRibbonAction("#{form}.performSave; #{window}.performBack", viewDefinition));
        ribbonSaveAction.setIcon("saveBackIcon24.png");
        ribbonSaveAction.setName("saveBack");
        ribbonSaveAction.setType(RibbonActionItem.Type.BIG_BUTTON);
        ribbonSaveAction.setEnabled(true);
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(ACTIONS, role);
        ribbonGroup.addItem(ribbonSaveAction);

        return ribbonGroup;
    }

    private InternalRibbonGroup createFormActivateAndDeactivateActionsTemplate(final ViewDefinition viewDefinition,
            final SecurityRole role) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(STATES, role);
        ribbonGroup.addItem(createFormActivateAction(viewDefinition));
        ribbonGroup.addItem(createFormDeactivateAction(viewDefinition));
        return ribbonGroup;
    }

    private InternalRibbonActionItem createFormDeleteAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonDeleteAction = new RibbonActionItemImpl();
        ribbonDeleteAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performDelete;", viewDefinition));
        ribbonDeleteAction.setIcon("deleteIcon16.png");
        ribbonDeleteAction.setName("delete");
        ribbonDeleteAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        ribbonDeleteAction.setEnabled(false);
        ribbonDeleteAction.setDefaultEnabled(false);
        ribbonDeleteAction
                .setScript("var listener = {onSetValue: function(value) {if (!value || !value.content) return; if (value.content.entityId) {"
                        + "this.enable();} else {this.disable();}}}; #{form}.addOnChangeListener(listener);");
        return ribbonDeleteAction;
    }

    private InternalRibbonActionItem createFormCancelAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonCancelAction = new RibbonActionItemImpl();
        ribbonCancelAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performCancel;", viewDefinition));
        ribbonCancelAction.setIcon("cancelIcon16.png");
        ribbonCancelAction.setName("cancel");
        ribbonCancelAction.setEnabled(true);
        ribbonCancelAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonCancelAction;
    }

    private InternalRibbonActionItem createFormCopyAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonCopyAction = new RibbonActionItemImpl();
        ribbonCopyAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performCopy;", viewDefinition));
        ribbonCopyAction.setIcon("copyIcon16.png");
        ribbonCopyAction.setName("copy");
        ribbonCopyAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        ribbonCopyAction.setEnabled(false);
        ribbonCopyAction.setDefaultEnabled(false);
        // ribbonCopyAction.setMessage("recordNotCreated");
        ribbonCopyAction
                .setScript("var listener = {onSetValue: function(value) {if (!value || !value.content) return; if (value.content.entityId) {"
                        + "this.enable();} else {this.disable();}}}; #{form}.addOnChangeListener(listener);");
        return ribbonCopyAction;
    }

    private InternalRibbonActionItem createFormSaveAndBackAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonSaveBackAction = new RibbonActionItemImpl();
        ribbonSaveBackAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performSave; #{window}.performBack;",
                viewDefinition));
        ribbonSaveBackAction.setIcon("saveBackIcon24.png");
        ribbonSaveBackAction.setName("saveBack");
        ribbonSaveBackAction.setEnabled(true);
        ribbonSaveBackAction.setType(RibbonActionItem.Type.BIG_BUTTON);
        return ribbonSaveBackAction;
    }

    private InternalRibbonActionItem createFormSaveAndNewAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonSaveNewAction = new RibbonActionItemImpl();
        ribbonSaveNewAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performSaveAndClear;", viewDefinition));
        ribbonSaveNewAction.setIcon("saveNewIcon16.png");
        ribbonSaveNewAction.setName("saveNew");
        ribbonSaveNewAction.setEnabled(true);
        ribbonSaveNewAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonSaveNewAction;
    }

    private InternalRibbonActionItem createFormSaveAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonSaveAction = new RibbonActionItemImpl();
        ribbonSaveAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performSave;", viewDefinition));
        ribbonSaveAction.setIcon("saveIcon24.png");
        ribbonSaveAction.setName("save");
        ribbonSaveAction.setEnabled(true);
        ribbonSaveAction.setType(RibbonActionItem.Type.BIG_BUTTON);
        return ribbonSaveAction;
    }

    private InternalRibbonActionItem createFormActivateAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonActivateAction = new RibbonActionItemImpl();
        ribbonActivateAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performActivate;", viewDefinition));
        ribbonActivateAction.setIcon("unactiveVisibleIcon.png");
        ribbonActivateAction.setName("activate");
        ribbonActivateAction.setEnabled(false);
        ribbonActivateAction
                .setScript("var listener = {onSetValue: function(value) {if (!value || !value.content) return; if (value.content.entityId "
                        + "&& !value.content.isActive) {this.enable();} else {this.disable();}}}; #{form}.addOnChangeListener(listener);");
        ribbonActivateAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonActivateAction;
    }

    private InternalRibbonActionItem createFormDeactivateAction(final ViewDefinition viewDefinition) {
        InternalRibbonActionItem ribbonDeactivateAction = new RibbonActionItemImpl();
        ribbonDeactivateAction.setAction(RibbonUtils.translateRibbonAction("#{form}.performDeactivate;", viewDefinition));
        ribbonDeactivateAction.setIcon("unactiveNotVisibleIcon.png");
        ribbonDeactivateAction.setName("deactivate");
        ribbonDeactivateAction.setEnabled(false);
        ribbonDeactivateAction
                .setScript("var listener = {onSetValue: function(value) {if (!value || !value.content) return; if (value.content.entityId "
                        + "&& value.content.isActive) {this.enable();} else {this.disable();}}}; #{form}.addOnChangeListener(listener);");
        ribbonDeactivateAction.setType(RibbonActionItem.Type.SMALL_BUTTON);
        return ribbonDeactivateAction;
    }

}
