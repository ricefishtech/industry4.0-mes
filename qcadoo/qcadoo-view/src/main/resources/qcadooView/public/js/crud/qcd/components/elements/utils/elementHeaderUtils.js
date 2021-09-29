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
QCD.components.elements.utils = QCD.components.elements.utils || {};

QCD.components.elements.utils.HeaderUtils = {};

QCD.components.elements.utils.HeaderUtils.createHeaderButton = function(label, clickAction, icon, clickActionData) {
	var elementIcon = (icon && $.trim(icon) != "") ? $.trim(icon) : null;
	
	if (elementIcon.indexOf("/") == -1) {
		elementIcon = '/qcadooView/public/img/core/icons/'+elementIcon;
	}
	
	var itemElementLabel = $('<div>');
	itemElementLabel.html(label);
	
	var itemElementSpan = $('<span>');
	
	var itemElementButton = $("<a>").attr('href','#').append(itemElementSpan);
	
	if (icon && $.trim(icon) != "") {
		itemElementLabel.addClass('hasIcon');
		itemElementSpan.append($('<div>').addClass('icon').css('backgroundImage', 'url(\''+elementIcon+'\')'));
	}

	itemElementSpan.append(itemElementLabel);
	if (label == "") {
		itemElementLabel.css("paddingLeft", "0px");
		itemElementLabel.css("paddingRight", "3px");
	}
	if (clickActionData) {
		itemElementButton.bind("click", clickActionData, function(e) {
			itemElementButton.blur();
			clickAction.call($(this).parent()[0], e.data);
		});
	} else {
		itemElementButton.bind("click", function(e) {
			itemElementButton.blur();
			clickAction.call($(this).parent(), e.data);
		});
	}
	
	var itemElementButtonWrapper = $("<div>").addClass("headerActionButton").append(itemElementButton);
	itemElementButtonWrapper.label = itemElementLabel;
	
	
	var tooltipElement = $("<div>").addClass("ribbon_description_icon").css("display", "none");
	//var tooltipElement = $("<div>").addClass("ribbon_description_icon");
	var tooltipMessageElement = $("<div>").addClass("description_message").css("display", "none");
	var tooltipMessageElementContent = $("<p>").html("");
	tooltipMessageElement.append(tooltipMessageElementContent);
	itemElementButtonWrapper.append(tooltipElement);
	itemElementButtonWrapper.append(tooltipMessageElement);
	tooltipElement.hover(function() {
		tooltipMessageElement.show();
	}, function() {
		tooltipMessageElement.hide();
	});
	
	itemElementButtonWrapper.setInfo = function(infoText) {
		if (infoText && infoText != "") {
			this.find(".ribbon_description_icon").show();
			this.find(".description_message").html(infoText);
		} else {
			this.find(".ribbon_description_icon").hide();
		}
	};
	
	return itemElementButtonWrapper;
}

QCD.components.elements.utils.HeaderUtils.createHeaderComboBox = function(options, selectAction) {
	
	var select = $("<select>").addClass("headerSelect");
	select.change(function() {
		selectAction(select.val());
	});
	
	for (var i in options) {
		select.append($("<option>").attr("value",options[i].value).html(options[i].label));
	}
	
	select.enable = function() {
		$(this).attr("disabled", false);
	}
	select.disable = function() {
		$(this).attr("disabled", true);
	}
	
	select.setSelectedValue = function(value) {
	    $(this).find("option").filter(function () {
            return $(this).attr('value') == value; 
        }).attr('selected', true);
	};
	
	select.disable();
	
	return select;
}

QCD.components.elements.utils.HeaderUtils.createDatePicker = function(elementId, labelvalue) {
	var container = $("<div>").addClass("component_container_form_w").css("position", "relative").css("width", "100px").css("line-height", "20px");
	container.append($("<div>").addClass("component_container_form_inner_h"));
	var inner = $("<div>").addClass("component_container_form_inner").addClass("required");
		var lookupValueWrapper = $("<div>").addClass("lookupValueWrapper").css("height", "20px").css("line-height", "10px").css("background-color", "white");
			var input = $("<input type='text' />").attr("id", elementId+"_input").css("line-height", "10px");;
			lookupValueWrapper.append($("<div>").addClass("lookupInputWrapper").append(input));
			var button = $("<div>").addClass("lookupButton").addClass("calendarButton").attr("id", elementId+"_calendar");
			lookupValueWrapper.append(button);
		inner.append(lookupValueWrapper);
		inner.append($("<div>").addClass("component_container_form_x"));
		inner.append($("<div>").addClass("component_container_form_y"));
	container.append(inner);
	
	var labelBox = $("<div>").addClass("labelbox").css("width", "100px").css("position", "relative").css("text-align", "right").css("padding-right","10px");
	labelBox.append($("<div>").addClass("label_h"));
	var label = $("<div>").addClass("label");
		label.append($("<span>").attr("id",elementId+"_labelDiv").css("display","inline").html(labelvalue));
	labelBox.append(label);
	
	var component = $("<div>").addClass("component").css("display", "inline-block").attr("id",elementId);
	component.append("<div class='element_options' style='display: none'>{listeners:[]}</div>");
	var formElement = $("<div>").addClass("component_element").addClass("component_form_element").addClass("omponent_element_calendar");
	formElement.append(labelBox);
	formElement.append(container);
	component.append(formElement);
	return component;
}
