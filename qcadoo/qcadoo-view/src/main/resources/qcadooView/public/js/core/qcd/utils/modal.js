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
QCD.utils = QCD.utils || {};
QCD.utils.Modal = {};

QCD.utils.Modal.zIndex = 3000;

QCD.utils.Modal.createModal = function() {
	
	var dialog = $("<div>").addClass("jqmWindow").width(1000);
	
	var container = $("<div>").css("border", "solid red 0px").width(1000).height(560);
	dialog.append(container);
	
	var iframe = $('<iframe frameborder="0" src="" width="1000" height="560">');
	container.append(iframe);
	
	$("body").append(dialog);
	dialog.jqm({modal: true});
	
	return {
		dialog: dialog,
		container: container,
		iframe: iframe,
		
		showStatic: function(src) {
			this.dialog.jqmShow();
			this.iframe.attr("src", src);
		},
		
		show: function(src, onLoadFunction) {
			this.iframe.hide();
			this.dialog.jqmShow();
			this.dialog.css("z-index", QCD.utils.Modal.zIndex++);
			QCD.components.elements.utils.LoadingIndicator.blockElement(this.dialog);
			this.iframe.load(function() {
				iframe.show();
				onLoadFunction.call(this);
				QCD.components.elements.utils.LoadingIndicator.unblockElement(dialog);
			});
			this.iframe.attr("src", src);
		},
		
		hide: function() {
			iframe.unbind("load");
			this.dialog.jqmHide();
		},
		
		changeSize: function(width, height) {
			this.dialog.css("marginLeft", "-"+(width/2)+"px");	
			this.dialog.width(width);
			this.container.width(width);
			this.container.height(height);
			this.iframe.width(width);
			this.iframe.height(height);
		}
	};
}
