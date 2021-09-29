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
QCD.components.elements.grid = QCD.components.elements.grid || {};

QCD.components.elements.grid.GridHeaderController = function (_gridController, _mainController, _gridParameters, _translations) {

    var gridController = _gridController;
    var mainController = _mainController;
    var gridParameters = _gridParameters;
    var translations = _translations;

    var deleteButtonEnabled = true;

    var cookieExpires = 30 * 12 * 2;

    var pagingVars = new Object();
    pagingVars.first = null;
    pagingVars.max = null;
    pagingVars.totalNumberOfEntities = null;

    var headerElement;
    var footerElement;

    var headerElements = new Object();
    headerElements.filterButton = null;
    headerElements.multiSearchButton = null;
    headerElements.predefiniedFiltersCombo = null;
    headerElements.predefiniedFiltersCustomOption_line1 = $("<option>").attr("value", -1).html("--------------").css("display", "none");
    headerElements.predefiniedFiltersCustomOption_line2 = $("<option>").attr("value", -1).html(translations.customPredefinedFilter).css("display", "none");
    headerElements.newButton = null;
    headerElements.addExistingButton = null;
    headerElements.deleteButton = null;
    headerElements.upButton = null;
    headerElements.downButton = null;

    var headerPagingController = null;
    var footerPagingController = null;

    var multiSearchDialog = null;

    var entitiesNumberSpan;

    var enabled = false;
    var rowIndex = null;
    var multiselectMode = false;

    var autoRefreshActive = false;
    var autoRefreshIntervalId;
    var idleIntervalId;
    var idleTimer = 0;

    function constructor(_this) {
        pagingVars.first = 0;
        pagingVars.max = $.cookie("page_size") ? $.cookie("page_size") : 30;
        pagingVars.totalNumberOfEntities = 0;
    }

    function paging_refresh() {
        if (gridParameters.paging) {
            var pagesNo = Math.ceil(pagingVars.totalNumberOfEntities / pagingVars.max);
            if (pagesNo == 0) {
                pagesNo = 1;
            }
            var currPage = Math.ceil(pagingVars.first / pagingVars.max);
            if (pagingVars.first % pagingVars.max == 0) {
                currPage += 1;
            }
            headerPagingController.setPageData(currPage, pagesNo, pagingVars.max);
            footerPagingController.setPageData(currPage, pagesNo, pagingVars.max);
            if (currPage > 1) {
                headerPagingController.enablePreviousButtons();
                footerPagingController.enablePreviousButtons();
            } else {
                headerPagingController.disablePreviousButtons();
                footerPagingController.disablePreviousButtons();
            }
            if (pagingVars.first + pagingVars.max < pagingVars.totalNumberOfEntities) {
                headerPagingController.enableNextButtons();
                footerPagingController.enableNextButtons();
            } else {
                headerPagingController.disableNextButtons();
                footerPagingController.disableNextButtons();
            }
            headerPagingController.enableRecordsNoSelect();
            footerPagingController.enableRecordsNoSelect();
            headerPagingController.enableInput();
            footerPagingController.enableInput();
        }
    }

    this.paging_prev = function () {
        pagingVars.first -= pagingVars.max;
        if (pagingVars.first < 0) {
            pagingVars.first = 0;
        }
        onPagingEvent();
    }

    this.paging_next = function () {
        pagingVars.first += pagingVars.max;
        onPagingEvent();
    }

    this.paging_first = function () {
        pagingVars.first = 0;
        onPagingEvent();
    }

    this.paging_last = function () {
        if (pagingVars.totalNumberOfEntities % pagingVars.max > 0) {
            pagingVars.first = pagingVars.totalNumberOfEntities - pagingVars.totalNumberOfEntities % pagingVars.max;
        } else {
            pagingVars.first = pagingVars.totalNumberOfEntities - pagingVars.max;
        }
        onPagingEvent();
    }

    this.paging_onRecordsNoSelectChange = function (recordsNoSelectElement) {
        var recordsNoSelectValue = recordsNoSelectElement.val();
        $.cookie("page_size", recordsNoSelectValue, {expires: cookieExpires});
        pagingVars.max = parseInt(recordsNoSelectValue);
        pagingVars.first = 0;
        onPagingEvent();
    }

    this.paging_setPageNo = function (pageNoElement) {
        var pageNoValue = pageNoElement.val();
        if (!pageNoValue || $.trim(pageNoValue) == "") {
            pageNoElement.addClass("inputError");
            return;
        }
        if (!/^\d*$/.test(pageNoValue)) {
            pageNoElement.addClass("inputError");
            return;
        }
        var intValue = parseInt(pageNoValue);
        if (intValue <= 0) {
            pageNoElement.addClass("inputError");
            return;
        }
        if (intValue > Math.ceil(pagingVars.totalNumberOfEntities / pagingVars.max)) {
            pageNoElement.addClass("inputError");
            return;
        }
        pagingVars.first = pagingVars.max * (intValue - 1);
        onPagingEvent();
    }

    function onPagingEvent() {
        headerPagingController.hideInputError();
        footerPagingController.hideInputError();
        gridController.onPagingParametersChange();
    }

    this.getPagingParameters = function () {
        return [pagingVars.first, pagingVars.max];
    }

    this.updatePagingParameters = function (_first, _max, _totalNumberOfEntities) {
        if (_first >= _totalNumberOfEntities) {
            pagingVars.first = 0;
            if (_first > _totalNumberOfEntities) {
                gridController.onPagingParametersChange();
            }
        } else {
            pagingVars.first = _first;
        }
        pagingVars.max = _max;
        pagingVars.totalNumberOfEntities = _totalNumberOfEntities;
        entitiesNumberSpan.html("(" + pagingVars.totalNumberOfEntities + ")");
        paging_refresh();
    }

    this.getHeaderElement = function () {
        headerElement = $("<div>").addClass('grid_header').addClass("elementHeader").addClass("elementHeaderDisabled");
        headerElement.append($("<span>").html(translations.header).addClass('grid_header_gridName').addClass('elementHeaderTitle'));
        entitiesNumberSpan = $("<span>").html("(0)").addClass('grid_header_totalNumberOfEntities').addClass('elementHeaderTitle');
        headerElement.append(entitiesNumberSpan);

        if (gridParameters.activable && !gridParameters.lookup) {
            headerElements.onlyActiveButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton("", function (e) {
                onlyActiveButtonClicked();
            }, "unactiveVisibleIcon.png");
            headerElements.onlyActiveButton.attr("title", translations.unactiveNotVisibleButton);
            headerElements.allButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton("", function (e) {
                allButtonClicked();
            }, "allVisibleIcon.png");
            headerElements.allButton.attr("title", translations.unactiveVisibleButton);

            headerElements.onlyInactiveButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton("", function (e) {
                onlyInactiveButtonClicked();
            }, "unactiveNotVisibleIcon.png");
            headerElements.onlyInactiveButton.attr("title", translations.onlyInactiveVisibleButton);

            headerElement.append(headerElements.onlyActiveButton);
            headerElement.append(headerElements.allButton);
            headerElement.append(headerElements.onlyInactiveButton);
            setEnabledButton(headerElements.onlyActiveButton, true);
            setEnabledButton(headerElements.allButton, true);
            setEnabledButton(headerElements.onlyInactiveButton, true);
            headerElements.allButton.hide();
            headerElements.onlyInactiveButton.hide();
        }
        if (gridParameters.canNew && !gridParameters.weakRelation) {
            headerElements.newButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.newButton, function (e) {
                if (headerElements.newButton.hasClass("headerButtonEnabled")) {
                    gridController.onNewButtonClicked();
                }
            }, "newIcon16_dis.png");
            headerElement.append(headerElements.newButton);
            setEnabledButton(headerElements.newButton, false);
        }
        if (gridParameters.canNew && gridParameters.weakRelation) {
            headerElements.addExistingButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.addExistingButton, function (e) {
                if (headerElements.addExistingButton.hasClass("headerButtonEnabled")) {
                    gridController.onAddExistingButtonClicked();
                }
            }, "newIcon16_dis.png");
            headerElement.append(headerElements.addExistingButton);
            setEnabledButton(headerElements.addExistingButton, false);
        }
        if (gridParameters.canDelete) {
            headerElements.deleteButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.deleteButton, function (e) {
                if (headerElements.deleteButton.hasClass("headerButtonEnabled")) {
                    gridController.onDeleteButtonClicked();
                }
            }, "deleteIcon16_dis.png");
            headerElement.append(headerElements.deleteButton);
            setEnabledButton(headerElements.deleteButton, false);
        }
        if (gridParameters.hasPredefinedFilters) {
            var options = new Array();

            for (var i = 0, j = gridParameters.predefinedFilters.length; i < j; i++) {
                var predefinedFilter = gridParameters.predefinedFilters[i];
                options.push({
                    value: i,
                    label: translations["filter." + predefinedFilter.label]
                });
            }

            headerElements.predefiniedFiltersCombo = QCD.components.elements.utils.HeaderUtils.createHeaderComboBox(options, function (selectedItem) {
                if (selectedItem < 0) {
                    return;
                }
                headerElements.predefiniedFiltersCustomOption_line1.css("display", "none");
                headerElements.predefiniedFiltersCustomOption_line2.css("display", "none");
                var filterObj = gridParameters.predefinedFilters[selectedItem];
                gridController.setFilterObject(filterObj);
            });

            if (gridParameters.hasFilterableColumns) {
                headerElements.predefiniedFiltersCombo.append(headerElements.predefiniedFiltersCustomOption_line1);
                headerElements.predefiniedFiltersCombo.append(headerElements.predefiniedFiltersCustomOption_line2);
            }
            headerElement.append(headerElements.predefiniedFiltersCombo);
        }
        if (gridParameters.hasFilterableColumns && gridParameters.filter) {
            headerElements.filterButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.addFilterButton, function () {
                if (headerElements.filterButton.hasClass("headerButtonEnabled")) {
                    filterClicked();
                }
            }, "filterIcon16_dis.png");
            headerElements.saveFilterButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.saveFilterButton, function () {
                if (headerElements.saveFilterButton.hasClass("headerButtonEnabled")) {
                    animateHeaderButtonClick(this);
                    saveFilterClicked();
                }
            }, "saveIcon16.png");
            headerElements.clearFilterButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton("", function () {
                if (headerElements.clearFilterButton.hasClass("headerButtonEnabled")) {
                    clearFilterClicked();
                }
            }, "clearIcon16_dis.png");
            headerElements.resetFilterButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton("", function () {
                if (headerElements.resetFilterButton.hasClass("headerButtonEnabled")) {
                    resetFilterClicked();
                }
            }, "cancel16.png");
            headerElements.saveFilterButton.attr("title", translations.saveFilterButton);
            headerElements.clearFilterButton.attr("title", translations.clearFilterButton);
            headerElements.resetFilterButton.attr("title", translations.resetFilterButton);
            headerElement.append(headerElements.filterButton);
            headerElement.append(headerElements.saveFilterButton);
            headerElement.append(headerElements.clearFilterButton);
            headerElement.append(headerElements.resetFilterButton);
            setEnabledButton(headerElements.filterButton, false);
            setEnabledButton(headerElements.saveFilterButton, true);
            setEnabledButton(headerElements.resetFilterButton, true);
            headerElements.clearFilterButton.hide();
        }
        headerElements.columnChooserButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton("", function () {
            if (headerElements.columnChooserButton.hasClass("headerButtonEnabled")) {
                columnChooserClicked();
            }
        }, "fileviewColumn16.png");
        headerElements.columnChooserButton.attr("title", translations.columnChooserButton);
        headerElement.append(headerElements.columnChooserButton);
        setEnabledButton(headerElements.columnChooserButton, true);
        headerElements.saveColumnWidthButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.saveColumnWidthButton, function () {
            if (headerElements.saveColumnWidthButton.hasClass("headerButtonEnabled")) {
                animateHeaderButtonClick(this);
                saveColumnWidthClicked();
            }
        }, "saveIcon16.png");
        headerElements.saveColumnWidthButton.attr("title", translations.saveColumnWidthButton);
        headerElement.append(headerElements.saveColumnWidthButton);
        setEnabledButton(headerElements.saveColumnWidthButton, true);
        if (gridParameters.orderable) {
            headerElements.upButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.upButton, function (e) {
                if (headerElements.upButton.hasClass("headerButtonEnabled")) {
                    gridController.onUpButtonClicked();
                }
            }, "upIcon16_dis.png");
            headerElement.append(headerElements.upButton);
            setEnabledButton(headerElements.upButton, false);
            headerElements.downButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.downButton, function (e) {
                if (headerElements.downButton.hasClass("headerButtonEnabled")) {
                    gridController.onDownButtonClicked();
                }
            }, "downIcon16_dis.png");
            headerElement.append(headerElements.downButton);
            setEnabledButton(headerElements.downButton, false);
        }
        if (gridParameters.hasMultiSearchColumns && gridParameters.multiSearchColumns) {
            headerElements.multiSearchButton = QCD.components.elements.utils.HeaderUtils.createHeaderButton(translations.multiSearchButton, function (e) {
                if (headerElements.multiSearchButton.hasClass("headerButtonEnabled")) {
                    multiSearchClicked();
                }
            }, "searchIcon16.png");
            headerElement.append(headerElements.multiSearchButton);
            setEnabledButton(headerElements.multiSearchButton, false);
        }
        if (gridParameters.paging) {
            headerPagingController = new QCD.components.elements.grid.GridPagingElement(this, mainController, translations);
            headerElement.append(headerPagingController.getPagingElement(pagingVars));
        }
        return headerElement;
    };

    function animateHeaderButtonClick(button){
        $(button).addClass("headerButtonActive");
        setTimeout(function(){
            button.removeClass("headerButtonActive");
        }, 200);
    }

    this.getFooterElement = function () {
        if (!gridParameters.paging && !gridParameters.autoRefresh) {
            return null;
        }
        footerPagingController = new QCD.components.elements.grid.GridPagingElement(this, mainController, translations);
        footerElement = $("<div>").addClass('grid_footer');

        if (gridParameters.autoRefresh) {
            autoRefreshElement = createautoRefreshElement();
            footerElement.append(autoRefreshElement);
        }
        if (gridParameters.paging) {
            footerElement.append(footerPagingController.getPagingElement(pagingVars));
        }
        return footerElement;
    }

    function createautoRefreshElement() {
        element = $("<div>").addClass("autoRefresh");
        checkbox = $("<input type='checkbox' id='autoRefresh' value='autoRefresh'>");
        checkbox.attr("checked", $.cookie('auto_refresh') === '1');
        label = $("<label>");
        label.append(checkbox);
        label.append(translations.autoRefresh);

        intervalSelect = $("<select id='autoRefreshInterval'>").addClass('autoRefreshInterval');
        intervalSelect.append("<option value='1'>1 min</option>");
        intervalSelect.append("<option value='5'>5 min</option>");
        intervalSelect.append("<option value='10'>10 min</option>");
        intervalSelect.val($.cookie('auto_refresh_interval'));
        intervalSelect.change(function (e) {
            $.cookie('auto_refresh_interval', $(this).val(), {expires: cookieExpires});
            onPagingEvent();
        });

        var that = $(this);
        var checkboxOnChange = function () {
            var isChecked = checkbox.is(":checked");
            if (isChecked) {
                autoRefreshActive = true;
                var interval = $.cookie('auto_refresh_interval');
                interval = interval || 1;
                autoRefreshIntervalId = setInterval(function () {
                    autoRefreshGrid()
                }, interval * 60000);
                that.mousemove(function (e) {
                    idleTimer = 0;
                });
                that.keypress(function (e) {
                    idleTimer = 0;
                });
                idleIntervalId = setInterval(timerIncrement, 60000); // 1 minute
            } else {
                autoRefreshActive = false;
                clearInterval(autoRefreshIntervalId);
                clearInterval(idleIntervalId);
                idleTimer = 0;
                that.off('mousemove');
                that.off('keypress');
            }

            $.cookie('auto_refresh', isChecked ? '1' : '0', {expires: cookieExpires});
            onPagingEvent();
        };
        checkbox.change(checkboxOnChange);
        if (checkbox.prop('checked')) {
            setTimeout(checkboxOnChange, 10000);
        }

        intervalSelect.change(function (e) {
            if (autoRefreshActive) {
                clearInterval(autoRefreshIntervalId);
                var interval = $("#autoRefreshInterval").val();
                autoRefreshIntervalId = setInterval(function () {
                    autoRefreshGrid()
                }, interval * 60000);
            }
        });
        element.append(label);
        element.append(intervalSelect);

        return element;
    }

    function autoRefreshGrid() {
        $(this).off('mousemove');
        gridController.performRefresh();
        setTimeout(function () {
            $(this).mousemove(function (e) {
                idleTimer = 0;
            });
        }, 1000);
    }

    function timerIncrement() {
        // GOODFOOD-1186 - disabled auto-off
//        idleTimer++;
//        if (idleTimer > 59) {
//            $('#autoRefresh').attr('checked', false);
//            $('#autoRefresh').trigger('change');
//            idleTimer = 0;
//        }
    }

    this.setEnabled = function (_enabled) {
        enabled = _enabled;
        if (enabled) {
            headerElement.removeClass("elementHeaderDisabled");
            if (footerElement) {
                footerElement.removeClass("elementHeaderDisabled");
            }
        } else {
            headerElement.addClass("elementHeaderDisabled");
            if (footerElement) {
                footerElement.addClass("elementHeaderDisabled");
            }
        }
        refreshButtons();
    }

    function getRulesToSet(data) {
        var rulesToSet = [];
        for (var ruleIterator in data.rules) {
            var ruleData = {
                fieldIndex: null,
                operatorIndex: null,
                dataValue: null
            };
            var rule = data.rules[ruleIterator];
            for (i = 0; i < gridParameters.multiSearchColumns.length; ++i) {
                var column = gridParameters.multiSearchColumns[i];

                if (rule.field === column.itemval) {
                    ruleData.fieldIndex = i;
                    ruleData.dataValue = rule.data;
                    if (column.dataValues) {
                        for (var j = 0; j < column.dataValues.length; ++j) {
                            if (rule.data === column.dataValues[j].value) {
                                ruleData.dataIndex = j;
                                break;
                            }
                        }
                    }
                    var operators;
                    if (column.ops) {
                        operators = column.ops;
                    } else {
                        operators = gridParameters.defaultOperators;
                    }
                    for (var j = 0; j < operators.length; ++j) {
                        if (operators[j].op === rule.op) {
                            ruleData.operatorIndex = j;
                            break;
                        }
                    }
                    break;
                }
            }
            rulesToSet.push(ruleData);
        }
        return rulesToSet;
    }
    this.initializeMultiSearchFilter = function (data) {
        headerElements.multiSearchButton.addClass("headerButtonActive");
        if (multiSearchDialog == null) {
            createMultiSearchDialog();
        }
        var i;
        for (i = 0; i < data.rules.length - 1; ++i) {
            multiSearchDialog.add(i);
        }

        var groupOpIndex = 0;
        if (data.groupOp === "OR") {
            groupOpIndex = 1;
        }
        $("#multiSearchDialog").find("select[name='groupOp']")[0].selectedIndex = groupOpIndex;

        var rulesToSet = getRulesToSet(data);

        for (i = 0; i < rulesToSet.length; ++i) {
            if (rulesToSet[i].fieldIndex != null && rulesToSet[i].operatorIndex != null && rulesToSet[i].dataValue != null) {
                var htmlRule = $("#multiSearchDialog").find("tr.sf")[i];
                $(htmlRule).find("select[name='field']")[0].selectedIndex = rulesToSet[i].fieldIndex;
                $(htmlRule).find("select[name='field']").change();
                $(htmlRule).find("select[name='op']")[0].selectedIndex = rulesToSet[i].operatorIndex;
                $(htmlRule).find("input.vdata").val(rulesToSet[i].dataValue);
                var select = $(htmlRule).find("select.vdata")[0];
                if (select) {
                    select.selectedIndex = rulesToSet[i].dataIndex;
                }
            }
        }
    }

    this.setDeleteEnabled = function (enabled) {
        deleteButtonEnabled = enabled;
        refreshButtons();
    }

    this.onSelectionChange = function (_multiselectMode, _selectedRowIndex) {
        multiselectMode = _multiselectMode;
        rowIndex = _selectedRowIndex;
        refreshButtons();
    }

    function refreshButtons() {
        if (!enabled) {
            if (headerElements.predefiniedFiltersCombo != null) {
                headerElements.predefiniedFiltersCombo.disable();
            }
            if (headerElements.newButton != null) {
                setEnabledButton(headerElements.newButton, false);
            }
            if (headerElements.addExistingButton != null) {
                setEnabledButton(headerElements.addExistingButton, false);
            }
            if (headerElements.deleteButton != null) {
                setEnabledButton(headerElements.deleteButton, false);
            }
            if (headerElements.upButton != null) {
                setEnabledButton(headerElements.upButton, false);
            }
            if (headerElements.downButton != null) {
                setEnabledButton(headerElements.downButton, false);
            }
        } else {
            if (headerElements.filterButton != null) {
                setEnabledButton(headerElements.filterButton, true);
            }
            if (headerElements.predefiniedFiltersCombo != null) {
                headerElements.predefiniedFiltersCombo.enable();
            }
            if (headerElements.newButton != null) {
                setEnabledButton(headerElements.newButton, true);
            }
            if (headerElements.addExistingButton != null) {
                setEnabledButton(headerElements.addExistingButton, true);
            }
            if (headerElements.deleteButton != null) {
                if ((multiselectMode || rowIndex != null) && deleteButtonEnabled) {
                    setEnabledButton(headerElements.deleteButton, true);
                } else {
                    setEnabledButton(headerElements.deleteButton, false);
                }
            }
            if (headerElements.upButton != null) {
                if (multiselectMode || rowIndex == 1 || rowIndex == null) {
                    setEnabledButton(headerElements.upButton, false);
                } else {
                    setEnabledButton(headerElements.upButton, true);
                }
            }
            if (headerElements.downButton != null) {
                if (multiselectMode || rowIndex == pagingVars.totalNumberOfEntities || rowIndex == null) {
                    setEnabledButton(headerElements.downButton, false);
                } else {
                    setEnabledButton(headerElements.downButton, true);
                }
            }
            if (headerElements.multiSearchButton != null) {
                setEnabledButton(headerElements.multiSearchButton, true);
            }
        }

        if (gridParameters.paging) {
            var currPage = Math.ceil(pagingVars.first / pagingVars.max) + 1;
            var pagesNo = Math.ceil(pagingVars.totalNumberOfEntities / pagingVars.max);
            if (pagesNo == 0) {
                pagesNo = 1;
            }
        }

    }

    function allButtonClicked() {
        headerElements.allButton.hide();
        //headerElements.onlyActiveButton.hide();
        headerElements.onlyInactiveButton.css("display", "inline-block");
        //gridController.setOnlyActive(false);
        gridController.setOnlyInactive(true);
    }

    function onlyActiveButtonClicked() {
        headerElements.onlyActiveButton.hide();
        //headerElements.onlyInactiveButton.hide();
        headerElements.allButton.css("display", "inline-block");
        gridController.setOnlyActive(false);
        //gridController.setOnlyInactive(false);
    }
    function onlyInactiveButtonClicked() {
        headerElements.onlyInactiveButton.hide();
        //headerElements.allButton.hide();
        headerElements.onlyActiveButton.css("display", "inline-block");
        gridController.setOnlyActiveAndOnlyInactive(true, false);

    }

    function filterClicked() {
        if (headerElements.filterButton.hasClass("headerButtonActive")) {
            headerElements.filterButton.removeClass("headerButtonActive");
            headerElements.filterButton.label.html(translations.addFilterButton);
            headerElements.clearFilterButton.hide();
        } else {
            headerElements.filterButton.addClass("headerButtonActive");
            headerElements.filterButton.label.html(translations.removeFilterButton);
            headerElements.clearFilterButton.css("display", "inline-block");
        }
        gridController.onFilterButtonClicked();
    }

    function multiSearchClicked() {
        if (multiSearchDialog == null) {
            createMultiSearchDialog();
        }

        $("#multiSearchDialog").show();
    }

    function createMultiSearchDialog() {
        $("<div id='multiSearchDialog' role='dialog' tabindex='-1' style='display: block; left: 50px; top: 125px;'></div>").insertBefore("#window");
        $("#multiSearchDialog").hide();
        var groupOps = [
            {op: "AND", text: translations.matchAllRules},
            {op: "OR", text: translations.matchAnyRules}
        ];
        multiSearchDialog = $("#multiSearchDialog").searchFilter(gridParameters.multiSearchColumns, {operators: gridParameters.defaultOperators, groupOps: groupOps, rulesText: '', matchText: translations.match, resetText: translations.resetButton, searchText: translations.searchButton, windowTitle: translations.multiSearchTitle, onSearch: onMultiSearchDialogSearchClicked, onReset: onMuliSearchDialogResetClicked, stringResult: false, clone: true});
        $(".ui-widget-overlay", "#multiSearchDialog").remove();
        $("#multiSearchDialog table thead tr:first td:first").css('cursor', 'move');
        if (jQuery.fn.jqDrag) {
            $("#multiSearchDialog").jqDrag($("#multiSearchDialog table thead tr:first td:first"));
        } else {
            try {
                $("#multiSearchDialog").draggable({handle: $("#multiSearchDialog table thead tr:first td:first")});
            } catch (e) {
            }
        }
    }

    function onMultiSearchDialogSearchClicked(data) {
        var isEmpty = true;
        for (var iterator in data.rules) {
            if (data.rules[iterator] != null && (data.rules[iterator].op === "isnull" || $.trim(data.rules[iterator].data) != "")) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) {
            headerElements.multiSearchButton.removeClass("headerButtonActive");
            return;
        }
        headerElements.multiSearchButton.addClass("headerButtonActive");
        gridController.onMultiSearchClicked(data);
    }

    function onMuliSearchDialogResetClicked(data) {
        headerElements.multiSearchButton.removeClass("headerButtonActive");
        gridController.onMultiSearchReset(data);
    }

    function clearFilterClicked() {
        gridController.onClearFilterClicked();
    }

    function saveFilterClicked() {
        gridController.onSaveFilterClicked();
    }

    function saveColumnWidthClicked() {
        gridController.onSaveColumnWidthClicked();
    }

    function resetFilterClicked(){
        gridController.onResetFilterClicked();
    }

    function columnChooserClicked() {
        gridController.onColumnChooserClicked();
    }

    this.setFiltersValuesEmpty = function () {
        setEnabledButton(headerElements.clearFilterButton, false);
    }

    this.setFiltersValuesNotEmpty = function () {
        setEnabledButton(headerElements.clearFilterButton, true);
    }

    this.setFilterActive = function () {
        if (gridParameters.hasFilterableColumns) {
            headerElements.filterButton.addClass("headerButtonActive");
            headerElements.filterButton.label.html(translations.removeFilterButton);
            headerElements.clearFilterButton.css("display", "inline-block");
        }
    }

    this.setFilterNotActive = function () {
        if (gridParameters.hasFilterableColumns) {
            headerElements.filterButton.removeClass("headerButtonActive");
            headerElements.filterButton.label.html(translations.addFilterButton);
            headerElements.clearFilterButton.hide();
        }
    }

    this.setPredefinedFilter = function (predefinedFilter) {
        if (predefinedFilter == null) {
            headerElements.predefiniedFiltersCustomOption_line1.css("display", "");
            headerElements.predefiniedFiltersCustomOption_line2.css("display", "");
            headerElements.predefiniedFiltersCombo.val(-1);
        } else {
            headerElements.predefiniedFiltersCustomOption_line1.css("display", "none");
            headerElements.predefiniedFiltersCustomOption_line2.css("display", "none");
            headerElements.predefiniedFiltersCombo.val(predefinedFilter);
        }
    }

    this.setEnabledButton = function (button, enabled) {
        if (button) {
            if (enabled) {
                button.addClass("headerButtonEnabled");
            } else {
                button.removeClass("headerButtonEnabled");
            }
        }
    }
    var setEnabledButton = this.setEnabledButton;

    constructor(this);
}


