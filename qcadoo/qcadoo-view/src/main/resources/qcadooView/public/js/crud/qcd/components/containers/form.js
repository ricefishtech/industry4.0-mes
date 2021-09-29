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

QCD.components.containers.Form = function(_element, _mainController) {
	$.extend(this, new QCD.components.Container(_element, _mainController));

    var that = this;	

	var mainController = _mainController;
	var element = _element;
	
	var elementPath = this.elementPath;
	
	var formValue = null;
	
	var baseValue = null; 
	
	var headerEntityIdentifier = null;
	
	var header = null;
	
	var translations = this.options.translations;
	
	var hasHeader = this.options.header;
	
	if (this.options.referenceName) {
		mainController.registerReferenceName(this.options.referenceName, this);
	}
	
	function constructor(_this) {
		var childrenElement = $("#"+_this.elementSearchName+"_formComponents");
		_this.constructChildren(childrenElement.children());
	}

	this.getComponentValue = function() {
		return {
			entityId: formValue,
			baseValue: baseValue,
			headerEntityIdentifier: headerEntityIdentifier,
			header : header
		};
	}
	
    function setWindowHeader(value) {
        if (hasHeader) {
            if (value.headerEntityIdentifier) {
                mainController.setWindowHeader(value.header + ' <span>' + value.headerEntityIdentifier + '</span>');
            } else {
                mainController.setWindowHeader(value.header);
            }
        }
    }

	this.setComponentValue = function (value) {
		if(value.valid) {
		    setWindowHeader(value);
		}
		headerEntityIdentifier = value.headerEntityIdentifier;
		header = value.header;
		formValue = value.entityId;
		unblock();
	};
	
	this.setComponentState = function (state) {
		setWindowHeader(state);
		headerEntityIdentifier = state.headerEntityIdentifier;
		header = state.header;
		formValue = state.entityId;
		if (state.baseValue) {
			baseValue = state.baseValue;
		}
		unblock();
	};
	
	this.setComponentEnabled = function (isEnabled) {
	};
	
	this.setComponentLoading = function (isLoadingVisible) {
		if (isLoadingVisible) {
			block();
		} else {
			unblock();
		}
	};
	
	this.performUpdateState = function() {
		baseValue = formValue;
	}
	
	this.isComponentChanged = function() {
		return ! (baseValue == formValue);
	}
	
	this.performSave = function(actionsPerformer) {
		callEvent("save", actionsPerformer);
	}

	this.performSaveAndClear = function(actionsPerformer) {
		callEvent("saveAndClear", actionsPerformer);
	}
	
	this.performDeactivate = function(actionsPerformer) {
		callEvent("deactivate", actionsPerformer);
	}
	
	this.performActivate = function(actionsPerformer) {
		callEvent("activate", actionsPerformer);
	}
	
	this.performCopy = function(actionsPerformer) {
		if (mainController.canClose()) {
			callEvent("copy", actionsPerformer);
		}
	}
	
	this.performDelete = function (actionsPerformer) {
		if (window.confirm(translations.confirmDeleteMessage + (headerEntityIdentifier !== null ? " " + headerEntityIdentifier : "") + "?")) {
			callEvent("delete", actionsPerformer);
		}
	};
	
	this.performCancel = function(actionsPerformer) {
		if (window.confirm(translations.confirmCancelMessage)) {
			callEvent("reset", actionsPerformer);
		}
	}
	
	this.performRefresh = function(actionsPerformer) {
		if (mainController.canClose()) {
			callEvent("reset", actionsPerformer);
		}
	}
	
	this.generateReportForEntity = function(actionsPerformer, arg1, args) {
		if (formValue) {
			mainController.generateReportForEntity(actionsPerformer, arg1, args, [formValue]);
		} else {
			mainController.showMessage({type: "error", content: translations.noEntity});
		}	
	}
	
	this.performEvent = function(eventName, args) {
		this.fireEvent(null, eventName, args);
	}
	
	function block() {
        QCD.components.elements.utils.LoadingIndicator.blockElement($("#window_windowComponents"));
    }
    
    function unblock() {
        QCD.components.elements.utils.LoadingIndicator.unblockElement($("#window_windowComponents"));
    }
	
	var origSendEvent = this.sendEvent;
    this.sendEvent = function (actionsPerformer, eventObj) {
        var origCallback = eventObj.callback;
        if (typeof origCallback === 'function') {
            eventObj.callback = function () {
                try {
                    origCallback();
                } catch (e) {
                    QCD.error(e);
                } finally {
                    unblock();
                }
            };
        } else {
            eventObj.callback = unblock;
        }
        block();
        origSendEvent.call(this, actionsPerformer, eventObj);
    };
	
	function callEvent(eventName, actionsPerformer, args) {
	    that.sendEvent(actionsPerformer, {
	        name : eventName,
	        args : args
	    });
	}
	
	this.updateSize = function(_width, _height) {
		for (var i in this.components) {
			this.components[i].updateSize(_width, _height);
		}
	}
	
	constructor(this);
}