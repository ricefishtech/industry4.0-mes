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

QCD.components.elements.GanttChartHeader = function(_listener, _elementId, _translations, _options) {
	
	var listener = _listener;
	
	var elementId = _elementId;
	
	var options = _options;

	var headerElement;
	
	var buttons = {};
	
	var dateFromElement;
	var dateToElement;
	
	var dateFromElementErrorBox;
	var dateToElementErrorBox;
	
	var dateFromCalendar;
	var dateToCalendar;
	
	var globalErrorBox;
	
	var currentScale = "H3";
	
	var translations = _translations
	
	function constructor() {
		headerElement = $("<div>").addClass("ganttHeaderContainer");
		
		headerElement.append($("<div>").html(translations["header.label"]).addClass("ganttHeaderContainerLabel"));
		
		var zoomButtonsElement = $("<div>").addClass("ganttHeaderZoomButtons");
		headerElement.append(zoomButtonsElement);
		
		buttons.zoom = {};
		
		buttons.zoom.H1 = QCD.components.elements.utils.HeaderUtils.createHeaderButton("",function(e) {
			if (buttons.zoom.H1.hasClass("headerButtonEnabled")) {
				listener.onScaleChanged("H1");
			}
		}, "zoom1hIcon2.png").attr("title", translations["header.zoom1h"]);
		buttons.zoom.H3 = QCD.components.elements.utils.HeaderUtils.createHeaderButton("",function(e) {
			if (buttons.zoom.H3.hasClass("headerButtonEnabled")) {
				listener.onScaleChanged("H3");
			}
		}, "zoom3hIcon2.png").attr("title", translations["header.zoom3h"]);
		buttons.zoom.H6 = QCD.components.elements.utils.HeaderUtils.createHeaderButton("",function(e) {
			if (buttons.zoom.H6.hasClass("headerButtonEnabled")) {
				listener.onScaleChanged("H6");
			}
		}, "zoom6hIcon2.png").attr("title", translations["header.zoom6h"]);
		buttons.zoom.D1 = QCD.components.elements.utils.HeaderUtils.createHeaderButton("",function(e) {
			if (buttons.zoom.D1.hasClass("headerButtonEnabled")) {
				listener.onScaleChanged("D1");
			}
		}, "zoom1dIcon2.png").attr("title", translations["header.zoom1d"]);
		zoomButtonsElement.append(buttons.zoom.H1);
		zoomButtonsElement.append(buttons.zoom.H3);
		zoomButtonsElement.append(buttons.zoom.H6);
		zoomButtonsElement.append(buttons.zoom.D1);
		
		dateFromElement = QCD.components.elements.utils.HeaderUtils.createDatePicker(elementId+"_dateFrom", translations["header.dateFrom"]);
		dateFromElementErrorBox = getErrorBox();
		dateFromElement.append(dateFromElementErrorBox);
		headerElement.append(dateFromElement);
		dateToElement = QCD.components.elements.utils.HeaderUtils.createDatePicker(elementId+"_dateTo", translations["header.dateTo"]);
		dateToElementErrorBox = getErrorBox();
		dateToElement.append(dateToElementErrorBox);
		headerElement.append(dateToElement);
		
		globalErrorBox = getErrorBox().css("left", "100px").css("width", "350px");
		dateFromElement.append(globalErrorBox);
		
		buttons.zoom[currentScale].addClass("headerButtonActive");
		
	}
	
	function getErrorBox() {
		var errorBox = $("<div>").css("position", "absolute").css("bottom", "30px").css("left", "0").css("border", "solid red 1px")
		errorBox.css("background", "#F5E9E4").css("padding", "5px").css("display", "none").css("width","200px").css("text-align","center");
		return errorBox;
	}
	
	this.getHeaderElement = function() {
		return headerElement;
	}
	
	this.init = function() {
		dateFromCalendar = new QCD.components.elements.Calendar(dateFromElement);
		dateToCalendar = new QCD.components.elements.Calendar(dateToElement);
		
		dateFromCalendar.addOnChangeListener({
			onChange: onCalendarSelect
		});
		dateToCalendar.addOnChangeListener({
			onChange: onCalendarSelect
		});
		
		if (options.allowDateSelection == false) {
			dateFromCalendar.setFormComponentEnabled(false);
			dateToCalendar.setFormComponentEnabled(false);
		} else {
			dateFromCalendar.setFormComponentEnabled(true);
			dateToCalendar.setFormComponentEnabled(true);
		}
	}
	
	function onCalendarSelect() {
		listener.onDateChanged();
	}
	
	this.setDateFromValue = function(date, errorMessage) {
		dateFromCalendar.setComponentError(errorMessage != null);
		if (errorMessage == null) {
			dateFromCalendar.setComponentValue({value:date});
			dateFromElementErrorBox.hide();
		} else {
			dateFromElementErrorBox.html(errorMessage).show();
		}
	}
	this.setDateToValue = function(date, errorMessage) {
		dateToCalendar.setComponentError(errorMessage != null);
		if (errorMessage == null) {
			dateToCalendar.setComponentValue({value:date});
			dateToElementErrorBox.hide();
		} else {
			dateToElementErrorBox.html(errorMessage).show();
		}
	}
	
	this.setGlobalErrorMessage = function(errorMessage) {
		if (errorMessage != null) {
			dateFromCalendar.setComponentError(true);
			dateToCalendar.setComponentError(true);
			globalErrorBox.html(errorMessage).show();
		} else {
			dateFromCalendar.setComponentError(false);
			dateToCalendar.setComponentError(false);
			globalErrorBox.hide();
		}
	}
	
	this.getCurrentParameters = function() {
		return {
			scale: currentScale,
			dateFrom: dateFromCalendar.getComponentValue().value,
			dateTo: dateToCalendar.getComponentValue().value
		};
	}
	
	this.setCurrentScale = function(newScale) {
		buttons.zoom[currentScale].removeClass("headerButtonActive");
		currentScale = newScale;
		buttons.zoom[currentScale].addClass("headerButtonActive");
	}
	
	this.disableButtons = function() {
		buttons.zoom.H1.addClass("headerButtonEnabled");
		buttons.zoom.H3.addClass("headerButtonEnabled");
		buttons.zoom.H6.addClass("headerButtonEnabled");
		buttons.zoom.D1.addClass("headerButtonEnabled");
	}
	this.enableButtons = function() {
		buttons.zoom.H1.addClass("headerButtonEnabled");
		buttons.zoom.H3.addClass("headerButtonEnabled");
		buttons.zoom.H6.addClass("headerButtonEnabled");
		buttons.zoom.D1.addClass("headerButtonEnabled");
	}
	
	constructor();
}