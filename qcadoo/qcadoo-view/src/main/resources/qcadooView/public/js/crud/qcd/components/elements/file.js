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

QCD.components.elements.File = function(_element, _mainController) {
	$.extend(this, new QCD.components.elements.FormComponent(_element, _mainController));
	
	var input = this.input;
	
	var link = $("#"+this.elementSearchName+"_fileList");
	
	var thumbnail = $("#"+this.elementSearchName+"_thumbnail");
	
	var modificationDate = $("#"+this.elementSearchName+"_fileLastModificationDate");
	
	var fileButton = $("#"+this.elementSearchName+"_fileButton");
	var fileDeleteButton = $("#"+this.elementSearchName+"_deleteButton");
	
	var elementPath = this.elementPath;
	
	var hasListeners = (this.options.listeners.length > 0) ? true : false;
	
	var _this = this;
	
	var translations = this.options.translations;
	
	var isEnabled = false;
	var fileNameValue = null;
	
	function constructor(_this) {
		input.change(function() {
			inputDataChanged();
		});
		
		fileButton.click(openFileWindow);
		fileDeleteButton.click(deleteLink);
	}
	
	function inputDataChanged() {
		if (hasListeners) {
			mainController.callEvent("onInputChange", elementPath, null, null, null);
		}
	}
	
	if (this.options.referenceName) {
		_mainController.registerReferenceName(this.options.referenceName, this);
	}
	
	this.getComponentData = function() {
		return {
			value : input.val()
		}
	}
	
	this.setComponentData = function(data) {
		if (data.value) {
			setData(data.value, data.fileLastModificationDate, data.fileUrl, data.fileName);
		} else {
			setData("", "", "#", "");
		}
	}
	
	this.setFormComponentEnabled = function(_isEnabled) {
		isEnabled = _isEnabled;
		updateButtons();
	}
	
	function deleteLink() {
		if (! fileDeleteButton.hasClass("enabled")) {
			return;
		}
		var confirmText = translations.deleteConfirm + " '" + fileNameValue + "'?";
		if (confirm(confirmText)) {
			setData("", "", "#", "");
		}
	}
	
	this.updateSize = function(_width, _height) {
		var height = _height ? _height-10 : 40;
		this.input.parent().parent().parent().height(height);
		this.input.parent().parent().height(height);
	}
	
	function openFileWindow() {
		if (! fileButton.hasClass("enabled")) {
			return;
		}
		mainController.openModal(elementPath+"_fileUploadWindow", "../fileUpload.html", false, onModalClose);
	}
	
	function onModalClose(response) {
		if (!response) {
			return;
		}
		var status = JSON.parse(response);
		if (status.fileUploadError) {
			showMessage("error", translations.uploadErrorHeader, translations.uploadErrorContent);
			return;
		}
		if (status.fileName && status.fileName != "") {
			fileNameValue = status.fileName;
			showMessage("success", translations.uploadSuccessHeader, translations.uploadSuccessContent + " '" + status.fileName + "'");
		}
		setData(status.filePath, status.fileLastModificationDate, status.fileUrl, status.fileName);
	}
	
	function showMessage(type, title, content) {
		mainController.showMessage({
			type: type,
			title: title,
			content: content
		});
	}
	
	function setData(filePath, fileLastModificationDate, fileUrl, fileName) {
		input.val(filePath);
		if (fileLastModificationDate && fileLastModificationDate != "") {
			modificationDate.text("(" + fileLastModificationDate + ")");
		} else {
			modificationDate.text("");
		}
		if (filePath && filePath != null) {
			fileNameValue = fileName;
			link.attr("title", fileName);
		} else {
			fileNameValue = null;
			link.attr("title", "");
		}
		link.attr("href", fileUrl);
		link.text(fileName);
		if(thumbnail){
			if(fileUrl == "#"){
                thumbnail.attr("src", "/qcadooView/public/img/core/no_pic.jpg"); <!--ricefishDev-->
			}else {
                thumbnail.attr("src", fileUrl);
			}
        }
		updateButtons();
	}
	
	function updateButtons() {
		if (isEnabled) {
			fileButton.addClass("enabled");
			fileButton.attr("title", translations.uploadButton);
			if (fileNameValue != null) {
				fileDeleteButton.addClass("enabled");
				fileDeleteButton.attr("title", translations.deleteButton);
			} else {
				fileDeleteButton.removeClass("enabled");
				fileDeleteButton.attr("title", "");
			}
		} else {
			fileButton.removeClass("enabled");
			fileButton.attr("title", "");
			fileDeleteButton.removeClass("enabled");
			fileDeleteButton.attr("title", "");
		}
	}
	
	constructor(this);
}