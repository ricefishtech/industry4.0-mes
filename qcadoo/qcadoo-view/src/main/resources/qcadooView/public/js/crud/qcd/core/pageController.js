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

QCD.PageController = function() {
	var viewName;
	var pluginIdentifier;
	var hasDataDefinition;
	var isPopup;

	var pageComponents;

	var headerComponent = null;

	var pageOptions;

	var messagesController;

	var popup;

	var actionEvaluator = new QCD.ActionEvaluator(this);

	var referencesObject = {};

	var tabController = new QCD.TabController()

	var windowUrl = window.location.href;

	var serializationObjectToInsert;

	var isScriptsPerformed = false;

	this.constructor = function(_viewName, _pluginIdentifier, _hasDataDefinition, _isPopup) {
		viewName = _viewName;
		pluginIdentifier = _pluginIdentifier;
		hasDataDefinition = _hasDataDefinition;
		isPopup = _isPopup;

		QCD.components.elements.utils.LoadingIndicator.blockElement($("body"));

		QCDConnector.windowName = "/page/"+pluginIdentifier+"/"+viewName;
		QCDConnector.mainController = this;

		var pageOptionsElement = $("body").children("#pageOptions");
		pageOptions = JSON.parse($.trim(pageOptionsElement.html()));
		pageOptionsElement.remove();


		var contentElement = $("body");
		pageComponents = QCDPageConstructor.getChildrenComponents(contentElement.children(), this);
		QCD.debug(pageComponents);

		tabController.updateTabObjects()

		$(window).bind('resize', updateSize);
		if (! isPopup) {
			updateSize();
		}

		if (window.parent) {
			$(window.parent).focus(onWindowClick);
		} else {
			$(window).focus(onWindowClick);
		}
		blockButtons();
	}

	this.init = function(serializationObject, dimensions) {

        function tryFocusFirstInput() {
            try {
                var form = window.mainController.getComponentByReferenceName("form");
                if (typeof form === "undefined") {
                    return;
                }
                $(form.element).find("input, textarea").not(".lookupInputWrapper input").first().focus();
            } catch (e) {
                console.error(e);
            }
        }

		if (isPopup) {
			if (window.parent.changeModalSize) {
				var modalWidth = (dimensions ? dimensions.width : pageOptions.windowWidth) || 1000;
				var modalHeight = (dimensions ? dimensions.height : pageOptions.windowHeight) || 560;
				window.parent.changeModalSize(modalWidth, modalHeight);
			}
			updateSize();
		}
		QCD.components.elements.utils.LoadingIndicator.blockElement($("body"));
		if (! isScriptsPerformed) {
			for (var i in pageComponents) {
				pageComponents[i].performScript();
			}
			isScriptsPerformed = true;
		}
		if (serializationObject) {
			setComponentState(serializationObject);
			if (hasDataDefinition) {
				this.callEvent("initializeAfterBack", null, function() {QCD.components.elements.utils.LoadingIndicator.unblockElement($("body"))});
			} else {
				QCD.components.elements.utils.LoadingIndicator.unblockElement($("body"));
			}
		} else {
			if (hasDataDefinition) {
				this.callEvent("initialize", null, function() {
                    tryFocusFirstInput();
                    QCD.components.elements.utils.LoadingIndicator.unblockElement($("body"))
                });
			} else {
				for (var i in pageComponents) {
					pageComponents[i].performInitialize();
				}
                tryFocusFirstInput();
				QCD.components.elements.utils.LoadingIndicator.unblockElement($("body"));
				unblockButtons()
			}
		}
	}

	this.setContext = function(contextStr) {
	    contextStr = contextStr.replace("~~~", "\\\"");
		var context = JSON.parse(contextStr);
		for (var i in context) {
			var dotPos = i.lastIndexOf(".");
			var contextComponentPath = i.substring(0, dotPos);
			var contextField = i.substring(dotPos+1);
			var contextComponent = this.getComponent(contextComponentPath);
			contextComponent.addContext(contextField, context[i]);
		}
	}


	this.callEvent = function(eventName, component, completeFunction, args, actionsPerformer, type) {
		var initParameters = new Object();
		var eventCompleteFunction = completeFunction;
		initParameters.event = {
			name: eventName
		}
		if (component) {
			initParameters.event.component = component;
			var componentObject = getComponent(component);
			var componentListeners = componentObject.options.listeners;
			if (componentListeners) {
				for (var i = 0; i<componentListeners.length; i++) {
					var listenerElement = getComponent(componentListeners[i]);
					if (listenerElement != null) {
						listenerElement.setComponentLoading(true);
					}
				}
				eventCompleteFunction = function() {
					if (completeFunction) {
						completeFunction();
					}
					for (var i = 0; i<componentListeners.length; i++) {
						var listenerElement = getComponent(componentListeners[i]);
						if (listenerElement != null) {
							listenerElement.setComponentLoading(false);
						}
					}
				}
			}
		}
		if (args) {
			initParameters.event.args = args;
		}
		initParameters.components = getValueData();
		performEvent(initParameters, eventCompleteFunction, actionsPerformer, type);
	}

	this.generateReportForEntity = function(actionsPerformer, arg1, args, ids) {
		if (args.length < 2) {
			QCD.error("generateReportForEntity - wrong arguments number");
			return;
		}
		var reportPlugin = trim(arg1);
		var reportName = trim(args[0]);
		var reportType = trim(args[1]);
		if (!reportType || reportType == "") {
			QCD.error("generateReportForEntity - no report type defined");
			return;
		}
		if (!reportPlugin || reportPlugin == "") {
			QCD.error("generateReportForEntity - no report plugin defined");
			return;
		}
		if (!reportName || reportName == "") {
			QCD.error("generateReportForEntity - no template name defined");
			return;
		}
		var userArgs = {};
		for (var i = 2; i < args.length; i++) {
			var arg = trim(args[i]);
			argParts = arg.split("=");
			if (argParts.length != 2) {
				QCD.error("wrong argument '"+arg+"'");
				return;
			}
			var key = trim(argParts[0]);
			var value = trim(argParts[1]);
			userArgs[key] = value;
		}
		var url = "/generateReportForEntity/"+reportPlugin+"/"+reportName+"."+reportType+"?additionalArgs="+JSON.stringify(userArgs);
		for (var i=0; i<ids.length; i++) {
			url += "&id="+ids[i];
		}
		window.open(url, "_blank", "status=0");

		if (actionsPerformer) {
			actionsPerformer.performNext();
		}
	}

	function trim(arg) {
		if (arg[0] == "\"" || arg[0] == "\'") {
			if (arg[arg.length-1] == arg[0]) {
				arg = arg.substring(1, arg.length-1);
			} else {
				QCD.error("wrong argument '"+arg+"'");
				return;
			}
		}
		return arg;
	}


	function performEvent(parameters, completeFunction, actionsPerformer, type) {
		blockButtons();
		var parametersJson = JSON.stringify(parameters);
		QCDConnector.sendPost(parametersJson, function(response) {
			unblockButtons();
			if (completeFunction) {
				completeFunction();
			}
			if (response.redirect) {
				var contextPath = window.location.protocol+"//"+window.location.host;
				var redirectUrl = response.redirect.url.replace(/\$\{root\}/, contextPath);
				if (response.redirect.openInNewWindow) {
					openNewWindow(redirectUrl);
				} else if (response.redirect.openInModalWindow) {
                    openModal(redirectUrl.replace(/\?.*$/, ""), redirectUrl);
				} else if (isPopup) {
				    preparePopup(redirectUrl);
				} else {
					goToPage(putShowBackInContext(redirectUrl), false, response.redirect.shouldSerializeWindow);
					return;
				}
			} else {
				setValueData(response);
			}
			if (actionsPerformer && ! (response.content && response.content.status && response.content.status != "ok") && (typeof actionsPerformer.performNext === 'function')) {
				actionsPerformer.performNext();
			}
		}, function() {
			unblockButtons();
			if (completeFunction) {
				completeFunction();
			}
		}, type);
	}

	function putShowBackInContext(url) {
		if (url.indexOf("context={") == -1) {
			return appendGetVariableToUrl(url,
					"context={\"window.showBack\":true}");
		}
		return url.replace("context={", "context={\"window.showBack\":true,");
	}

	function appendGetVariableToUrl(url, variableString) {
		if (url.indexOf("?") != -1) {
			url += "&";
		} else {
			url += "?";
		}
		url += variableString;
		return url;
	}

	this.getActionEvaluator = function() {
		return actionEvaluator;
	};

	function blockButtons() {
		headerComponent.blockButtons();
	}

	function unblockButtons() {
		headerComponent.unblockButtons();
	}

	function getValueData() {
		var values = new Object();
		for (var i in pageComponents) {
			var value = pageComponents[i].getValue();
			if (value) {
				values[i] = value;
			}
		}
		return values;
	}

	function setComponentState(state) {
		for (var i in state.components) {
			var component = pageComponents[i];
			component.setState(state.components[i]);
		}
		if (state.currentMenuItem) {
			window.parent.activateMenuPosition(state.currentMenuItem);
		}
	}

	this.showMessage = function(message) {
		if (window.parent && window.parent.addMessage) {
			window.parent.addMessage(message);
		} else {
			if (!messagesController) {
				messagesController = new QCD.MessagesController();
			}
			messagesController.addMessage(message);
		}
	}

	this.setWindowHeaderComponent = function(component) {
		headerComponent = component;
	}
	this.setWindowHeader = function(header) {
		if (headerComponent) {
			headerComponent.setHeader(header);
		}
	}

	this.getViewName = function() {
		return pluginIdentifier+"/"+viewName;
	}

	function setValueData(data) {
		QCD.debug(data);
		if (data.messages) {
			for (var i in data.messages) {
				var message = data.messages[i];
				window.parent.addMessage(message);
			}
		}
		for (var i in data.components) {
			var component = pageComponents[i];
			component.setValue(data.components[i]);
		}
	}

	this.getComponent = function(componentPath) {
		var pathParts = componentPath.split(".");
		var component = pageComponents[pathParts[0]];
		if (! component) {
			return null;
		}
		for (var i = 1; i<pathParts.length; i++) {
			if (!component.components) {
				return null;
			}
			component = component.components[pathParts[i]];
			if (! component) {
				return null;
			}
		}
		return component;
	}
	var getComponent = this.getComponent;

	this.registerReferenceName = function(referenceName, object) {
		referencesObject[referenceName] = object;
	}

	this.getComponentByReferenceName = function(referenceName) {
		return referencesObject[referenceName];
	}

	this.getTabController = function() {
		return tabController;
	}

	function onWindowClick() {
		if (popup) {
			popup.parentComponent.onPopupClose();
			popup.window.close();
			popup = null;
		}
	}

	this.closePopup = function() {
		if (popup) {
			popup.parentComponent.onPopupClose();
			try {
				popup.window.close();
			} catch (e) {
			}
			popup = null;
		}
	}

	// TODO MAKU remove old pop-up code
	this.openPopup = function(url, parentComponent, title) {
		if (url.indexOf("?") != -1) {
			url+="&";
		} else {
			url+="?";
		}
		if(window.location.href.indexOf("page/") == -1){
				url = "page/"+pluginIdentifier+"/"+url;
			}
		url+="popup=true";
		popup = new Object();
		popup.pageController = this;
		popup.parentComponent = parentComponent;
		var left = (screen.width/2)-(400);
		var top = (screen.height/2)-(350);
		popup.window = window.open(url, title, 'status=0,toolbar=0,width=800,height=700,left='+left+',top='+top);
		return popup.window;
	}

	this.onPopupInit = function() {
		popup.parentComponent.onPopupInit();
	}

	this.isPopup = function() {
		return isPopup;
	}

	this.updateMenu = function() {
		window.parent.updateMenu();
	}

	this.activateMenuPosition = function(position) {
		window.parent.activateMenuPosition(position);
	}

	this.goToPage = function(url, isPage, serialize) {
		if (isPage == undefined || isPage == null) {
			isPage = true;
		}
		var serializationObject = null;
		if (serialize == true || serialize == undefined || serialize == null) {
			QCD.components.elements.utils.LoadingIndicator.blockElement($("body"));
			serializationObject = getSerializationObject();
		}
		if (isPopup) {
			if (url.indexOf("?") != -1) {
				url+="&";
			} else {
				url+="?";
			}
			url+="popup=true";
		}
		url = encodeParams(url);
		window.parent.goToPage(url, serializationObject, isPage);
	}
	var goToPage = this.goToPage;

	function openModal(id, url, shouldSerialize, onCloseListener, afterInitListener, dimensions) {
		shouldSerialize = (shouldSerialize == undefined) ? true : shouldSerialize;
		var serializationObject = null;
		if (shouldSerialize) {
			serializationObject = getSerializationObject();
		}
		url = encodeParams(url);
		return window.parent.openModal(id, url, serializationObject, onCloseListener, afterInitListener, dimensions);
	}
	this.openModal = openModal;

    function openNewWindow(url) {
        url = encodeParams(url);
        window.open(url, "_blank", "status=0");
    }
    this.openNewWindow = openNewWindow;

    function preparePopup(url) {
        url = encodeParams(url);
		window.location = url;
    }
    this.preparePopup = preparePopup;

    function encodeParams(url) {
        if(url.indexOf("context=") != -1){
            url = url.substring(0, url.indexOf("context=") + 8) + encodeURIComponent(url.substring(url.indexOf("context=") + 8, url.length));
        }
        return url;
    }

	function canClose() {
        changed = false;
        for (var i in pageComponents) {
            if(pageComponents[i].isChanged()) {
                changed = true;
            }
        }
        if(changed) {
            return window.confirm(pageOptions.translations.backWithChangesConfirmation);
        } else {
            return true;
        }
    }
    this.canClose = canClose;

	this.goBack = function (omitConfirm) {
		if (omitConfirm || canClose()) {
			QCD.components.elements.utils.LoadingIndicator.blockElement($("body"));
			window.parent.goBack(this);
		}
	};

	function getSerializationObject() {
		return {
			url: windowUrl,
			components: getValueData(),
			currentMenuItem: null
		};
	}

	this.getLastPageController = function() {
		var lastPageController =  window.parent.getLastPageController();
		try {
			lastPageController.getViewName()
		} catch(e) {
			return null;
		}
		return lastPageController;
	}

	this.closeWindow = function() {
		window.close();
	}

	this.closeThisModalWindow = function(actionsPerformer, status) {
		window.parent.closeThisModalWindow(status);
	}

	this.onSessionExpired = function() {
		if (!isPopup) {
			window.parent.onSessionExpired(getSerializationObject());
		} else {
			if (window.parent.onSessionExpired) { // modal
				window.parent.onSessionExpired(getSerializationObject(), true);
			} else { // popup
				window.location = "/login.html?popup=true&targetUrl="+escape(windowUrl);
			}
		}
	}

	this.getCurrentUserLogin = function() {
		return window.parent.getCurrentUserLogin();
	}

	function updateSize() {
		var width = $(window).width();
		var height = $(window).height();
		for (var i in pageComponents) {
			pageComponents[i].updateSize(width, height);
		}
	}
	this.updateSize = updateSize;

}
