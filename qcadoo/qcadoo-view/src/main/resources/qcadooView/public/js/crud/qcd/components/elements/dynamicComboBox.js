/*
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
var QCD = QCD || {};
QCD.components = QCD.components || {};
QCD.components.elements = QCD.components.elements || {};

QCD.components.elements.DynamicComboBox = function (element, mainController) {
    "use strict";
    
    if (!(this instanceof QCD.components.elements.DynamicComboBox)) {
        return new QCD.components.elements.DynamicComboBox(element, mainController);
    }
    
	$.extend(this, new QCD.components.elements.FormComponent(element, mainController));

    var that = this,
        values = [],
        hasListeners = (this.options.listeners.length > 0) ? true : false;
	
	if (this.options.referenceName) {
		mainController.registerReferenceName(this.options.referenceName, this);
	}
	
	function inputDataChanged() {
	    that.fireOnChangeListeners("onChange", [that.input.val()]);
		if (hasListeners) {
			mainController.callEvent("onSelectedEntityChange", that.elementPath, null, null, null);
		}
	}
	
	function setTitle() {
		var title = that.input.find(':selected').text(),
		    value = that.input.val();
		
		if (title && value) {		
			that.input.attr('title', title);
		} else {
			that.input.removeAttr('title');
		}
	}
	
	this.getComponentData = function () {
		var selected = this.input.val();
		return {
			value: selected,
			values: values
		};
	};
	
	function setData(data) {
	    var availableValuesLen = 0,
	        availableValue = null,
	        i = 0;
		if (data.values) {
			values = data.values;
			availableValuesLen = values.length;
			that.input.children().remove();
			for (i = 0; i < availableValuesLen; i++) {
				availableValue = values[i];
				that.input.append("<option value='" + availableValue.key + "'>" + availableValue.value + "</option>");
			}
		}
		that.input.val(data.value);
		setTitle();
	}
	
	this.setComponentData = function (data) {
        setData(data);
    };
	
	this.setComponentEnabled = function (isEnabled) {
		if (isEnabled) {
			element.removeClass("disabled");
			this.input.removeAttr('disabled');
		} else {
			element.addClass("disabled");
			this.input.attr('disabled', 'true');
		}
		if (this.setFormComponentEnabled) {
			this.setFormComponentEnabled(isEnabled);
		}
	};
	
	this.updateSize = function (width, height) {
		height = height ? height - 10 : 40;
		this.input.parent().parent().height(height);
	};
		

    function constructor() {
        that.input.change(function () {
            setTitle();
            inputDataChanged();
        });
    }
	constructor();
	
};
