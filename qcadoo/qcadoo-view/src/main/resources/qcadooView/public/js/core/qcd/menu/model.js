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
QCD.menu = QCD.menu || {};

QCD.menu.MenuModel = function (menuStructure) {

    // See 'self-executing constructor' pattern
    if (!(this instanceof QCD.menu.MenuModel)) {
        return new QCD.menu.MenuModel(menuStructure);
    }

    var that = this;

    function addCategory(item, type) {
        if (!item) {
            return;
        }
        var button = new QCD.menu.FirstButton(item, type);
        that.items.push(button);
        that.itemsMap[button.name] = button;
        if (!that.selectedItem) {
            that.selectedItem = button;
            button.selectedItem = button.items[0];
        }
    }

    function initialize() {
        var itemsLen = menuStructure.menuItems.length,
            idx;

        addCategory(menuStructure.homeCategory, QCD.menu.MenuModel.HOME_CATEGORY);

        for (idx = 0; idx < itemsLen; idx++) {
            addCategory(menuStructure.menuItems[idx]);
        }
        addCategory(menuStructure.administrationCategory, QCD.menu.MenuModel.ADMINISTRATION_CATEGORY);
    }

    this.selectedItem = null;
    this.items = [];
    this.itemsMap = {};

    initialize();

};

QCD.menu.MenuModel.HOME_CATEGORY = 1;
QCD.menu.MenuModel.ADMINISTRATION_CATEGORY = 2;
QCD.menu.MenuModel.REGULAR_CATEGORY = 3;

QCD.menu.FirstButton = function (menuItem, menuItemType) {

    // See 'self-executing constructor' pattern
    if (!(this instanceof QCD.menu.FirstButton)) {
        return new QCD.menu.FirstButton(menuItem, menuItemType);
    }

    var that = this;

    function initialize() {
        var itemsLen = menuItem.items.length,
            idx,
            secondButton = {};
        for (idx = 0; idx < itemsLen; idx++) {
            secondButton = new QCD.menu.SecondButton(menuItem.items[idx], that);
            that.itemsMap[secondButton.name] = secondButton;
            that.items.push(secondButton);
        }
    }

    this.type = menuItemType ? menuItemType : QCD.menu.MenuModel.REGULAR_CATEGORY;
    this.name = menuItem.name;
    this.label = menuItem.label;
    this.description = menuItem.description;

    this.element = null;

    this.selectedItem = null;

    this.itemsMap = {};
    this.items = [];

    initialize();

};

QCD.menu.SecondButton = function (menuItem, firstButton) {
    this.name = firstButton.name + "_" + menuItem.name;
    this.label = menuItem.label;
    this.description = menuItem.description;

    this.page = menuItem.page;

    this.element = null;
};