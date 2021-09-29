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

QCD.components.containers.Window = function(element, mainController) {
    "use strict";

    if (!(this instanceof QCD.components.containers.Window)) {
        return new QCD.components.containers.Window(element, mainController);
    }

	$.extend(this, new QCD.components.Container(element, mainController));

	this.element.css("height","100%");

	var that = this,
	    ribbon,
	    row3Element,
	    ribbonLeftElement,
	    tabsLeftElement,
	    ribbonMainElement,
	    tabRibbonDiv,
	    tabsRightElement,
	    ribbonShadowElement,
	    currentWidth,
	    currentHeight,
	    tabs = {},
	    tabHeaders = {},
	    tabRibbonExists = false,
	    oneTab = this.options.oneTab,
	    hasTabs = false,
	    currentTabName,
	    innerWidthMarker = $("#"+this.elementSearchName+"_windowContainerContentBodyWidthMarker");
	
	function constructor() {
		var childrenElement = $("#"+that.elementSearchName+"_windowComponents"),
		    tabsElement = $("#"+that.elementSearchName+"_windowTabs > div"),
		    tabName = "";

		that.constructChildren(childrenElement.children());
		mainController.setWindowHeaderComponent(that);
		tabs = that.getChildren();

		for (tabName in tabs) {
		    if (!(tabs[tabName] instanceof QCD.components.containers.WindowTab)) {
		        continue;
		    }
			var tabElement = $("<a>").attr("href","#").html(that.options.translations["tab."+tabName]).bind('click', {tabName: tabName}, function(e) {
				e.target.blur();
				showTab(e.data.tabName);
			});
			hasTabs = true;
			tabs[tabName].setHeaderElement(tabElement);
			tabHeaders[tabName] = tabElement;
			tabsElement.append(tabElement);
			if (tabs[tabName].getRibbonElement && tabs[tabName].getRibbonElement()) {
				tabRibbonExists = true;
			}
		}
		
		if (that.options.hasRibbon) {
		
			if (that.options.ribbon) {
				ribbon = new QCD.components.Ribbon(that.options.ribbon, that.elementName, mainController, that.options.translations);
			}
				
			element = $("<div>");
			
			row3Element =  $("<div>").attr("id", "q_row3_out_container");
			element.append(row3Element);
			
			ribbonLeftElement = $("<div>").attr("id", "q_row3_out_left");
			row3Element.append(ribbonLeftElement);
			
			ribbonMainElement = $("<div>").attr("id", "q_row3_out_main");
			var ribbonAlignment = that.options.ribbon.alignment;
			if (ribbonAlignment) {
				ribbonMainElement.addClass("align-" + ribbonAlignment);
			}
			row3Element.append(ribbonMainElement);
			if (ribbon) {
				ribbonMainElement.append(ribbon.constructElementContent());
			}
			
			if (! oneTab) {
				tabsLeftElement = $("<div>").attr("id", "q_row3_out_tabs_left");
				tabsLeftElement.append($("<div>"));
				row3Element.append(tabsLeftElement);
				
				var tabsElement = $("<div>").attr("id", "q_row3_out_tabs");
				row3Element.append(tabsElement);
				
				tabsRightElement = $("<div>").attr("id", "q_row3_out_tabs_right");
				tabsRightElement.append($("<div>"));
				row3Element.append(tabsRightElement);
				
				tabRibbonDiv = tabsElement;
				for (var tabName in tabs) {
					var tabRibbonElement = tabs[tabName].getRibbonElement();
					if (tabRibbonElement) {
						tabRibbonElement.hide();
						tabRibbonDiv.append(tabRibbonElement);
					}
				}
			}
			
			ribbonShadowElement = $("<div>").attr("id", "q_row4_out");
			element.append(ribbonShadowElement);
			
			var ribbonDiv = $("#"+that.elementPath+"_windowContainerRibbon");
			ribbonDiv.append(element);
		} else {
			$("#"+that.elementPath+"_windowContainerContentBody").css("top","5px");
		}
		
		if (that.options.firstTabName) {
			showTab(that.options.firstTabName);
		}
		
		if (that.options.referenceName) {
			mainController.registerReferenceName(that.options.referenceName, that);
		}
		
	}
	
	function showTab(tabName) {
	    if (!hasTabs) {
	        return;
	    }
		if (currentTabName) {
			tabs[currentTabName].element.children().hide();
			tabHeaders[currentTabName].removeClass("activeTab");
			var tabRibbonElement = tabs[currentTabName].getRibbonElement();
			if (tabRibbonElement) {
				tabRibbonElement.hide();
			}
		}
		currentTabName = tabName;
		if (! oneTab) {
			tabHeaders[tabName].addClass("activeTab");
		}
		tabs[tabName].element.children().show();
		
		if (tabRibbonDiv) {
			if (tabs[tabName].getRibbonElement) {
				if (tabs[tabName].getRibbonElement()) {
					tabs[tabName].getRibbonElement().show();
					tabRibbonDiv.css("display", "inline-block");
					tabsLeftElement.css("display", "inline-block");
					tabsRightElement.css("display", "inline-block");
				} else {
					tabRibbonDiv.css("display", "none");
					tabsLeftElement.css("display", "none");
					tabsRightElement.css("display", "none");
				}
			}
		}
	}
	
	this.getComponentValue = function() {
		return {
		    tabsSelectionState: {
		        activeTab: currentTabName
		    }
		};
	};

    function tabExists(tabName) {
        var tabObj = tabs[tabName];
        return (typeof tabObj === 'object') && (tabObj !== null);
    }

    function tabIsVisible(tabName) {
        var tabObj = tabs[tabName];
        return tabExists(tabName) && tabObj.isVisible();
    }

	function showFirstVisibleTab() {
	    if (!hasTabs || tabs[currentTabName].isVisible()) {
	        return;
	    }
	    var tabName = "";
        for (tabName in tabs) {
            if (!Object.prototype.hasOwnProperty.call(tabs, tabName)) {
                continue;
            }
            if (tabIsVisible(tabName)) {
                showTab(tabName);
                break;
            }
        }
	}

	this.setComponentValue = function(value) {
	    if (hasTabs) {
            for (var tabName in tabs) {
                tabHeaders[tabName].removeClass("errorTab");
            }
            for (var i in value.errors) {
                tabHeaders[value.errors[i]].addClass("errorTab");
            }
		}
		if (value.ribbon) {
			ribbon.updateRibbonState(value.ribbon);
		}
		if (value.activeMenu) {
			mainController.activateMenuPosition(value.activeMenu);
		}
		if (value.tabsSelectionState.updateRequired && value.tabsSelectionState.activeTab) {
		    showTab(value.tabsSelectionState.activeTab);
		}
		setContextualHelpButton(value.contextualHelpUrl);
		showFirstVisibleTab();
	};

	function setContextualHelpButton(url) {
		var contentElement = $("#" + that.elementPath + "_windowContent");
		var windowTabs = contentElement.find("#" + that.elementSearchName + "_windowTabs");
		var windowHeader = contentElement.find("#" + that.elementSearchName + "_windowHeader");
		
		var windowContextualHelpButton = $("#" + that.elementSearchName + "_contextualHelpButton"); 
		if (windowContextualHelpButton.length) {
			if (url) {
				windowContextualHelpButton.find("a").attr("href", url);
			} else {
				windowContextualHelpButton.parent().removeClass("hasContextualHelpButton");
				if (windowTabs.length) {
					windowTabs.removeClass("hasContextualHelpButton");
				}
			}
			return;
		}

		if (!url) {
			return;
		}
		
		var button = QCD.components.elements.ContextualHelpButton.createBigButton(url, that.options.translations["contextualHelpTooltip"]);
		button.attr("id", that.elementPath+"_contextualHelpButton");
			
		if (windowHeader.length) {
			if (!windowTabs.length) {
				button.addClass("withBorder");
			}
			contentElement.prepend(button);
		} else if (windowTabs.length) {
			windowTabs.addClass("hasContextualHelpButton");
			button.addClass("withBorder");
			contentElement.prepend(button);
		} else {
			button.addClass('inComponentHeader');
			var gridHeaderPaging = contentElement.find("#" + that.elementSearchName + "\\.mainTab .gridWrapper:first .grid_header .grid_paging"); 
			gridHeaderPaging.addClass("hasContextualHelpButton");
			gridHeaderPaging.prepend(button);
		}
	}
		
	this.setActiveTab = function (tabName) {
	    if (!(typeof tabName === 'string')) {
	        QCD.error("wrong argument type for setActiveTab - expected string, but given '" + tabName + "'");
	        return;
	    }
        if (!tabExists(tabName)) {
            QCD.error("tab with name '" + tabName + "' doesn't exist.");
            return;
        }
        tabs[tabName].setVisible(true);
	    showTab(tabName);
	};

	this.getTab = function (tabName) {
	    return tabs[tabName];
	};
	
	this.setComponentState = function(state) {
	    if (typeof state.tabsSelectionState.activeTab === 'string') {
	        that.setActiveTab(state.tabsSelectionState.activeTab);
	    }
	};
	
	this.setMessages = function(messages) {
	};
	
	this.setComponentEnabled = function(isEnabled) {
	};
	
	this.setComponentLoading = function() {
	};
	
	this.setHeader = function(header) {
		var headerElement = $("#"+this.elementPath+"_windowHeader");
		if (headerElement) {
			headerElement.html(header);
		}
	};
	
	this.blockButtons = function() {
		if (ribbon) {
			ribbon.blockButtons();
		}
		for (var tabName in tabs) {
			if (tabs[tabName].blockButtons) {
				tabs[tabName].blockButtons();
			}
		}
	};
	
	this.unblockButtons = function() {
		if (ribbon) {
			ribbon.unblockButtons();
		}
		for (var tabName in tabs) {
			if (tabs[tabName].unblockButtons) {
				tabs[tabName].unblockButtons();
			}
		}
	};
	
	this.updateSize = function(_width, _height) {
		currentWidth = _width;
		currentHeight = _height;
		
		var isMinWidth = ! mainController.isPopup();
		
		var childrenElement = $("#"+this.elementSearchName+"_windowContent");
		
		var margin = Math.round(_width * 0.02);
		if (margin < 20 && isMinWidth) {
			margin = 20;
		}
		var ribbonWidth = _width - margin;
		var width = Math.round(_width - 2 * margin);
		/* ricefishM
		if (width < 960 && isMinWidth) {
			width = 960;
			childrenElement.css("marginLeft", margin+"px");
			childrenElement.css("marginRight", margin+"px");
		} else {
			childrenElement.css("marginLeft", "auto");
			childrenElement.css("marginRight", "auto");
		}
		*/
        childrenElement.css("marginLeft", 5+"px");
        childrenElement.css("marginRight", 5+"px");

		childrenElement.width(width + 40);//ricefishM
		childrenElement.css("marginTop", 5+"px");
		if (! this.options.fixedHeight) {
			childrenElement.css("marginBottom", margin+"px");
		}
		var windowWidth = width +2*margin
		var innerWidth = innerWidthMarker.innerWidth();
		
		var height = null;
		if (this.options.fixedHeight) {
			var ribbonHeight = $(".windowContainer .windowContainerRibbon").height() || 70;
			var containerHeight = Math.round(_height - 2 * margin - ribbonHeight);
			height = containerHeight;
			if (this.options.header) {
				height -= 29;
			}
			var childrenElementHeight = containerHeight;
			if (childrenElement.hasClass("displayingHelpPaths")) {
				containerHeight -= 40;
				height -= 35;
			}
			childrenElement.height(childrenElementHeight);
		}
		if (! oneTab) {
			var componentsHeight = height ? height-35 : null;
			for (var i in this.components) {
				this.components[i].updateSize(width, componentsHeight);
			}
		} else {
			var componentsHeight = height;
			for (var i in this.components) {
				this.components[i].updateSize(width, componentsHeight);
			}
		}
		
		this.element.width(windowWidth);
		
		if (this.options.hasRibbon) {
			ribbonLeftElement.width(margin);
			ribbonShadowElement.width(innerWidth > windowWidth ? windowWidth : innerWidth);
			if (tabRibbonDiv) {
				var tabRibbonWidth = width - ribbonMainElement.width();
				tabRibbonDiv.width(tabRibbonWidth);
			}
			if (! tabRibbonExists) {
				ribbonMainElement.width(width);
				if (ribbon) {
					ribbon.updateSize(width);
				}
			}
		}
		
	};
	
	this.performRefresh = function() {
		var mainViewComponent = mainController.getComponentByReferenceName("form") || mainController.getComponentByReferenceName("grid");
		if (mainViewComponent) {
			mainViewComponent.performRefresh();
		} else {
			QCD.error("Can't find component #{form} or #{grid}!");
		}
	};
	
	this.performBack = function(actionsPerformer) {
		mainController.goBack();
		if (actionsPerformer) {
			actionsPerformer.performNext();
		}
	};

	this.performBackWithoutConfirm = function(actionsPerformer) {
		mainController.goBack(true);
		if (actionsPerformer) {
			actionsPerformer.performNext();
		}
	};
	this.updateMenu = function() {
		mainController.updateMenu();
	};
	
	this.performCloseWindow = function(actionsPerformer) {
		mainController.closeWindow();
		if (actionsPerformer) {
			actionsPerformer.performNext();
		}
	};
	
	this.closeThisModalWindow = function(actionsPerformer, status) {
		mainController.closeThisModalWindow(actionsPerformer, status);
	};
	
	this.performComponentScript = function() {
		if (ribbon) {
			ribbon.performScripts();
		}
	};
	
	this.getRibbonItem = function(ribbonItemPath) {
		return ribbon.getRibbonItem(ribbonItemPath);
	};
	this.getRibbonItemOrNull = function(ribbonItemPath) {
    		return ribbon.getRibbonItemOrNull(ribbonItemPath);
    	};
	
	constructor();
}
