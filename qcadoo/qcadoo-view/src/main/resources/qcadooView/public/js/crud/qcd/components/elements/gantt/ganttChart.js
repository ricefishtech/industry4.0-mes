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

QCD.components.elements.GanttChart = function (_element, _mainController) {
    $.extend(this, new QCD.components.elements.FormComponent(_element, _mainController));

    var element = _element;

    var mainController = _mainController;

    var htmlElements = {};

    var _this = this;

    var constants = {
        CELL_WIDTH: 25,
        CELL_HEIGHT: 30,
        HEADER_HEIGHT: 60,
        ROW_NAMES_WIDTH: 200,
        RIGHT_SCROLL_WIDTH: 17,
        BOTTOM_SCROLL_HEIGHT: 16
    };

    var currentCellSettings;

    var stripsOrientation;
    var itemsBorderWidth;
    var itemsBorderColor;

    var rowsByName = {};
    var rowsByIndex = [];

    var currentWidth;
    var isVScrollVisible = false;
    var isHScrollVisible = false;

    var header;

    var selectedItem;

    var collisionInfoBoxContent;
    var collisionInfoBoxOverlay;

    var ganttTooltip;

    function constructor() {
        createGantt();
        createGanttTooltip();
        QCD.components.elements.utils.LoadingIndicator.blockElement(element);
        header.init();
    }

    this.getComponentValue = function () {
        var headerParameters = header.getCurrentParameters();
        var data = {
            headerParameters: headerParameters
        };
        if (selectedItem && selectedItem[0] && selectedItem[0].entityId) {
            data.selectedEntityId = selectedItem[0].entityId;
        }
        return data;
    };

    this.setComponentValue = function (value) {
        applySettings(value);
        header.enableButtons();
        header.setDateFromValue(value.dateFrom, value.dateFromErrorMessage);
        header.setDateToValue(value.dateTo, value.dateToErrorMessage);
        header.setDateToValue(value.dateTo, value.dateToErrorMessage);
        header.setGlobalErrorMessage(value.globalErrorMessage);
        if (value.selectedEntityId) {
            if (selectedItem) {
                selectedItem.removeClass("ganttItemSelected");
            }
            var newSelectedItem = $("#" + _this.elementSearchName + "_item_" + value.selectedEntityId);
            selectedItem = newSelectedItem;
            newSelectedItem.addClass("ganttItemSelected");
        }
        collisionInfoBoxOverlay.hide();
        QCD.components.elements.utils.LoadingIndicator.unblockElement(element);
    };

    this.performInitialize = function () {
        refreshContent();
    };

    this.setComponentLoading = function (isLoadingVisible) {
        if (isLoadingVisible) {
            QCD.components.elements.utils.LoadingIndicator.blockElement(element);
            header.disableButtons();
        } else {
            QCD.components.elements.utils.LoadingIndicator.unblockElement(element);
            header.enableButtons();
        }
    };

    this.onScaleChanged = function (newScale) {
        header.setCurrentScale(newScale);
        refreshContent();
    };

    this.onDateChanged = function () {
        refreshContent();
    };

    function onSelectChange() {
        if (_this.options.listeners.length > 0) {
            mainController.callEvent("select", _this.elementPath, null);
        }
    }

    function refreshContent() {
        QCD.components.elements.utils.LoadingIndicator.blockElement(element);
        mainController.callEvent("refresh", _this.elementPath);
    }


    function applySettings(cellSettings) {
        if (cellSettings.globalErrorMessage) {
            return;
        }
        if (!cellSettings.scale) {
            return;
        }

        updateHeader(cellSettings);

        stripsOrientation = cellSettings.stripsOrientation;
        itemsBorderWidth = cellSettings.itemsBorderWidth || 1;
        itemsBorderColor = cellSettings.itemsBorderColor || "silver";

        htmlElements.rowNamesConteiner.children().remove();
        htmlElements.rowsContainer.children().remove();

        var contentWidth = constants.CELL_WIDTH * getTotalNumberOfCells(cellSettings);
        htmlElements.topRow1.width(contentWidth);
        htmlElements.topRow2.width(contentWidth);
        htmlElements.rowsContainer.width(contentWidth);

        rowsByName = {};
        rowsByIndex = [];
        for (var i = 0; i < cellSettings.rows.length; i++) {
            rowsByIndex[i] = addRow(cellSettings, cellSettings.rows[i]);
        }
        updateScroll();

        updateItems(cellSettings.items, cellSettings.collisions);

        currentCellSettings = cellSettings;
    }

    function updateItems(items, collisions) {
        for (var itemIndex in items) {
            var item = items[itemIndex];
            addItem(item, false);
        }
        for (var itemIndex in collisions) {
            var item = collisions[itemIndex];
            addItem(item, true);
        }
    }

    function showCollisionBox(ganttItem) {
        collisionInfoBoxContent.children().remove();
        for (var i = 0; i < ganttItem.items.length; i++) {
            var collisionItem = ganttItem.items[i];

            var collisionItemElement = $("<div>").addClass("collisionInfoBoxItem").html(collisionItem.info.name);
            collisionItemElement.attr("id", _this.elementPath + "_collisionItem_" + collisionItem.id);
            collisionItemElement[0].entityId = collisionItem.id;

            collisionItemElement.click(function () {
                var itemElement = $(this);
                var itemId = this.entityId;
                if (selectedItem) {
                    selectedItem.removeClass("ganttItemSelected");
                    $("#" + _this.elementSearchName + "_collisionItem_" + selectedItem[0].entityId).removeClass("ganttItemSelected");
                }
                var ganttElement = $("#" + _this.elementSearchName + "_item_" + itemId);
                selectedItem = ganttElement;
                itemElement.addClass("ganttItemSelected");
                ganttElement.addClass("ganttItemSelected");
                onSelectChange();
            });

            collisionInfoBoxContent.append(collisionItemElement);
        }
        collisionInfoBoxOverlay.show();
        if (selectedItem) {
            $("#" + _this.elementSearchName + "_collisionItem_" + selectedItem[0].entityId).addClass("ganttItemSelected");
        }
    }

    function createGantt() {
        header = new QCD.components.elements.GanttChartHeader(_this, _this.elementPath + "_header", _this.options.translations, _this.options);
        element.append(header.getHeaderElement());

        htmlElements.wrapper = $("<div>").addClass("ganttContainer");
        element.append(htmlElements.wrapper);

        htmlElements.rowNamesWrapper = $("<div>").addClass("ganttRowNamesWrapper");
        htmlElements.rowNamesWrapper.width(constants.ROW_NAMES_WIDTH - 1);
        htmlElements.rowNamesWrapper.height("100%");
        htmlElements.wrapper.append(htmlElements.rowNamesWrapper);

        htmlElements.centerContainer = $("<div>").addClass("ganttCenterConteiner");
        htmlElements.centerContainer.css("left", constants.ROW_NAMES_WIDTH + "px");
        htmlElements.wrapper.append(htmlElements.centerContainer);

        // ROW NAMES
        htmlElements.rowNamesButtonsConteiner = $("<div>").addClass("ganttRowNamesButtonsConteiner");
        htmlElements.rowNamesButtonsConteiner.height(constants.HEADER_HEIGHT - 1);
        htmlElements.rowNamesWrapper.append(htmlElements.rowNamesButtonsConteiner);

        htmlElements.rowNamesConteiner = $("<div>").addClass("ganttRowNamesConteiner");
        htmlElements.rowNamesWrapper.append(htmlElements.rowNamesConteiner);

        // CENTER
        htmlElements.topRow = $("<div>").addClass("ganttTopRow");
        htmlElements.topRow.height(constants.HEADER_HEIGHT - 1);
        htmlElements.centerContainer.append(htmlElements.topRow);

        htmlElements.topRow1 = $("<div>").addClass("ganttTopRow1");
        htmlElements.topRow1.height((constants.HEADER_HEIGHT / 2) - 1);
        htmlElements.topRow1.css("line-height", ((constants.HEADER_HEIGHT / 2) - 1) + "px");
        htmlElements.topRow.append(htmlElements.topRow1);
        htmlElements.topRow2 = $("<div>").addClass("ganttTopRow2");
        htmlElements.topRow2.height((constants.HEADER_HEIGHT / 2));
        htmlElements.topRow2.css("line-height", ((constants.HEADER_HEIGHT / 2) - 1) + "px");
        htmlElements.topRow.append(htmlElements.topRow2);

        htmlElements.rowsContainerWrapper = $("<div>").addClass("rowsContainerWrapper");
        htmlElements.centerContainer.append(htmlElements.rowsContainerWrapper);

        htmlElements.rowsContainer = $("<div>").addClass("rowsContainer");
        htmlElements.rowsContainerWrapper.append(htmlElements.rowsContainer);

        // SCROLL
        htmlElements.rowsContainerWrapper.scroll(function (eventObject) {
            var scrollLeft = htmlElements.rowsContainerWrapper.scrollLeft();
            htmlElements.topRow.scrollLeft(scrollLeft);

            var scrollTop = htmlElements.rowsContainerWrapper.scrollTop();
            htmlElements.rowNamesConteiner.scrollTop(scrollTop);
        });

        var collisionInfoBox = $("<div>").addClass("collisionInfoBox").click(function () {
            return false;
        });
        var collisionInfoBoxWrapper = $("<div>").addClass("collisionInfoBoxWrapper");
        collisionInfoBoxOverlay = $("<div>").addClass("collisionInfoBoxOverlay").click(function () {
            $(this).hide()
        });
        collisionInfoBoxOverlay.append(collisionInfoBoxWrapper);
        collisionInfoBoxWrapper.append(collisionInfoBox);
        element.css("position", "relative");
        element.append(collisionInfoBoxOverlay);

        var collisionInfoBoxHeader = $("<div>").addClass("collisionInfoBoxHeader").html(_this.options.translations["colisionBox.header"]);
        var closeButton = $("<div>").addClass("collisionInfoBoxHeaderCloseButton").attr("title", _this.options.translations["colisionBox.closeButton"]);
        closeButton.click(function () {
            collisionInfoBoxOverlay.hide();
        });
        collisionInfoBoxHeader.append(closeButton);

        collisionInfoBoxContent = $("<div>").addClass("collisionInfoBoxContent");
        collisionInfoBox.append(collisionInfoBoxHeader);
        collisionInfoBox.append(collisionInfoBoxContent);
    }

    function createGanttTooltip() {
        ganttTooltip = new QCD.components.elements.GanttChartTooltip(element);
    }

    function setVerticalScrollVisible(visible) {
        isVScrollVisible = visible;
        var topRowWidth = currentWidth - constants.ROW_NAMES_WIDTH;
        if (visible) {
            topRowWidth -= constants.RIGHT_SCROLL_WIDTH;
        }
        htmlElements.topRow.width(topRowWidth);
    }

    function setHorizontalScrollVisible(visible) {
        isHScrollVisible = visible;
        var rowNamesHeight = htmlElements.rowNamesWrapper.height() - htmlElements.topRow.height() - 1;
        if (visible) {
            rowNamesHeight -= constants.BOTTOM_SCROLL_HEIGHT;
        }
        htmlElements.rowNamesConteiner.height(rowNamesHeight);
    }

    function updateHeader(cellSettings) {
        header.setCurrentScale(cellSettings.zoomLevel);
        htmlElements.topRow1.children().remove();
        htmlElements.topRow2.children().remove();
        for (var i = 0; i < cellSettings.scale.categories.length; i++) {
            var categoryCellsNumber = getCategoryCellsNumber(cellSettings, i);

            var cellElement = $("<div>").height("100%").width((categoryCellsNumber * constants.CELL_WIDTH) - 1).addClass("ganttTopRowElement").addClass("ganttTopRowElementEnd");
            cellElement.html(cellSettings.scale.categories[i]);

            htmlElements.topRow1.append(cellElement);

            var labelNumber = cellSettings.scale.elementLabelInitialNumber ? cellSettings.scale.elementLabelInitialNumber : 0;
            var bottomElement = null;
            for (var bottomI = 0; bottomI < categoryCellsNumber; bottomI++) {
                bottomElement = $("<div>").height(htmlElements.topRow2.height() - 1).width(constants.CELL_WIDTH - 1).addClass("ganttTopRowElement");
                var bottomElementContent = null;
                var moveLabelLeft = false;
                if (cellSettings.scale.elementLabelsValues) {
                    if (i == 0 && cellSettings.scale.firstCategoryFirstElement) {
                        bottomElementContent = cellSettings.scale.elementLabelsValues[bottomI + cellSettings.scale.firstCategoryFirstElement - 1];
                    } else {
                        bottomElementContent = cellSettings.scale.elementLabelsValues[bottomI];
                    }
                } else {
                    bottomElementContent = labelNumber;
                    moveLabelLeft = true;
                }
                if (moveLabelLeft) {
                    bottomElement.html("<div>" + bottomElementContent + "</div>");
                } else {
                    bottomElement.html(bottomElementContent);
                }
                htmlElements.topRow2.append(bottomElement);
                labelNumber += cellSettings.scale.elementLabelsInterval;
            }
            bottomElement.addClass("ganttTopRowElementEnd");
        }
    }

    function getCategoryCellsNumber(cellSettings, i) {
        if (cellSettings.scale.elementsInCategory instanceof Array) {
            return cellSettings.scale.elementsInCategory[i];
        } else {
            if (cellSettings.scale.categories.length == 1 && cellSettings.scale.firstCategoryFirstElement && cellSettings.scale.lastCategoryLastElement) {
                return cellSettings.scale.lastCategoryLastElement - cellSettings.scale.firstCategoryFirstElement + 1;
            } else if (i == 0 && cellSettings.scale.firstCategoryFirstElement) {
                return cellSettings.scale.elementsInCategory - cellSettings.scale.firstCategoryFirstElement + 1;
            } else if (i == cellSettings.scale.categories.length - 1 && cellSettings.scale.lastCategoryLastElement) {
                return cellSettings.scale.lastCategoryLastElement;
            } else {
                return cellSettings.scale.elementsInCategory;
            }
        }
    }

    function getTotalNumberOfCells(cellSettings) {
        var totalCellsNumber = 0;
        for (var i = 0; i < cellSettings.scale.categories.length; i++) {
            totalCellsNumber += getCategoryCellsNumber(cellSettings, i);
        }
        return totalCellsNumber;
    }

    function addRow(cellSettings, rowName) {
        var rowNameElement = $("<div>").height(constants.CELL_HEIGHT - 1).addClass("ganttRowNameElement");
        rowNameElement.css("line-height", (constants.CELL_HEIGHT - 1) + "px");
        rowNameElement.html(rowName);
        htmlElements.rowNamesConteiner.append(rowNameElement);

        var rowElement = $("<div>").height(constants.CELL_HEIGHT - 1).addClass("ganttRowElement");
        htmlElements.rowsContainer.append(rowElement);

        for (var i = 0; i < cellSettings.scale.categories.length; i++) {
            var categoryCellsNumber = getCategoryCellsNumber(cellSettings, i);
            var cellElement;
            for (var bottomI = 0; bottomI < categoryCellsNumber; bottomI++) {
                cellElement = $("<div>").height(constants.CELL_HEIGHT - 1).width(constants.CELL_WIDTH - 1).addClass("ganttCellElement");
                cellElement.attr("id", "ganttCellElement_" + i + "_" + bottomI);
                rowElement.append(cellElement);
            }
            cellElement.addClass("ganttTopRowElementEnd");
        }
        rowsByName[rowName] = rowElement;
        return rowElement;
    }

    function addItem(item, isCollision) {
        var row = rowsByName[item.row];
        var itemElement = $("<div>").addClass("ganttItem");
        itemElement.css("line-height", (constants.CELL_HEIGHT - 4 - (2 * itemsBorderWidth)) + "px");
        var left = (constants.CELL_WIDTH * item.from);
        var right = (constants.CELL_WIDTH * item.to);
        var width = right - left;
        itemElement.width(width - (2 * itemsBorderWidth) + 1);
        itemElement.height(constants.CELL_HEIGHT - 3 - (2 * itemsBorderWidth));
        itemElement.css("top", "1px");
        itemElement.css("left", (left - 1) + "px");
        itemElement.css("border-width", itemsBorderWidth + "px");
        itemElement.css("border-color", itemsBorderColor);
        row.append(itemElement);
        if (item.type) {
            itemElement.addClass("ganttItemType_" + item.type);
        }

        if (item.strips) {
            for (var stripIdx = 0; stripIdx < item.strips.length; stripIdx++) {
                var strip = item.strips[stripIdx];
                itemElement.append(createStripElement(strip));
            }
        }

        var itemElementContent = $("<div>").addClass("ganttItemContent");

        if (isCollision) {
            itemElement.addClass("ganttCollisionItem");
            if (width > 30) {
                itemElement.addClass("withIcon");
                itemElementContent.html(_this.options.translations["colisionElementName"]);
                itemElementContent.shorten({width: width, tail: "...", tooltip: false});
            } else if (width > 15) {
                itemElement.addClass("withIcon");
            }
            itemElement[0].isCollision = true; // add isCollision to DOM element
            itemElement[0].ganttItem = item; // add item element to DOM
        } else {
            if (width > 30) {
                itemElementContent.html(item.info.name);
                itemElementContent.shorten({width: width, tail: "...", tooltip: false});
            }
        }
        itemElement.append(itemElementContent);

        function createTooltipContent(item) {
            var tooltip = item.info.tooltip,
                description = "",
                content = tooltip.content || [],
                contentLen = content.length,
                i;
            if (typeof tooltip.header === 'string') {
                description += "<div class='ganttItemDescriptionName'>" + tooltip.header + "</div>"
            }
            for (i = 0; i < contentLen; i++) {
                description += "<div class='ganttItemDescriptionInfo'>" + content[i] + "</div>";
            }
            description += "<div class='ganttItemDescriptionInfo'>";
            description += "<div class='ganttItemDescriptionLabel'>" + _this.options.translations["description.dateFrom"] + "</div>";
            description += "<div class='ganttItemDescriptionValue'>" + item.info.dateFrom + "</div></div>";
            description += "<div class='ganttItemDescriptionInfo'>";
            description += "<div class='ganttItemDescriptionLabel'>" + _this.options.translations["description.dateTo"] + "</div>";
            description += "<div class='ganttItemDescriptionValue'>" + item.info.dateTo + "</div></div>";
            return description;
        }

        if (_this.options.hasPopupInfo) {
            itemElement.bind({
                "mousemove": function (eventObj) {
                    ganttTooltip.show(eventObj.clientX, eventObj.clientY, createTooltipContent(item));
                },
                "mouseleave": function (eventObj) {
                    ganttTooltip.hide();
                }
            });
        }

        if (item.id || isCollision) {
            itemElement.attr("id", _this.elementPath + "_item_" + item.id);
            itemElement[0].entityId = item.id; // add entityId to DOM element
            itemElement.css("cursor", "pointer");
            itemElement.click(function () {
                if (this.isCollision) {
                    showCollisionBox(this.ganttItem);
                    return;
                }
                var itemElement = $(this);
                if (selectedItem) {
                    selectedItem.removeClass("ganttItemSelected");
                }
                selectedItem = itemElement;
                itemElement.addClass("ganttItemSelected");
                onSelectChange();
            });
        }

        itemElement.mouseover(function () {
            $(this).addClass("ganttItemHovered");
        }).mouseout(function () {
            $(this).removeClass("ganttItemHovered");
        });
    }

    function createStripElement(strip) {
        var stripElement = $("<div>").addClass("ganttItemStrip");
        if (stripsOrientation == 'vertical') {
            stripElement.addClass("verticalStrip");
            stripElement.css("height", strip.size + '%');
        } else {
            stripElement.addClass("horizontalStrip");
            stripElement.css("width", strip.size + '%');
        }
        stripElement.css("background-color", strip.color);
        return stripElement;
    }

    this.updateSize = function (_width, _height) {
        _width = _width - 22;
        _height = _height - 50;
        currentWidth = _width;
        htmlElements.wrapper.width(_width);
        htmlElements.wrapper.height(_height);
        var centerAreaWidth = _width - constants.ROW_NAMES_WIDTH;
        var topRowWidth = centerAreaWidth;
        if (isVScrollVisible) {
            topRowWidth = topRowWidth - constants.RIGHT_SCROLL_WIDTH - 1;
        }
        htmlElements.centerContainer.width(centerAreaWidth);
        htmlElements.topRow.width(topRowWidth);
        htmlElements.rowsContainerWrapper.width(centerAreaWidth);

        htmlElements.centerContainer.height(_height);
        htmlElements.rowNamesConteiner.height(_height - constants.HEADER_HEIGHT - constants.BOTTOM_SCROLL_HEIGHT);
        htmlElements.rowsContainerWrapper.height(_height - constants.HEADER_HEIGHT);// - constants.BOTTOM_SCROLL_HEIGHT - 2);
        updateScroll();
    };

    function updateScroll() {
        setVerticalScrollVisible(rowsByIndex.length * constants.CELL_HEIGHT > htmlElements.rowsContainerWrapper.height());
        setHorizontalScrollVisible(htmlElements.rowsContainer.width() > htmlElements.rowsContainerWrapper.width());
    }

    constructor();
};

