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

QCD.components.elements.TextInput = function (_element, _mainController) {
    $.extend(this, new QCD.components.elements.FormComponent(_element, _mainController));

    var textRepresentation = $("#" + this.elementSearchName + "_text");

    var input = this.input;

    var elementPath = this.elementPath;

    var hasListeners = (this.options.listeners.length > 0) ? true : false;

    var fireOnChangeListeners = this.fireOnChangeListeners;

    var allowOnlyScan = this.options.allowOnlyScan;

    if (allowOnlyScan) {
        this.input.addClass('allowOnlyScan');

        // disable delete and back
        $(this.input).keydown(function (e) {
            if (e.keyCode === 8 || e.keyCode === 46) {
                e.preventDefault();
            }
        });

        $(this.input).bind("cut copy paste", function (e) {
            e.preventDefault();
        });

        // clear previous value
        $(window).bind('scannerDetectionComplete', function (e, data) {
            $(input).removeAttr('value');
            $(input).val(data.string);
            $(input).trigger("change");
        })

        $(this.input).scannerDetection({
            timeBeforeScanTest: 200, // wait for the next character for up to 200ms
            endChar: [13], // be sure the scan is complete if key 13 (enter) is detected
            avgTimeByChar: 40, // it's not a barcode if a character takes longer than 40ms
            //                ignoreIfFocusOn: 'input', // turn off scanner detection if an input has focus
            minLength: 5,
            onComplete: function (barcode, qty) {
            }, // main callback function
            scanButtonKeyCode: 116, // the hardware scan button acts as key 116 (F5)
            scanButtonLongPressThreshold: 5, // assume a long press if 5 or more events come in sequence
            onScanButtonLongPressed: function () {
            }, // callback for long pressing the scan button
            onError: function (string) {
                input.val('');
            },
            onReceive: function () {
            }
        });

    } else {
        this.input.removeClass('allowOnlyScan');
    }

    function constructor(_this) {
        input.change(function () {
            inputDataChanged();
        });
    }

    function inputDataChanged() {
        fireOnChangeListeners("onChange", [input.val()]);
        if (hasListeners) {
            mainController.callEvent("onInputChange", elementPath, null, null, null);
        }
    }

    if (this.options.referenceName) {
        _mainController.registerReferenceName(this.options.referenceName, this);
    }

    this.getComponentData = function () {
        return {
            value: input.val()
        }
    }

    this.setComponentData = function (data) {
        if (data.value) {
            this.input.val(data.value);
            textRepresentation.html(data.value);
        } else {
            this.input.val("");
            textRepresentation.html("-");
        }
    }

    this.setFormComponentEnabled = function (isEnabled) {
        if (this.options.textRepresentationOnDisabled) {
            if (isEnabled) {
                input.show();
                textRepresentation.hide();
            } else {
                input.hide();
                textRepresentation.show();
            }
        }
    }

    this.updateSize = function (_width, _height) {
        var height = _height ? _height - 10 : 40;
        this.input.parent().parent().parent().height(height);
        this.input.parent().parent().height(height);
    }

    constructor(this);
}
