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

QCD.components.elements.Lookup = function(_element, _mainController) {
	$.extend(this, new QCD.components.elements.FormComponent(_element,
			_mainController));

	var element = _element;
	var elementPath = this.elementPath;

	var translations = this.options.translations;

	var fireOnChangeListeners = this.fireOnChangeListeners;

	var AUTOCOMPLETE_TIMEOUT = 400;

	var keyboard = {
		UP : 38,
		DOWN : 40,
		ENTER : 13,
		ESCAPE : 27
	};

	var elements = {
		input : this.input,
		text : $("#" + this.elementSearchName + "_text"),
		loading : $("#" + this.elementSearchName + "_loadingDiv"),
		label : $("#" + this.elementSearchName + "_labelDiv"),
		openLookupButton : $("#" + this.elementSearchName + "_openLookupButton"),
		lookupDropdown : $("#" + this.elementSearchName + "_lookupDropdown")
	};

	var labels = {
		normal : elements.label.html(),
		focus : "<span class='focusedLabel'>"
				+ this.options.translations.labelOnFocus + "</span>"
	};

	var viewState = {
		isFocused : false,
		error : null
	};

	var modalDimensions = this.options.modalDimensions || {
		width : 1000,
		height : 560
	};

	var dataState = {
		currentCode : null,
		selectedEntity : {
			id : null,
			value : null,
			code : null
		},
		autocomplete : {
			matches : null,
			code : null,
			entitiesNumber : null
		},
		contextEntityId : null,
		criteriaModifierParameter: null
	}

	var autocompleteRefreshTimeout = null;

	var blurAfterLoad = false;

	var lookupDropdown = new QCD.components.elements.lookup.Dropdown(
			elements.lookupDropdown, this, translations);

	var hasListeners = (this.options.listeners.length > 0) ? true : false;

	var _this = this;

	var lookupWindow;

	var baseValue;

	if (this.options.referenceName) {
		_mainController.registerReferenceName(this.options.referenceName, this);
	}

	function constructor(_this) {

		elements.openLookupButton.click(openLookup);

		elements.input.focus(function() {
			viewState.isFocused = true;
			onViewStateChange();
		}).blur(function() {
			viewState.isFocused = false;
			onViewStateChange();
		});

		elements.input.keyup(function(e) {
			if(elements.input.attr("readonly")){
				return;
			}
			var key = getKey(e);
			if (key == keyboard.UP) {
				if (!lookupDropdown.isOpen()) {
					onInputValueChange(true);
				}
				lookupDropdown.selectPrevious();

			} else if (key == keyboard.DOWN) {
				if (!lookupDropdown.isOpen()) {
					onInputValueChange(true);
				}
				lookupDropdown.selectNext();

			} else if (key == keyboard.ENTER) {
				if (!lookupDropdown.isOpen()) {
					return;
				}
				var entity = lookupDropdown.getSelected();
				if (entity == null) {
					return;
				}
				performSelectEntity(entity);
				dataState.currentCode = dataState.selectedEntity.code;
				elements.input.val(dataState.currentCode);
				elements.input.removeClass('inactive');
				lookupDropdown.hide();

			} else if (key == keyboard.ESCAPE) {
				preventEvent(e);
				elements.input.val(dataState.currentCode);
				elements.input.removeClass('inactive');
				lookupDropdown.hide();
			} else {
				var inputVal = elements.input.val();
				if (dataState.currentCode != inputVal) {
					dataState.currentCode = inputVal;
					performSelectEntity(null);
					onInputValueChange();
				}
			}
		});

		// prevent event propagation
		elements.input.keydown(function(e) {
			var key = getKey(e);
			if (key == keyboard.UP || key == keyboard.ESCAPE) {
				preventEvent(e);
				return false;
			}
		}).keypress(function(e) {
			var key = getKey(e);
			if (key == keyboard.UP || key == keyboard.ESCAPE) {
				preventEvent(e);
				return false;
			}
		});
	}

	this.getComponentData = function() {
		return {
			value : dataState.selectedEntity.id,
			oldValue : (baseValue ? baseValue.selectedEntityId : null),
			selectedEntityValue : dataState.selectedEntity.value,
			selectedEntityCode : dataState.selectedEntity.code,
			selectedEntityActive : dataState.selectedEntity.active,
			currentCode : dataState.currentCode,
			autocompleteCode : dataState.autocomplete.code,
			contextEntityId : dataState.contextEntityId,
			criteriaModifierParameter : dataState.criteriaModifierParameter
		};
	}

	this.setComponentData = function(data) {
		if (data.clearCurrentCodeCode) {
			dataState.currentCode = "";
		} else {
			dataState.currentCode = data.currentCode ? data.currentCode
					: dataState.currentCode;
		}
		dataState.selectedEntity.id = data.value ? data.value : null;
		dataState.selectedEntity.value = data.selectedEntityValue;
		dataState.selectedEntity.code = data.selectedEntityCode;
		dataState.selectedEntity.active = data.selectedEntityActive;
		dataState.autocomplete.matches = data.autocompleteMatches ? data.autocompleteMatches
				: [];
		dataState.autocomplete.code = data.autocompleteCode ? data.autocompleteCode
				: "";
		dataState.autocomplete.entitiesNumber = data.autocompleteEntitiesNumber;
		dataState.criteriaModifierParameter = data.criteriaModifierParameter;
		if (dataState.contextEntityId != data.contextEntityId) {
			dataState.contextEntityId = data.contextEntityId;
			dataState.currentCode = "";
		}
		// initialaize current code on first load
		if (!dataState.currentCode) {
			dataState.currentCode = dataState.selectedEntity.id ? dataState.selectedEntity.code
					: "";
		}
		onDataStateChange();
	}

	this.setComponentBaseValue = function(state) {
		if (state.currentCode != undefined && state.value != undefined) {
			baseValue = {
				currentCode : state.currentCode,
				selectedEntityId : state.value
			};
		}
	}

	this.performUpdateState = function() {
		baseValue = {
			currentCode : dataState.currentCode,
			selectedEntityId : dataState.selectedEntity.id
		};
	}

	this.isComponentChanged = function() {
		if (!baseValue) {
			return false;
		}
		return !(dataState.currentCode == baseValue.currentCode);
	}

	function onViewStateChange() {
		if (viewState.isFocused && !elements.input.attr("readonly")) {
			elements.openLookupButton.addClass("lightHover");
			elements.label.html(labels.focus);
			elements.input.val(dataState.currentCode);
			elements.input.removeClass('inactive');
		} else {
			elements.openLookupButton.removeClass("lightHover");
			lookupDropdown.hide();

			if (autocompleteRefreshTimeout || elements.loading.is(':visible')) {
				blurAfterLoad = true;
				return;
			}

			viewState.error = null;
			if (!dataState.selectedEntity.id && !lookupDropdown.getSelected()
					&& !lookupDropdown.getMouseSelected()
					&& dataState.autocomplete.matches
					&& dataState.currentCode != "") {
				if (dataState.autocomplete.matches.length == 0) {
					viewState.error = translations.noMatchError;
				} else if (dataState.autocomplete.matches.length > 1) {
					viewState.error = translations.moreTahnOneMatchError;
				} else {
					performSelectEntity(dataState.autocomplete.matches[0]);
				}
			}

			if (viewState.error == null) {
				elements.label.html(labels.normal);
				if (dataState.selectedEntity.id) {

				} else if (lookupDropdown.getMouseSelected()) {
					performSelectEntity(lookupDropdown.getMouseSelected());
					dataState.currentCode = lookupDropdown.getMouseSelected().code;
				} else if (lookupDropdown.getSelected()) {
					performSelectEntity(lookupDropdown.getSelected());
					dataState.currentCode = lookupDropdown.getSelected().code;
				}
				elements.input.val(stripHTML(dataState.selectedEntity.value));
				elements.text.html(dataState.selectedEntity.value);
				if (!dataState.selectedEntity.active) {
					elements.input.addClass('inactive');
				} else {
					elements.input.removeClass('inactive');
				}
			} else {
				_this.addMessage( {
					title : "",
					content : viewState.error
				});
				element.addClass("error");
			}
		}
	}

	function onDataStateChange() {

		if (dataState.autocomplete.code == dataState.currentCode) {
			elements.loading.hide();
		}
		if (blurAfterLoad) {
			blurAfterLoad = false;
			viewState.isFocused = false;
			lookupDropdown.updateAutocomplete(dataState.autocomplete.matches,
					dataState.autocomplete.entitiesNumber);
			onViewStateChange();
			return;
		}

		// awesomeDynamicList isn't so awesome - it steals the focus.
        // Focusing shouldn't be performed after back from lookup's modal window.
		if (!lookupWindow && !viewState.isFocused && dataState.autocomplete.code) {
			dataState.currentCode = dataState.autocomplete.code;
			viewState.isFocused = true;
			$(elements.input).trigger("focus");
			elements.input.val(dataState.autocomplete.code);
		}

		if (viewState.isFocused) {
			lookupDropdown.updateAutocomplete(dataState.autocomplete.matches,
					dataState.autocomplete.entitiesNumber);
			lookupDropdown.show();
		} else {
			if (dataState.selectedEntity.value) {
				elements.input.val(stripHTML(dataState.selectedEntity.value));
			} else {
				elements.input.val(dataState.currentCode);
			}
			elements.text.html(dataState.selectedEntity.value);
			if (!dataState.selectedEntity.active) {
				elements.input.addClass('inactive');
			} else {
				elements.input.removeClass('inactive');
			}

		}
	}

	function onInputValueChange(immidiateRefresh) {
		if (autocompleteRefreshTimeout) {
			window.clearTimeout(autocompleteRefreshTimeout);
			autocompleteRefreshTimeout = null;
		}
		if (immidiateRefresh) {
			elements.loading.show();
			mainController.callEvent("autompleteSearch", elementPath, null,
					null, null);
		} else {
			autocompleteRefreshTimeout = window.setTimeout(function() {
				autocompleteRefreshTimeout = null;
				elements.loading.show();
				mainController.callEvent("autompleteSearch", elementPath, null,
						null, null);
			}, AUTOCOMPLETE_TIMEOUT);
		}
	}

	function performSelectEntity(entity, callEvent) {
		if (callEvent == undefined) {
			callEvent = true;
		}
		if (entity) {
			dataState.selectedEntity.id = entity.id;
			dataState.selectedEntity.code = entity.code;
			dataState.selectedEntity.value = entity.value;
			dataState.selectedEntity.active = entity.active;
		} else {
			dataState.selectedEntity.id = null;
			dataState.selectedEntity.code = null;
			dataState.selectedEntity.value = null;
			dataState.selectedEntity.active = true;
		}
		fireOnChangeListeners("onChange", [ entity ]);
		if (hasListeners && callEvent) {
			mainController.callEvent("onSelectedEntityChange", elementPath,
					null, null, null);
		}
	}

	function stripHTML(text) {
		if (!text || text == "") {
			return "";
		}
		var re = /<\S[^><]*>/g
		return text.replace(re, "");
	}

	this.updateSize = function(_width, _height) {
		var height = _height ? _height - 10 : 40;
		this.input.parent().parent().parent().parent().parent().height(height);
	}

	function preventEvent(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
		e.stopPropagation();
		e.keyCode = 0;
		e.which = 0;
		e.returnValue = false;
	}

	function getKey(e) {
		return e.keyCode || e.which;
	}

	this.setFormComponentEnabled = function(isEnabled) {
		if (isEnabled) {
			elements.openLookupButton.addClass("enabled")
		} else {
			elements.openLookupButton.removeClass("enabled")
		}
	}

	function openLookup() {
		if (!elements.openLookupButton.hasClass("enabled")) {
			return;
		}
		var url = _this.options.viewName + ".html";

		var params = new Object();
		if (dataState.contextEntityId) {
			params["window.grid.belongsToEntityId"] = dataState.contextEntityId;
			url += "?context=" + JSON.stringify(params);
		}
		else if (dataState.criteriaModifierParameter) {
			params["window.grid.options"] = {
					criteriaModifierParameter: dataState.criteriaModifierParameter
				};
			url += "?context=" + JSON.stringify(params);
			url = url.replace("\\\"", "~~~");
		}
		lookupWindow = mainController.openModal(elementPath + "_lookup", window.pluginIdentifier + "/" + url, false, onModalClose, onModalRender, modalDimensions);
	}

	function onModalClose() {
		lookupWindow = null;
	}

	function onModalRender(modalWindow) {
		modalWindow.getComponent("window.grid").setLinkListener(_this);
	}

	this.onGridLinkClicked = function(entityId) {
		var grid = lookupWindow.mainController.getComponent("window.grid");
		var lookupData = grid.getLookupData(entityId);
		performSelectEntity({
			id : lookupData.entityId,
			code : lookupData.lookupCode,
			value : lookupData.lookupValue
			},false);
		dataState.currentCode = lookupData.lookupCode;
		onDataStateChange();
		onViewStateChange();
		if (hasListeners) {
			mainController.callEvent("onSelectedEntityChange", elementPath,
					null, null, null);
		}
		mainController.closeThisModalWindow();
	}

	constructor(this);
}