QCD.components.elements.grid.GridPagingElement = function (_gridHeaderController, _mainController, _translations) {

    var gridHeaderController = _gridHeaderController;
    var mainController = _mainController;
    var translations = _translations;

    var pagingElements = new Object();
    pagingElements.prevButton = null;
    pagingElements.nextButton = null;
    pagingElements.firstButton = null;
    pagingElements.lastButton = null;
    pagingElements.recordsNoSelect = null;
    pagingElements.pageNo = null;
    pagingElements.allPagesNoSpan = null;

    function constructor() {
    }

    this.getPagingElement = function (pagingVars) {
        var pagingDiv = $("<div>").addClass('grid_paging');
        var onPageSpan = $("<span>").html(translations.perPage).addClass('onPageSpan');
        pagingDiv.append(onPageSpan);
        pagingElements.recordsNoSelect = $("<select>").addClass('recordsNoSelect');
        pagingElements.recordsNoSelect.append("<option value=20>20</option>");
        pagingElements.recordsNoSelect.append("<option value=30>30</option>");
        pagingElements.recordsNoSelect.append("<option value=50>50</option>");
        pagingElements.recordsNoSelect.append("<option value=100>100</option>");
        pagingElements.recordsNoSelect.append("<option value=200>200</option>");
        pagingElements.recordsNoSelect.val(pagingVars.max);
        pagingDiv.append(pagingElements.recordsNoSelect);

        pagingElements.firstButton = $("<div>").addClass("headerPagingButton").addClass("headerButton_first");
        pagingDiv.append(pagingElements.firstButton);

        pagingElements.prevButton = $("<div>").addClass("headerPagingButton").addClass("headerButton_left");
        pagingDiv.append(pagingElements.prevButton);

        var pagesNo = Math.ceil(pagingVars.totalNumberOfEntities / pagingVars.max);
        if (pagesNo == 0) {
            pagesNo = 1;
        }
        var currPage = Math.ceil(pagingVars.first / pagingVars.max) + 1;

        var pageInfoSpan = $("<span>").addClass('grid_paging_pageInfo');

        pagingElements.pageNo = $("<input type='text'></input>").addClass('pageInput');
        var component_container_form_inner = $("<div>").addClass("component_container_form_inner");
        component_container_form_inner.append('<div class="component_container_form_x"></div>');
        component_container_form_inner.append('<div class="component_container_form_y"></div>');
        component_container_form_inner.append(pagingElements.pageNo.val(currPage));
        var component_container_form_w = $("<div>").addClass('component_container_form_w').append(component_container_form_inner);
        pageInfoSpan.append(component_container_form_w);
        var ofPagesInfoSpan = $("<span>").addClass("ofPagesSpan");
        ofPagesInfoSpan.append('<span>').html(' ' + translations.outOfPages + ' ');
        pagingElements.allPagesNoSpan = $("<span>");
        ofPagesInfoSpan.append(pagingElements.allPagesNoSpan.html(pagesNo));
        pageInfoSpan.append(ofPagesInfoSpan);
        pagingDiv.append(pageInfoSpan);

        pagingElements.nextButton = $("<div>").addClass("headerPagingButton").addClass("headerButton_right");
        pagingDiv.append(pagingElements.nextButton);
        pagingElements.lastButton = $("<div>").addClass("headerPagingButton").addClass("headerButton_last");
        ;
        pagingDiv.append(pagingElements.lastButton);

        pagingElements.firstButton.click(function (e) {
            if ($(e.target).hasClass("headerButtonEnabled")) {
                gridHeaderController.paging_first();
            }
        });
        pagingElements.prevButton.click(function (e) {
            if ($(e.target).hasClass("headerButtonEnabled")) {
                gridHeaderController.paging_prev();
            }
        });

        pagingElements.recordsNoSelect.change(function (e) {
            gridHeaderController.paging_onRecordsNoSelectChange($(this));
        });
        pagingElements.pageNo.change(function (e) {
            gridHeaderController.paging_setPageNo($(this));
        });

        pagingElements.nextButton.click(function (e) {
            if ($(e.target).hasClass("headerButtonEnabled")) {
                gridHeaderController.paging_next();
            }
        });
        pagingElements.lastButton.click(function (e) {
            if ($(e.target).hasClass("headerButtonEnabled")) {
                gridHeaderController.paging_last();
            }
        });

        gridHeaderController.setEnabledButton(pagingElements.prevButton, false);
        gridHeaderController.setEnabledButton(pagingElements.firstButton, false);
        gridHeaderController.setEnabledButton(pagingElements.nextButton, false);
        gridHeaderController.setEnabledButton(pagingElements.lastButton, false);

        return pagingDiv;
    }

    this.setPageData = function (currPage, pagesNo, max) {
        pagingElements.allPagesNoSpan.html(pagesNo);
        pagingElements.pageNo.val(currPage);
        pagingElements.recordsNoSelect.val(max);
    }

    this.enablePreviousButtons = function () {
        gridHeaderController.setEnabledButton(pagingElements.prevButton, true);
        gridHeaderController.setEnabledButton(pagingElements.firstButton, true);
    }
    this.disablePreviousButtons = function () {
        gridHeaderController.setEnabledButton(pagingElements.prevButton, false);
        gridHeaderController.setEnabledButton(pagingElements.firstButton, false);
    }
    this.enableNextButtons = function () {
        gridHeaderController.setEnabledButton(pagingElements.nextButton, true);
        gridHeaderController.setEnabledButton(pagingElements.lastButton, true);
    }
    this.disableNextButtons = function () {
        gridHeaderController.setEnabledButton(pagingElements.nextButton, false);
        gridHeaderController.setEnabledButton(pagingElements.lastButton, false);
    }
    this.enableRecordsNoSelect = function () {
        pagingElements.recordsNoSelect.attr("disabled", false);
    }
    this.disableRecordsNoSelect = function () {
        pagingElements.recordsNoSelect.attr("disabled", true);
    }
    this.enableInput = function () {
        pagingElements.pageNo.attr("disabled", false);
    }
    this.disableInput = function () {
        pagingElements.pageNo.attr("disabled", true);
    }

    this.showInputError = function () {
        pagingElements.pageNo.addClass("inputError");
    }
    this.hideInputError = function () {
        pagingElements.pageNo.removeClass("inputError");
    }

    constructor();
}