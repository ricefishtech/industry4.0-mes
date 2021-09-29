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

QCD.components.elements.ContextualHelpButton = {};

QCD.components.elements.ContextualHelpButton.createBigButton = function(url, title, target) {
	var contextualHelpButton = QCD.components.elements.ContextualHelpButton.createButton(url, 'helpIcon24.png', title, target);
	contextualHelpButton.addClass('bigButton');
	return contextualHelpButton;
}

QCD.components.elements.ContextualHelpButton.createSmallButton = function(url, title, target) {
	var contextualHelpButton = QCD.components.elements.ContextualHelpButton.createButton(url, 'helpIcon16.png', title, target);
	contextualHelpButton.addClass('smallButton');
	return contextualHelpButton;
}

QCD.components.elements.ContextualHelpButton.createButton = function(url, icon, title, target) {
	var elementIcon = (icon && $.trim(icon) != "") ? $.trim(icon) : null;
	
	if (elementIcon.indexOf("/") == -1) {
		elementIcon = '/qcadooView/public/img/core/icons/' + icon;
	}
	
	if (!target) {
		target = "_blank";
	}
	
	var itemElement = $('<a>');
	itemElement.attr('href', url);
	itemElement.attr('target', target);
	if (title) {
		itemElement.attr('title', title);
	}
	
	var itemIcon = $('<img>');
	itemIcon.attr('src', elementIcon);
	itemIcon.attr('border', '0');
	
	itemElement.append(itemIcon);
	
	var itemElementWrapper = $('<div>');
	itemElementWrapper.addClass('contextualHelpButton');
	
	itemElementWrapper.append(itemElement);
	
	return itemElementWrapper;
}
