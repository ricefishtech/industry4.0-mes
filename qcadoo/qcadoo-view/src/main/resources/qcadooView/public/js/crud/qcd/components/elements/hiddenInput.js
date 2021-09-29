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

QCD.components.elements.HiddenInput = function(_element, _mainController) {
	$.extend(this, new QCD.components.elements.FormComponent(_element, _mainController));
	
	var input = this.input;
	
	var elementPath = this.elementPath;
	
	var hasListeners = (this.options.listeners.length > 0) ? true : false;
	
	var fireOnChangeListeners = this.fireOnChangeListeners;
	
	function constructor(_this) {
		input.change(function() {
			inputDataChanged();
		});
	}
	
	function inputDataChanged() {
		fireOnChangeListeners("onChange", [input.val()]);
		if (hasListeners) {
			mainController.callEvent("onInputChange", elementPath, null, null, null);
		}
	}
	
	if (this.options.referenceName) {
		_mainController.registerReferenceName(this.options.referenceName, this);
	}
	
	this.getComponentData = function() {
		return {
			value : input.val()
		}
	}
	
	this.setComponentData = function(data) {
		if (data.value) {
			this.input.val(data.value);
		} else {
			this.input.val("");
		}
	}
		
	constructor(this);
}