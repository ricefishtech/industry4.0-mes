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
QCD.components.containers = QCD.components.containers || {};

QCD.components.containers.WindowTab = function(element, mainController) {
    "use strict";

    if (!(this instanceof QCD.components.containers.WindowTab)) {
        return new QCD.components.containers.WindowTab(element, mainController);
    }

	$.extend(this, new QCD.components.Container(element, mainController));
	
	var that = this,
	    ribbon = null,
	    ribbonElement = null,
	    tabHeaderElement = null,
	    isVisible = true;

	function constructor() {
		var childrenElement = $("#"+that.elementSearchName+" > div");
		that.constructChildren(childrenElement.children());

		if (that.options.ribbon) {
			ribbon = new QCD.components.Ribbon(that.options.ribbon, that.elementName, mainController, that.options.translations);
			ribbonElement = ribbon.constructElementContent();
		}
		
		if (that.options.referenceName) {
			mainController.registerReferenceName(that.options.referenceName, that);
		}
	}

	this.getRibbonElement = function() {
		return ribbonElement;
	}
	
	this.getRibbonItem = function(ribbonItemPath) {
		return ribbon.getRibbonItem(ribbonItemPath);
	}
	
	this.performComponentScript = function() {
		if (ribbon) {
			ribbon.performScripts();
		}
	}
	
	this.getComponentValue = function() {
		return {};
	}
	this.setComponentValue = function(value) {
		setContextualHelpButton(value.contextualHelpUrl);
        if (value.ribbon) {
            ribbon.updateRibbonState(value.ribbon);
        }
	}
	
	function setContextualHelpButton(url) {
		var contentElement = that.element.find("div:first"),
		    windowTabContextualHelpButton = $("#" + that.elementSearchName + "_contextualHelpButton"),
		    button = {};

		if (windowTabContextualHelpButton.length) {
			if (url) {
				windowTabContextualHelpButton.find("a").attr("href", url);
			} else {
				windowTabContextualHelpButton.parent().removeClass("hasContextualHelpButton");
				windowTabContextualHelpButton.remove();
			}
			return;
		}

		if (!url) {
			return;
		}

		button = QCD.components.elements.ContextualHelpButton.createSmallButton(url, that.options.translations["contextualHelpTooltip"]);
		button.attr("id", that.elementPath+"_contextualHelpButton");
		contentElement.addClass("hasContextualHelpButton");
		contentElement.prepend(button);
	}
	
	this.setComponentState = function(state) {
	}
	
	this.setMessages = function(messages) {
	}
	
	this.setComponentEnabled = function(isEnabled) {
	}
	
	this.setComponentLoading = function() {
	}

	this.setHeaderElement = function (tabElement) {
	    tabHeaderElement = tabElement;
	};

	this.setComponentVisible = function (shouldBeVisible) {
	    if (typeof tabHeaderElement === 'undefined' || tabHeaderElement.length == 0) {
	        QCD.error("Can't find header element for tab '" + that.elementName + "'");
	        return;
	    }
        if (shouldBeVisible) {
            tabHeaderElement.show();
            element.show();
        } else {
            tabHeaderElement.hide();
            element.hide();
        }
	};
	
	this.blockButtons = function() {
		if (ribbon) {
			ribbon.blockButtons();
		}
	}
	
	this.unblockButtons = function() {
		if (ribbon) {
			ribbon.unblockButtons();
		}
	}
	
	this.updateSize = function(_width, _height) {
		var componentsHeight = _height ? _height-20 : null;
		for (var i in this.components) {
			this.components[i].updateSize(_width-20, componentsHeight);
		}
	}
	
	constructor();
}
