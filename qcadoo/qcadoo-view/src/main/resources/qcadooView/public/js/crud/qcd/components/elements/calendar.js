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

QCD.components.elements.Calendar = function (element, mainController) {
    "use strict";
    
    if (!(this instanceof QCD.components.elements.Calendar)) {
        return new QCD.components.elements.Calendar(element, mainController);
    }
    
	$.extend(this, new QCD.components.elements.FormComponent(element, mainController));
	
	var ANIMATION_LENGTH = 200,
        SECOND_IN_MILLIS = 1000,
	    MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS,
	    HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS,
	    that = this,
	    calendar = $("#" + this.elementSearchName + "_calendar"),
	    timeInput = $("#" + this.elementSearchName + "_timeInput"),
	    input = this.input,
	    withTimePicker = timeInput.length > 0,
	    datepicker = null,
	    datepickerElement = null,
	    elementPath = this.elementPath,
	    opened = false,
	    skipButtonClick = false,
	    isTriggerBootonHovered = false,
	    hasListeners = (this.options.listeners && this.options.listeners.length > 0),
	    isValidationError = false;
	
	if (this.options.referenceName) {
		mainController.registerReferenceName(this.options.referenceName, this);
	}
	
	function checkTimeFormat(inputValue) {
		var fragmentaryValues = [ inputValue.substr(0, 2), inputValue.substr(3, 2), inputValue.substr(6, 2) ],
            fragmentaryValuesLen = fragmentaryValues.length,
            i = 0,
            fragmentValue = null,
            intIndexOfMatch = null;
		
		for (i = 0; i < fragmentaryValuesLen; i++) {
			fragmentValue = fragmentaryValues[i];
			if (fragmentValue.charAt(0) !== '_' && fragmentValue.charAt(1) === '_') {
				fragmentValue = "0" + fragmentValue.charAt(0); 
			} else {
				intIndexOfMatch = fragmentValue.indexOf("_");
				// FIXME MAKU replace below loop with /<pattern>/g regExp
				while (intIndexOfMatch !== -1) {
					fragmentValue = fragmentValue.replace("_", "0");
					intIndexOfMatch = fragmentValue.indexOf("_");
				}
			}
			fragmentaryValues[i] = fragmentValue;
		}
		timeInput.val(fragmentaryValues.join(":"));
	}
	
	function getNormalizedDate(date) {
        date = $.trim(date);
        if (date.length === 10) {
            return date;
        } else if (date.length === 16 || date.length === 19) {
            return date.substring(0, 10);
        } else {
            return '';
        }
    }
    
    function getNormalizedTime(date) {
        var res = null;
        date = $.trim(date);
        if (withTimePicker) {
            if (date.length === 16) {
                res = date.substring(11, 16) + ':00';
            } else if (date.length === 19) {
                res = date.substring(11, 19);
            } else {
                res = '00:00:00';
            }
        }
        return res;
    }
	
	this.setComponentData = function (data) {
		if (data.value) {
			this.input.val(getNormalizedDate(data.value));
			if (withTimePicker) {
				timeInput.val(getNormalizedTime(data.value));
			}
		} else {
			this.input.val("");
			if (withTimePicker) {
				timeInput.val("");
			}
		}
	};
	
	this.getComponentData = function () {
		if (withTimePicker) {
			return {
				value : this.input.val() ? (this.input.val() + ' ' + (timeInput.val() ? timeInput.val() : '00:00:00')) : '' 
			};
		} else {
			return {
				value : this.input.val()
			};
		}
	};
	
    function getTimeInMillis(timeString) {
        var timeInMillis = 0,
            parts;
        timeString = $.trim(timeString);
        if (timeString !== "") {
            parts = timeString.split(':');
            if (parts.length !== 3 || parts[0].length !== 2 || parts[1].length !== 2 || parts[2].length !== 2) {
                QCD.error("Can't parse time from string: '" + timeString + "'");
                return null;
            }    
            try {
                timeInMillis = parseInt(parts[0], 10) * HOUR_IN_MILLIS + parseInt(parts[1], 10) * MINUTE_IN_MILLIS + parseInt(parts[2], 10) * SECOND_IN_MILLIS;
            } catch (e) {
                QCD.error("Can't parse time from string: '" + timeString + "'");
                return null;
            }
        }
        return timeInMillis;
    }
    
    function parseDate() {
        var dateString = input.val(),
            parts,
            date,
            timeInMillis = getTimeInMillis(timeInput.val());
        if ($.trim(dateString) === "") {
            return null;
        }
        parts = dateString.split("-");
        if (parts.length !== 3 || parts[0].length !== 4 || parts[1].length !== 2 || parts[2].length !== 2) {
            throw "Can't build date from string: '" + dateString + "'";
        }
        date = new Date(dateString);
        if (timeInMillis) {
            date = new Date(date.getTime() + timeInMillis);
        }
        
        return date;
    }
    
    function getDate() {
        try {
            return parseDate();
        } catch (e) {
            QCD.error(e);
            return null;
        }
    }
    this.getDate = getDate;

	function inputDataChanged() {
	    var hasParseError = false,
	        date = null;
	    try {
            date = parseDate();
		} catch (e) {
		    QCD.error(e);
            hasParseError = true;  
		}
		if (!isValidationError) {
			if (hasParseError) {
				that.addMessage({
					title: "",
					content: ""
				});
				element.addClass("error");
			} else {
                element.removeClass("error");
			}
		}
		that.fireOnChangeListeners("onChange", [date]);
		if (hasListeners) {
			mainController.callEvent("onChange", elementPath, null, null, null);
		}
	}
	
	this.setComponentError = function (isError) {
		isValidationError = isError;
		if (isError) {
			element.addClass("error");
		} else {
			element.removeClass("error");
		}
	};
	
	this.setFormComponentEnabled = function (isEnabled) {
		if (isEnabled) {
			calendar.addClass("enabled");
			input.datepicker("enable");
			input.mask("2999-19-39");
			if (withTimePicker) {
				timeInput.mask("29:69:69");
				timeInput.removeAttr("disabled");
			}
			input.removeAttr("disabled");
		} else {
			calendar.removeClass("enabled");
			input.datepicker("disable");
			input.unmask();
			input.attr("disabled", "disabled");
			if (withTimePicker) {
				timeInput.unmask();
				timeInput.attr("disabled", "disabled");
			}
		}
	};
	
	this.updateSize = function (width, height) {
		height = height ? height - 10 : 40;
		this.input.parent().parent().parent().parent().parent().height(height);
	};
	
	this.setDate = function (date) {
		var dateString = $.datepicker.formatDate("yy-mm-dd", date),
            timeString = null;
		
		input.val(dateString);
		if (withTimePicker) {
			timeString = getNormalizedTime(date);
			timeInput.val(timeString);
		}
		inputDataChanged();
	};
	
	function constructor() {
        var containerElement = element,
            closestWindowContent = containerElement.closest("div.windowContent"),
            closestWindowContainerBody = containerElement.closest("div.windowContainerContentBody"),
            options = null;
        
        $.mask.definitions['1'] = '[0-1]';
        $.mask.definitions['2'] = '[0-2]';
        $.mask.definitions['3'] = '[0-3]';
        $.mask.definitions['6'] = '[0-5]';
        
        options = $.datepicker.regional['zh-CN'];
        if (!options) {
            options = $.datepicker.regional[''];
        }
        
        options.changeMonth = true;
        options.changeYear = true;
        options.showOn = 'button';
        options.dateFormat = 'yy-mm-dd';
        options.showAnim = 'show';
        options.altField = input;
        options.onClose = function (dateText, inst) {
            opened = false;
            if (isTriggerBootonHovered) {
                skipButtonClick = true;
            }
        };
        options.onSelect = function (dateText, inst) {
            datepickerElement.slideUp(ANIMATION_LENGTH);
            opened = false;
            inputDataChanged();
        };
        
        datepickerElement = $("<div>").css("position", "absolute").css("zIndex", 300).css("right", "15px").css("line-height", "14px");
        containerElement.css("position", "relative");
        datepickerElement.hide();
        
        containerElement.append(datepickerElement);
        
        datepickerElement.datepicker(options);
        
        input.val("");
        timeInput.val("");
        
        $(document).mousedown(function (event) {
            var target = null;
            if (!opened) {
                return;
            }
            target = $(event.target);
            if (target.attr("id") !== input.attr("id") && 
                target.attr("id") !== calendar.attr("id") && 
                target.parents('.ui-datepicker').length === 0) {
                
                datepickerElement.slideUp(ANIMATION_LENGTH);
                opened = false;
            }
        });
        
        calendar.hover(function () {
                isTriggerBootonHovered = true;
            }, function () {
                isTriggerBootonHovered = false;
            });
        calendar.click(function () {
            if (calendar.hasClass("enabled")) {
                if (skipButtonClick) {
                    skipButtonClick = false;
                    return;
                }
                if (!opened) {
                    
                    if (input.val()) {
                        try {
                            $.datepicker.parseDate("yy-mm-dd", input.val());
                            datepickerElement.datepicker("setDate", input.val());
                        } catch (e) {
                            // do nothing
                        }
                    }
                    
                    var top = input.offset().top + closestWindowContainerBody.scrollTop(),
                        calendarHeight = datepickerElement.outerHeight(),
                        inputHeight = input.outerHeight() + 10,
                        viewHeight = closestWindowContent.outerHeight(),
                        ribbonHeight = closestWindowContent.offset().top;
                    
                    if (top - 5 - calendarHeight > ribbonHeight && top + inputHeight + 5 + calendarHeight > viewHeight) {
                        datepickerElement.css("top", "");
                        datepickerElement.css("bottom", "0px");
                    } else {
                        datepickerElement.css("top", inputHeight + 5 + "px");
                        datepickerElement.css("bottom", "");
                    }
                    
                    datepickerElement.slideDown(ANIMATION_LENGTH).show();
                    opened = true;
                } else {
                    datepickerElement.slideUp(ANIMATION_LENGTH);
                    opened = false;
                }
            }
        });
        
        input.focus(function () {
            calendar.addClass("lightHover");
        }).blur(function () {
            calendar.removeClass("lightHover");
        });
        
        timeInput.focus(function () {
        }).blur(function () {
            checkTimeFormat(timeInput.val());
        });
        
        timeInput.change(function () {
            inputDataChanged();
        });
        
        input.change(function () {
            inputDataChanged();
        });
        
        $("#ui-datepicker-div").hide();
    }
	
	constructor();
};
