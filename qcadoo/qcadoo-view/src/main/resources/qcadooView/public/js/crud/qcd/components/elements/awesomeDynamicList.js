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

QCD.components.elements.AwesomeDynamicList = function (element, mainController) {
    "use strict";

	$.extend(this, new QCD.components.Container(element, mainController));

	var that = this,
	    elementPath = this.elementPath,
	    elementSearchName = this.elementSearchName,
	    innerFormContainer = null,
	    awesomeDynamicListContent = null,
	    awesomeDynamicListHeader = null,
	    awesomeDynamicListHeaderObject = null,
	    formObjects = null,
	    formObjectsIndex = 1,
	    currentWidth = 0,
	    currentHeight = 0,
	    buttonsArray = [],
	    firstLine = null,
	    BUTTONS_WIDTH = 70,
	    hasButtons = this.options.hasButtons,
	    flipOrder = this.options.flipOrder,
	    enabled = true,
	    isChanged = false,
	    components = {},
	    isRequired = false,
	    hasListeners = (this.options.listeners && this.options.listeners.length > 0);

	if (!(this instanceof QCD.components.elements.AwesomeDynamicList)) {
	    return new QCD.components.elements.AwesomeDynamicList(element, mainController);
	}

	function constructor() {
		innerFormContainer = $("#" + that.elementSearchName + " > .awesomeDynamicList > .awesomeDynamicListInnerForm").children();

		awesomeDynamicListContent = $("#" + that.elementSearchName + " > .awesomeDynamicList > .awesomeDynamicListContent");
		awesomeDynamicListHeader = $("#" + that.elementSearchName + " > .awesomeDynamicList > .awesomeDynamicListHeader");
		if (awesomeDynamicListHeader && awesomeDynamicListHeader.length > 0) {
			awesomeDynamicListHeaderObject = QCDPageConstructor.getChildrenComponents(awesomeDynamicListHeader.children(), mainController).header;
			awesomeDynamicListHeaderObject.setEnabled(true, true);
		}

		formObjects = [];
		if (!hasButtons) {
			BUTTONS_WIDTH = 0;
		}

		if (that.options.referenceName) {
            mainController.registerReferenceName(that.options.referenceName, that);
        }

		that.components = components;

		updateButtons();
	}

	this.getComponentValue = function () {
		var formValues = [],
		    formObjectsLen = formObjects.length,
		    i = 0,
		    formObject = null;

		for (i = 0; i < formObjectsLen; i++) {
		    formObject = formObjects[i];
			if (!formObject) {
				continue;
			}
			formValues.push({
				name: formObject.elementName,
				value: formObject.getValue()
			});
		}
		return {
			forms: formValues
		};
	};

	this.setComponentValue = function (value) {
		var forms = value.forms;
		if (typeof value.required !== 'undefined') {
			isRequired = value.required;
		}
		if (forms) {
			formObjects = [];
			awesomeDynamicListContent.empty();
			this.components = {};
			components = this.components;
			formObjectsIndex = 1;
			for (var i in forms) {
				var formValue = forms[i];
				var formObject = getFormCopy(formObjectsIndex);
				formObject.setValue(formValue);
				formObjects[formObjectsIndex] = formObject;
				this.components[formObject.elementName] = formObject;
				formObjectsIndex++;
			}

			if (isRequired && formObjectsIndex == 1) {
				var formObject = getFormCopy(formObjectsIndex, true);
				formObjects[formObjectsIndex] = formObject;
				this.components[formObject.elementName] = formObject;
				formObjectsIndex++;
			}

			updateButtons();
		} else {
            (function(that) {
                var innerFormChanges = value.innerFormChanges,
                    component;
                for (var i in innerFormChanges) {
                    component = that.components[i];
                    if (typeof component !== 'undefined') {
                        that.components[i].setValue(innerFormChanges[i]);
                    }
                }
            })(this);
		}
		mainController.updateSize();
	};

	this.setComponentState = function (state) {
	    state.forms = state.forms.map(function (form) {return form.value;})
		this.setComponentValue(state);
	};

	this.setComponentEnabled = function (isEnabled) {
	    var buttonsArrayLen = buttonsArray.length,
	        i = 0;

		enabled = isEnabled;

		for (i = 0; i < buttonsArrayLen; i++) {
		    if (!buttonsArray[i]) {
		        continue;
		    }
	        if (enabled) {
	            buttonsArray[i].addClass("enabled");
	        } else {
	            buttonsArray[i].removeClass("enabled");
            }
		}
	};

	this.isComponentChanged = function () {
		if (isChanged) {
			return true;
		}
		for (var i in formObjects) {
			if (formObjects[i] && !formObjects[i].isVirtual && formObjects[i].isChanged()) {
				return true;
			}
		}
		return false;
	};

	this.performUpdateState = function () {
		isChanged = false;
	};

	this.setComponentLoading = function (isLoadingVisible) {
	};

	this.updateSize = function (width, height) {
		currentWidth = width;
		currentHeight = height;
		for (var i in formObjects) {
			if (formObjects[i]) {
                if(hasButtons){
                    $("#" + elementSearchName + "_line_" + i + " .awesomeListFormContainer:eq(0)").attr("style", "min-width:" + (width - BUTTONS_WIDTH - 20) + "px; max-width:" + (width - BUTTONS_WIDTH - 20) + "px;");
                }
				formObjects[i].updateSize(width - BUTTONS_WIDTH, height);
			}
		}
		if (awesomeDynamicListHeaderObject) {
            awesomeDynamicListHeader.width(width - ((BUTTONS_WIDTH > 0) ? BUTTONS_WIDTH + 20 : 20));
			awesomeDynamicListHeaderObject.updateSize(width - ((BUTTONS_WIDTH > 0) ? BUTTONS_WIDTH + 20 : 20), height);
		}

		$(".awesomeListLine").addClass('forceRedraw').removeClass('forceRedraw'); // IE fix - force redraw
	};

	function getFormCopy(formId, isVirtual) {
		isVirtual = isVirtual ? isVirtual : false;
		var copy = innerFormContainer.clone();

		changeElementId(copy, formId);
		var line = $("<div>").addClass("awesomeListLine").attr("id", elementPath+"_line_"+formId);
		var formContainer = $("<span>").addClass("awesomeListFormContainer");
		formContainer.append(copy);
		line.append(formContainer);
		if (hasButtons) {
			var buttons = $("<span>").addClass("awesomeListButtons");

			var removeLineButton = $("<a>").addClass("awesomeListButton").addClass("awesomeListMinusButton").addClass("enabled").attr("id", elementPath+"_line_"+formId+"_removeButton");
			removeLineButton.css("display", "none");
			removeLineButton.click(function(e) {
				var button = $(e.target);
				if (button.hasClass("enabled")) {
					var lineId = button.attr("id").substring(elementPath.length+6, button.attr("id").length-13);
					removeRowClicked(lineId);
				}
			});
			buttonsArray.push(removeLineButton);
			buttons.append(removeLineButton);
			var addLineButton = $("<a>").addClass("awesomeListButton").addClass("awesomeListPlusButton").addClass("enabled").attr("id", elementPath+"_line_"+formId+"_addButton");
			addLineButton.click(function(e) {
				var button = $(e.target);
				if (button.hasClass("enabled")) {
					var lineId = button.attr("id").substring(elementPath.length+6, button.attr("id").length-10);
					addRowClicked(lineId);
				}
			});
			addLineButton.css("display", "none");
			buttonsArray.push(addLineButton);
			buttons.append(addLineButton);

			line.append(buttons);
		}
		if(flipOrder){
            $(awesomeDynamicListContent).prepend($(line));
		} else {
			awesomeDynamicListContent.append(line);
		}
		var formObject = QCDPageConstructor.getChildrenComponents(copy, mainController)["innerForm_"+formId];

		formObject.isVirtual = isVirtual;

		formObject.updateSize(currentWidth - BUTTONS_WIDTH, currentHeight);
		var componentValue = formObject.getValue();
		deleteContentFromField(componentValue);
		formObject.setValue(componentValue);

		return formObject;
	}

	function deleteContentFromField(value){
		if (value.content) {
			value.content = null;
		}
		for (var i in value.components) {
			var formObj = value.components[i];
			if (formObj) {
				deleteContentFromField(formObj);
			}
		}
		return value;
	}

	function updateButtons() {
	    var objectCounter = 0,
	        lastObject = 0;
		if (!hasButtons) {
			return;
		}
		for (var i in formObjects) {
			if (formObjects[i]) {
				objectCounter++;
				lastObject = i;
			}
		}
		if (objectCounter  > 0) {
			if (firstLine) {
				firstLine.hide();
				firstLine = null;
			}
			for (var i in formObjects) {
				if (! formObjects[i]) {
					continue;
				}
				var line = $("#"+elementSearchName+"_line_"+i);
				var removeButton = $("#"+elementSearchName+"_line_"+i+"_removeButton");
				var addButton = $("#"+elementSearchName+"_line_"+i+"_addButton");
				if (!(isRequired && objectCounter<=1)){
				    if(enabled){
					    removeButton.show();
					    removeButton.css("display", "inline-block");
                    }
				} else {
					removeButton.hide();
				}
				if (i == lastObject) {
				    if(enabled){
					    addButton.show();
					    addButton.css("display", "inline-block");
					}
					line.addClass("lastLine");
				} else {
					addButton.hide();
					line.removeClass("lastLine");
				}
			}
		} else {
			firstLine = $("<div>").addClass("awesomeListLine").addClass("lastLine").attr("id", elementPath+"_line_0");
			var buttons = $("<span>").addClass("awesomeListButtons");
			var addLineButton = $("<a>").addClass("awesomeListButton").addClass("awesomeListPlusButton").attr("id", elementPath+"_line_0_addButton");
			addLineButton.click(function (e) {
				var button = $(e.target),
				    lineId;
				if (button.hasClass("enabled")) {
					lineId = button.attr("id").substring(elementPath.length + 6, button.attr("id").length - 10);
					addRowClicked(lineId);
				}
			});
			buttons.append(addLineButton);
			buttonsArray.push(addLineButton);
			if (enabled) {
				addLineButton.addClass("enabled");
			}
			firstLine.append(buttons);
			awesomeDynamicListContent.append(firstLine);
		}
	}

    function addRowClicked(rowId) {
        var addedRowForm = getFormCopy(formObjectsIndex);
        formObjects[formObjectsIndex] = addedRowForm;
        components[addedRowForm.elementName] = addedRowForm;
        isChanged = true;
        formObjectsIndex++;
        updateButtons();
        mainController.updateSize();
        that.fireOnChangeListeners("onAddRow", [addedRowForm, rowId]);
        if (hasListeners) {
            mainController.callEvent("onAddRow", elementPath, null, [rowId]);
        }
    }

    function removeRowClicked(rowId) {
        var line = $("#" + elementSearchName + "_line_" + rowId),
            deletedRowForm = formObjects[rowId];
        line.remove();
        isChanged = true;
        formObjects[rowId] = null;
        updateButtons();
        mainController.updateSize();
        that.fireOnChangeListeners("onDeleteRow", [deletedRowForm, rowId]);
        if (hasListeners) {
            mainController.callEvent("onDeleteRow", elementPath);
        }
    }

	function changeElementId(element, formId) {
		var id = element.attr("id");
		if (id) {
			element.attr("id", id.replace("@innerFormId", formId));
		}
		element.children().each(function (i, e) {
			var kid = $(e);
			changeElementId(kid, formId);
		});
	}

	constructor();
};