QCD.components.elements.GanttChartTooltip = function (_element) {

    var element = _element;

    var visible = false;

    var htmlElements = {};

    function constructor() {
        createTooltip();
    }

    function createTooltip() {
        htmlElements.tooltipElement = $("<div>").addClass("ganttChartTooltip");
        htmlElements.tooltipElement.css("opacity", "0");
        htmlElements.tooltipElement.css("position", "fixed");
        htmlElements.tooltipElement.css("z-index", "100");
        htmlElements.tooltipElement.css("top", "-1000px");

        htmlElements.tooltipBodyWrapper = $("<div>").addClass("ganttChartTooltipBody");

        htmlElements.tooltipElement.append(htmlElements.tooltipBodyWrapper);
        element.append(htmlElements.tooltipElement);
    }

    this.show = function (x, y, body) {
        if (!visible) {
            htmlElements.tooltipBodyWrapper.html(body);
        }
        var position = calculatePosition(x, y);
        htmlElements.tooltipElement.css("left", position.x).css("top", position.y);
        if (!visible) {
            visible = true;
            htmlElements.tooltipElement.animate({
                opacity: 0.8
            }, {
                duration: 100,
                queue: false
            });
        }
    };

    this.hide = function () {
        htmlElements.tooltipElement.stop().css("opacity", "0.0").css("top", "-1000px");
        visible = false;
    };

    function calculatePosition(x, y) {
        var spacing = {
            top: 20,
            right: 40,
            bottom: 20,
            left: 20
        };

        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var tooltipWidth = htmlElements.tooltipElement.width();
        var tooltipHeight = htmlElements.tooltipElement.height();

        var calcX = x - (tooltipWidth / 2);
        var calcY = y + 20;

        if (calcX < spacing.left) {
            calcX = spacing.left;
        } else if (calcX + tooltipWidth > windowWidth - spacing.right) {
            calcX = windowWidth - tooltipWidth - spacing.right;
        }

        if (calcY + tooltipHeight > windowHeight - spacing.bottom) {
            calcY = y - tooltipHeight - 20;
        }

        return {
            x: calcX,
            y: calcY
        }
    }

    constructor();
};
