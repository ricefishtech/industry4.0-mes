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

QCD.components.elements.StaticComponent = function (element, mainController) {
    "use strict";
    
    if (!(this instanceof QCD.components.elements.StaticComponent)) {
        return new QCD.components.elements.StaticComponent(element, mainController);
    }
    
	$.extend(this, new QCD.components.Component(element, mainController));
	
	var changed = false;
	
	if (this.options.referenceName) {
		mainController.registerReferenceName(this.options.referenceName, this);
	}

	this.setComponentState = function (state) {
	};
	
	this.getComponentValue = function () {
		return null;
	};
	
	this.setComponentValue = function (value) {
	};
	
	this.setComponentEnabled = function (isEnabled) {
	};
	
	this.setComponentLoading = function (isLoadingVisible) {
	};
	
	this.performUpdateState = function () {
        changed = false;
    };
    
    this.setComponentChanged = function (isChanged) {
        changed = isChanged;
    };
    
    this.isComponentChanged = function () {
        return changed;
    };
    
};
