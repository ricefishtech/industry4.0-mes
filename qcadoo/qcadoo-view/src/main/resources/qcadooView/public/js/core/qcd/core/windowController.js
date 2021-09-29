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

QCD.WindowController = function(_menuStructure) {

	var iframe = null;

	var loadingIndicator;

	var statesStack = new Array();

	var modalsStack = new Array();

	var modalObjects = new Object();

	var serializationObjectToInsert = null;

	var currentPage = null;

	var currentMenuItem = null;

	var messagesController = new QCD.MessagesController();

	var menuStructure = _menuStructure;
	var menuController

	var lastPageController;

	function constructor(_this) {
		iframe = $("#mainPageIframe");
		loadingIndicator = $("#loadingIndicator");
		loadingIndicator.hide();
		iframe.load(function() {
			onIframeLoad(this);
		});
		$(window).bind('resize', updateSize);

		menuController = new QCD.menu.MenuController(menuStructure, _this);

		updateSize();
	}

	this.addMessage = function(message) {
		messagesController.addMessage(message);
	}

	this.performLogout = function() {
		window.location = "j_spring_security_logout";
	}

	this.goToPage = function(url, serializationObject, isPage) {
		if (serializationObject) {
			serializationObject.currentMenuItem = getCurrentMenuItem();
			statesStack.push(serializationObject);
		}
		if (isPage) {
			currentPage = "page/" + url;
		} else {
			currentPage = url;
		}
		performGoToPage(currentPage);
	}

	function getCurrentMenuItem() {
		var currentActive = menuController.getCurrentActive();
		if (currentActive.first) {
			currentMenuItem = currentActive.first.name;
		}
		if (currentActive.second) {
			currentMenuItem += "." + currentActive.second.name;
		}
		return currentMenuItem;
	}

	window.openModal = function(id, url, serializationObject, onCloseListener,
			afterInitListener, dimensions) {
		if (serializationObject != null) {
			serializationObject.openedModal = true;
			statesStack.push(serializationObject);
		}

		if (!modalObjects[id]) {
			modalObjects[id] = QCD.utils.Modal.createModal();
		}
		if (onCloseListener) {
			modalObjects[id].onCloseListener = onCloseListener;
		}
		if (dimensions) {
			modalObjects[id].changeSize(dimensions.width || 1000,
					dimensions.height || 560);
		}

		modalsStack.push(modalObjects[id]);

		if (url.indexOf("?") != -1) {
			url += "&";
		} else {
			url += "?";
		}
		url += "popup=true";

		var contextPath = window.location.protocol + "//"
				+ window.location.host;
		if (url.indexOf(contextPath) == -1) {
			url = "page/" + url;
		}

		modalObjects[id].show(url, function() {
			if (this.src != "" && this.contentWindow.init) {
				this.contentWindow
						.init(serializationObjectToInsert, dimensions);
				serializationObjectToInsert = null;
			}
			if (afterInitListener) {
				afterInitListener(this.contentWindow);
			}
		});

		return modalObjects[id].iframe[0].contentWindow;
	}

	window.changeModalSize = function(width, height) {
		if (modalsStack.length == 0) {
			return;
		}
		var modal = modalsStack[modalsStack.length - 1];
		modal.changeSize(width, height);
	}

	this.onLoginSuccess = function() {
		this.goToLastPage();
	}

	this.goBack = function(pageController) {
		lastPageController = pageController;
		if(statesStack.length > 0){
            var stateObject = statesStack.pop();
            serializationObjectToInsert = stateObject;
            if (stateObject.openedModal) {
                modal = modalsStack.pop();
                modal.hide();
                if (modalsStack.length == 0) {
                    onIframeLoad();
                } else {
                    onIframeLoad(modalsStack[modalsStack.length - 1].iframe[0]);
                }
            } else {
                currentPage = stateObject.url;
                performGoToPage(currentPage);
            }
		} else {
		    this.goToDashboard();
		}
	}

	this.getStatesStackLength = function() {
	    return statesStack.length;
	}

	this.closeThisModalWindow = function(status) {
		modal = modalsStack.pop();
		modal.hide();
		if (modal.onCloseListener) {
			modal.onCloseListener(status);
		}
	}

	this.getLastPageController = function() {
		return lastPageController;
	}

	this.goToLastPage = function() {
		performGoToPage(currentPage);
	}

	this.activateMenuPosition = function(position) {
		menuController.activateMenuPosition(position);
	}

	this.goToMenuPosition = function(position) {
		menuController.goToMenuPosition(position);
	}

	this.goToDashboard = function() {
		if (hasMenuPosition("home.home")) {
			this.goToMenuPosition("home.home");
		}
	}

	this.hasMenuPosition = function(position) {
		return menuController.hasMenuPosition(position);
	}

	this.updateMenu = function() {
		menuController.updateMenu();
	}

	this.onSessionExpired = function(serializationObject, isModal) {
		serializationObjectToInsert = serializationObject;
		if (isModal) {
			var modal = modalsStack[modalsStack.length - 1]
			modal.show("login.html?popup=true&targetUrl="
					+ escape(serializationObject.url), function() {
				if (this.src != "" && this.contentWindow.init) {
					this.contentWindow.init(serializationObjectToInsert);
					serializationObjectToInsert = null;
				}
			});
		} else {
			performGoToPage("login.html");
		}
	}

	this.restoreMenuState = function() {
		menuController.restoreState();
		menuController.fillBreadCrumbs();
		menuController.setPageTitle();
	}

	this.canChangePage = function() {
		try {
			if (iframe[0].contentWindow.canClose) {
				return iframe[0].contentWindow.canClose();
			}
		} catch (e) {
		}
		return true;
	}

	this.onMenuClicked = function(pageName) {
		currentPage = pageName;
		statesStack = new Array();
		performGoToPage(currentPage);
	}

	function performGoToPage(url) {
		if (messagesController.isInitialized()) {
			messagesController.clearMessager();
		}
		loadingIndicator.show();
		if(url.indexOf(window.location.origin) > -1){
		    window.location.hash = url.split(window.location.origin)[1];
		} else {
		    window.location.hash = url;
		}
		if (url.search("://") <= 0) {
			if (url.indexOf("?") == -1) {
				url += "?iframe=true";
			} else {
				if (url.charAt(url.length - 1) == '?') {
					url += "iframe=true";
				} else {
					url += "&iframe=true";
				}
			}
		}
		// TODO mina
		if (modalsStack.length > 0) { // isModal
			var modal = modalsStack[modalsStack.length - 1]; // opened modal
			modal.iframe.attr('src', url);
		} else {
			iframe.attr('src', url);
		}
	}

	function onIframeLoad(iframeToInit) {
		if (!iframeToInit) {
			iframeToInit = iframe[0];
		}
		try {
			if (iframeToInit.contentWindow.init) {
				iframeToInit.contentWindow.init(serializationObjectToInsert);
				serializationObjectToInsert = null;
			}
		} catch (e) {
			// I swear that I'll chase each one who puts an empty catch statement!
			QCD.error(e);
		}
		loadingIndicator.hide();
	}

	function updateSize() {
		var width = $(document).width();
		var margin = Math.round(width * 0.02);
		var innerWidth = Math.round(width - 2 * margin);
		$("#q_menu_row1").width(innerWidth);
		$("#secondLevelMenu").width(innerWidth);
	}
	this.updateSize = updateSize;

	constructor(this);

}