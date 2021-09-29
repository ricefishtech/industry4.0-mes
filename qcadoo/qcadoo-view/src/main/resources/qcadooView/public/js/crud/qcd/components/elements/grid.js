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

QCD.components.elements.Grid = function (element, mainController) {
    "use strict";
    
    if (!(this instanceof QCD.components.elements.Grid)) {
        return new QCD.components.elements.Grid(element, mainController);
    }
    
    $.extend(this, new QCD.components.Component(element, mainController));

    var options = this.options,
        headerController = null,
        
        elementPath = this.elementPath,
        elementSearchName = this.elementSearchName,

        gridParameters = {
            paging : true,
            fullScreen : false,
            shrinkToFit : false
        },
        
        grid = null,
        belongsToFieldName = null,

        translations = {},

        componentEnabled = false,

        currentGridHeight = 0,

        linkListener = null,

        selectAllCheckBox = null,

        currentState = {
            selectedEntityId : null,
            selectedEntities : {},
            filtersEnabled : true,
            newButtonClickedBefore : false,
            rowLinkClickedBefore : false,
            addExistingButtonClickedBefore : false,
            multiselectMode : true,
            isEditable : true,
			multiSearchEnabled : false,
			userHiddenColumns : [],
			deleteEnabled : false
        },

        columnModel = {},

        hiddenColumnValues = {},

        globalColumnTranslations = {},

        currentEntities = {},

        noRecordsDiv = null,

        fireOnChangeListeners = this.fireOnChangeListeners,

        that = this,

        addedEntityId = null,

        localStorageKey = ['qcadoo', pluginIdentifier, viewName, elementPath].join("-");

    if (this.options.referenceName) {
        mainController.registerReferenceName(this.options.referenceName, this);
    }

    function cellFormatter(cellvalue, options, rowObject) {
        if (options.colModel.stype === 'select') {
            // translate enumerable value
            cellvalue = globalColumnTranslations[options.colModel.name][cellvalue];
        }
        if (options.colModel.link) {
            // wrap cell value with link-like span element
            var linkElem = $("<span />").addClass('gridLink').addClass(elementPath + '_link');
            linkElem.attr('id', elementPath + "_" + options.colModel.name + "_" + rowObject.id);
            linkElem.append(cellvalue);
            cellvalue = linkElem.wrap('<div />').parent().html();
        } else if (options.colModel.classesNames) {
            if (!options.colModel.classesCondition || Function('rowObject', '"use strict";return '
                    + options.colModel.classesCondition.replace(/&gt;/g,">").replace(/&lt;/g,"<"))(rowObject)) {
                var elem = $("<span />");
                var modelCls = options.colModel.classesNames;
                if(modelCls.includes("rowObject['")){
                    modelCls = Function('rowObject', '"use strict";return '+ options.colModel.classesNames)(rowObject);
                }
                var classes = modelCls.split(" ");
                for (var cls in classes) {
                    elem.addClass(classes[cls]);
                }
                elem.attr('id', elementPath + "_" + options.colModel.name + "_" + rowObject.id);
                elem.append(cellvalue);
                cellvalue = elem.wrap('<div />').parent().html();
            }
        }
        return cellvalue; 
    }

    function parseOptions(options) {
        var colNames = [],
            colModel = [],
            hasFilterableColumns = false,
			hasMultiSearchColumns = false,
			multiSearchColumns=[],
            i = null;	
			
		var selectOperators = [
				{ op: "eq", text: options.translations.operator_eq },
				{ op: "ne", text: options.translations.operator_ne }
			];
		
		gridParameters.defaultOperators = [
				{ op: "cn", text: options.translations.operator_cn },
				{ op: "bw", text: options.translations.operator_bw },
				{ op: "ew", text: options.translations.operator_ew },
				{ op: "eq", text: options.translations.operator_eq },
				{ op: "ne", text: options.translations.operator_ne },
				{ op: "gt", text: options.translations.operator_gt },
				{ op: "ge", text: options.translations.operator_ge },
				{ op: "lt", text: options.translations.operator_lt },
				{ op: "le", text: options.translations.operator_le },
				{ op: "in", text: options.translations.operator_in },
				{ op: "isnull", text: options.translations.operator_isnull },
				{ op: "cin", text: options.translations.operator_cin }
		];
			
        for (i in options.columns) {
            var column = options.columns[i],
                isSortable = false,
                isSerchable = false,
				multiSearchColumn = null;

            columnModel[column.name] = column;
            
            for (var sortColIter in options.orderableColumns) {
                if (options.orderableColumns[sortColIter] === column.name) {
                    isSortable = true;
                    break;
                }
            }
            for (var sortColIter in options.searchableColumns) {
                if (options.searchableColumns[sortColIter] === column.name) {
                    isSerchable = true;
                    hasFilterableColumns = true;
                    break;
                }
            }
			
			for (var multiSearchColIter in options.multiSearchColumns){
				if (options.multiSearchColumns[multiSearchColIter] === column.name){
					hasMultiSearchColumns = true;
					multiSearchColumn = {
							text : column.label,
							itemval : column.name
						};
				}
			}

            column.isSerchable = isSerchable;

            if (!column.hidden) {
                if (isSortable) {
                    colNames.push(column.label + "<div class='sortArrow' id='" + elementPath + "_sortArrow_" + column.name + "'></div>");
                } else {
                    colNames.push(column.label);
                }

                var stype = 'text',
                    searchoptions = {},
                    possibleValues = {};

                if (column.filterValues) {
					
					if(multiSearchColumn){
					    var multiSearchFilterValues = [];
					    var iter = null;
					    for (iter in column.filterValues) {
					        var multiSearchFilterValue = {
					            text: column.filterValues[iter],
					            value: iter
					        };
					        multiSearchFilterValues.push(multiSearchFilterValue);   
					    }
					    multiSearchColumn.dataValues = multiSearchFilterValues;
						multiSearchColumn.ops = selectOperators;
				    }
                    possibleValues[""] = "";
                    var possibleValuesString = ":",
                        j = null;
                        
                    for (j in column.filterValues) {
                        possibleValues[j] = column.filterValues[j];
                        possibleValuesString += ";" + j + ":" + column.filterValues[j];
                    }
                    stype = 'select';
                    searchoptions.value = possibleValuesString;
                    searchoptions.defaultValue = "";
                }

                var col = {
                    name : column.name,
                    index : column.name,
                    width : column.width,
                    sortable : isSortable,
                    resizable : true,
                    align : column.align,
                    classesNames: column.classesNames,
                    classesCondition: column.classesCondition,
                    stype : stype,
                    searchoptions : searchoptions,
                    link : column.link
                };

                globalColumnTranslations[column.name] = possibleValues;

                if (searchoptions.value || column.link || column.classesNames) {
                    col.formatter = cellFormatter;
                }

                colModel.push(col);
            } else {
                var col = {
                    name : column.name,
                    index : column.name,
                    width : column.width,
                    sortable : isSortable,
                    resizable : true,
                    hidden : true,
                    align : column.align,
                    classesNames: column.classesNames,
                    classesCondition: column.classesCondition,
                    stype : stype,
                    searchoptions : searchoptions,
                    link : column.link
                };
                colModel.push(col);
                colNames.push(column.name);

                hiddenColumnValues[column.name] = {};
            }
			if (multiSearchColumn != null) {
				multiSearchColumns.push(multiSearchColumn);
				multiSearchColumn = null;
			}
        }

        restoreSavedColumns(colModel);

        gridParameters.hasMultiSearchColumns = hasMultiSearchColumns;
		gridParameters.multiSearchColumns = multiSearchColumns;
		gridParameters.hasFilterableColumns = hasFilterableColumns;
        gridParameters.filtersDefaultEnabled = options.filtersDefaultVisible && hasFilterableColumns;
        gridParameters.hasPredefinedFilters = options.hasPredefinedFilters;
        gridParameters.predefinedFilters = options.predefinedFilters;

        gridParameters.sortColumns = options.orderableColumns;

        gridParameters.colNames = colNames;
        gridParameters.colModel = colModel;
        gridParameters.datatype = function (postdata) {
        };
        gridParameters.multiselect = true;
        gridParameters.shrinkToFit = options.shrinkToFit;

        gridParameters.listeners = options.listeners;
        gridParameters.canNew = options.creatable;
        gridParameters.canDelete = options.deletable;
        gridParameters.paging = options.paginable;
        gridParameters.activable = options.activable;
        gridParameters.lookup = options.lookup;
        gridParameters.filter = hasFilterableColumns;
        gridParameters.orderable = options.prioritizable;
        gridParameters.allowMultiselect = options.multiselect;
        gridParameters.autoRefresh = options.autoRefresh;

        gridParameters.fullScreen = options.fullscreen;

        gridParameters.footerrow = options.footerRow;
        gridParameters.userDataOnFooter = options.footerRow;
        gridParameters.columnsToSummary = options.columnsToSummary;
        gridParameters.columnsToSummaryTime = options.columnsToSummaryTime;
        gridParameters.suppressSelectEvent = options.suppressSelectEvent;

        if (options.height) {
            gridParameters.height = parseInt(options.height, 10);
            if (gridParameters.height <= 0) {
                gridParameters.height = null;
            }
        }
        if (options.width) {
            gridParameters.width = parseInt(options.width, 10);
        }
        if (!gridParameters.width && !gridParameters.fullScreen) {
            gridParameters.width = 300;
        }
        gridParameters.correspondingViewName = options.correspondingView;
        gridParameters.correspondingComponent = options.correspondingComponent;
        gridParameters.correspondingLookup = options.correspondingLookup;
        gridParameters.correspondingViewInModal = options.correspondingViewInModal;
        gridParameters.weakRelation = options.weakRelation;

        updateUserHiddenColumns();
    }

    function aferSelectionUpdate() {
        var selectionCounter = 0,
            lastSelectedRow = null,
            selectedArray = [],
            selectedEntitiesArray = [],
            i = null;
            
        for (i in currentState.selectedEntities) {
            if (typeof i === "undefined") {
                currentState.selectedEntities = false;
                continue;
            }
            if (currentState.selectedEntities[i]) {
                selectionCounter++;
                lastSelectedRow = i;
                selectedArray.push(i);
                selectedEntitiesArray.push(currentEntities[i]);
            }
        }

        switch (selectionCounter) {
        case 0:
            currentState.selectedEntities = {};
            currentState.multiselectMode = false;
            currentState.selectedEntityId = null;
            break;
        case 1:
            currentState.multiselectMode = false;
            currentState.selectedEntityId = lastSelectedRow;
            break;
        default:
            currentState.multiselectMode = true;
            currentState.selectedEntityId = null;
            break;
        }

        // UPDATE SELECTION COLOR
        if (currentState.multiselectMode) {
            element.addClass("multiselectMode");
        } else {
            element.removeClass("multiselectMode");
        }

        // UPDATE SELECT ALL BUTTON
        if (selectAllCheckBox) {
            var isAllSelected = true,
                isEmpty = true,
                j = null;
            for (j in currentEntities) {
                isEmpty = false;
                if (!currentState.selectedEntities[j]) {
                    isAllSelected = false;
                    break;
                }
            }
            if (isEmpty) {
                isAllSelected = false;
            }
            if (isAllSelected) {
                selectAllCheckBox.attr('checked', true);
                selectAllCheckBox.attr('title', translations.diselectAll);
            } else {
                selectAllCheckBox.attr('checked', false);
                selectAllCheckBox.attr('title', translations.selectAll);
            }
        }

        // UPDATE HEADER
        if (currentState.multiselectMode) {
            headerController.onSelectionChange(true);
        } else {
            var rowIndex = null;
            if (currentState.selectedEntityId) {
                rowIndex = grid.jqGrid('getInd', currentState.selectedEntityId);
                if (rowIndex === false) {
                    rowIndex = null;
                }
            }
            headerController.onSelectionChange(false, rowIndex);
        }

        // FIRE ON CHANGE LISTENERS
        fireOnChangeListeners("onChange", [selectedEntitiesArray]);
    }
    
    this.setLinkListener = function (listener) {
        linkListener = listener;
    };
    
    function setPermanentlyDisableParam(params) {
        if (!componentEnabled) {
            params["window.permanentlyDisabled"] = true;
        }
    }

    function redirectToCorrespondingPage(params) {
        if (gridParameters.correspondingViewName && gridParameters.correspondingViewName !== '' && mainController.canClose()) {
            setPermanentlyDisableParam(params);
            params[gridParameters.correspondingComponent + "." + belongsToFieldName] = currentState.belongsToEntityId;
            var url = gridParameters.correspondingViewName + ".html?context=" + JSON.stringify(params);
            if (gridParameters.correspondingViewInModal) {
                mainController.openModal(elementPath + "_editWindow", url);
            } else {
                mainController.goToPage(url);
            }
        }
    }

    function linkClicked(selectedEntities, colName) {
        if (!currentState.isEditable) {
            return;
        }
        currentState.rowLinkClickedBefore = true;
        if (linkListener) {
            linkListener.onGridLinkClicked(selectedEntities);
        } else if(colName && columnModel[colName].attachment && mainController.canClose()) {
            var url =  "/attachmentViewer.html?attachment=" +  Base64.encodeURI(JSON.stringify(currentEntities[selectedEntities].fields[columnModel[colName].correspondingField]));
            window.open(url, '_blank');
        } else if(colName && columnModel[colName].correspondingView && mainController.canClose()) {
            var params = {};
            params["form.id"] = currentEntities[selectedEntities].fields[columnModel[colName].correspondingField].replace(/\s/g, '').replace(/,/g , '');
            setPermanentlyDisableParam(params);
            var url = columnModel[colName].correspondingView + ".html?context=" + JSON.stringify(params);
            mainController.goToPage(url);
        } else if(colName && columnModel[colName].correspondingViewField && mainController.canClose()) {
            var params = {};
            params["form.id"] = currentEntities[selectedEntities].fields[columnModel[colName].correspondingField].replace(/\s/g, '').replace(/,/g , '');
            setPermanentlyDisableParam(params);
            var url = currentEntities[selectedEntities].fields[columnModel[colName].correspondingViewField] + ".html?context=" + JSON.stringify(params);
            mainController.goToPage(url);
        } else {
            var params = {};
            params[gridParameters.correspondingComponent + ".id"] = selectedEntities;
            redirectToCorrespondingPage(params);
        }
    }
    
    function onModalRender(modalWindow) {
        modalWindow.getComponent("window.grid").setLinkListener(that);
    }

    function onModalClose() {
    }

    function showCorrespondingLookupGridModal(params) {
        if (gridParameters.correspondingLookup && gridParameters.correspondingLookup !== '' && mainController.canClose()) {
            setPermanentlyDisableParam(params);
            var correspondingLookupComponent = mainController.getComponentByReferenceName(gridParameters.correspondingLookup),
                lookupComponentData = correspondingLookupComponent.getComponentData(),
                url = "";
                
			if (lookupComponentData.criteriaModifierParameter) {
				params["window.grid.options"] = {
					criteriaModifierParameter: lookupComponentData.criteriaModifierParameter
				};
			}
            
            url = pluginIdentifier + "/" + correspondingLookupComponent.options.viewName + ".html?context=" + JSON.stringify(params);
            mainController.openModal(elementPath + "_editWindow", url, false, onModalClose, onModalRender);
        }
    }

    this.getComponentValue = function () {
        return currentState;
    };
    
    function getColumnFilterElement(name) {
        return that.element.find("#gs_" + name);
    }


    this.setComponentState = function (state) {
        currentState.rowLinkClickedBefore = false;
        currentState.selectedEntityId = state.selectedEntityId;
        currentState.selectedEntities = state.selectedEntities;
        currentState.multiselectMode = state.multiselectMode;
        currentState.onlyActive = state.onlyActive;
        currentState.onlyInactive = state.onlyInactive;
        currentState.deleteEnabled = state.deleteEnabled;

        if (state.belongsToEntityId) {
            currentState.belongsToEntityId = state.belongsToEntityId;
        } else {
            currentState.belongsToEntityId = null;
        }
        if (state.firstEntity) {
            currentState.firstEntity = state.firstEntity;
        }
        if (state.maxEntities) {
            currentState.maxEntities = state.maxEntities;
        }
        if (currentState.filtersEnabled !== state.filtersEnabled) {
            currentState.filtersEnabled = state.filtersEnabled;
            grid[0].toggleToolbar();
            updateSearchFields();
            if (currentState.filtersEnabled) {
                headerController.setFilterActive();
                currentGridHeight -= 21;
            } else {
                headerController.setFilterNotActive();
                currentGridHeight += 21;
            }
            grid.setGridHeight(currentGridHeight);
        }
		if(currentState.multiSearchEnabled !== state.multiSearchEnabled){
			currentState.multiSearchEnabled = state.multiSearchEnabled;
		}
        
		setSortColumnAndDirection(state.order);
        
        if (state.filters) {
            currentState.filters = state.filters;
            for (var filterIndex in currentState.filters) {
                getColumnFilterElement(filterIndex).val(currentState.filters[filterIndex]);
            }
            findMatchingPredefiniedFilter();
            onFiltersStateChange();
        }
		if(state.multiSearchFilter){
			currentState.multiSearchFilter = state.multiSearchFilter;
			headerController.initializeMultiSearchFilter(state.multiSearchFilter);
		}
        if (state.newButtonClickedBefore || state.addExistingButtonClickedBefore) {
            var lastPageController = mainController.getLastPageController();
            if (lastPageController && lastPageController.getViewName() == gridParameters.correspondingViewName) {
                var lastCorrespondingComponent = lastPageController.getComponentByReferenceName(gridParameters.correspondingComponent);
                addedEntityId = lastCorrespondingComponent.getComponentValue().entityId;
            }
        }
    };
    
    function setSortColumnAndDirection(order) {
        for (var col in columnModel) {
            var column = columnModel[col];
    		$("#" + gridParameters.modifiedPath + "_grid_" + column.name).removeClass("sortColumn");
    		$("#" + elementSearchName + "_sortArrow_" + column.name).removeClass("downArrow");
    	}
    	currentState.order = [];
    	if(order){
	    	$.each(order, function (i, orderItem){
	    		currentState.order.push({
	    			column : orderItem.column,
	    			direction : orderItem.direction
	    		});
	    		$("#" + gridParameters.modifiedPath + "_grid_" + orderItem.column).addClass("sortColumn");
	    		if(orderItem.direction === "asc"){
	    			$("#" + elementSearchName + "_sortArrow_" + orderItem.column).addClass("upArrow");
	    		} else if(orderItem.direction === "desc"){
    				$("#" + elementSearchName + "_sortArrow_" + orderItem.column).addClass("downArrow");
    			}
	    	});

            $(window).resize();
    	}

    }
    
    function findMatchingPredefiniedFilter() {
        var filterToSearch = {},
            isIdentical = true;
        if (currentState.filtersEnabled && currentState.filters) {
            filterToSearch = currentState.filters;
        }
        for (var i in gridParameters.predefinedFilters) {
            var predefiniedFilter = gridParameters.predefinedFilters[i].filter;
            isIdentical = true;

            if (gridParameters.predefinedFilters[i].orderColumn) {
            	isIdentical = false;
            	$.each(currentState.order, function (j, currentStateOrderItem){
	                if (currentStateOrderItem.column === gridParameters.predefinedFilters[i].orderColumn && 
	                		currentStateOrderItem.direction === gridParameters.predefinedFilters[i].orderDirection) {
	                    isIdentical = true;
	                    return;
	                }
            	});
            	if (!isIdentical) {
            		continue;
            	}
            }

            for (var col in columnModel) {
                var column = columnModel[col];
                if (predefiniedFilter[column.name] !== filterToSearch[column.name]) {
                    isIdentical = false;
                    break;
                }
            }
            if (isIdentical) {
                headerController.setPredefinedFilter(i);
                break;
            }
        }

        if (!isIdentical && !jQuery.isEmptyObject(filterToSearch)) {
            headerController.setPredefinedFilter(null);
        } else {
            if(!isIdentical){
                headerController.setPredefinedFilter(0);
            }
        }
    }
    
    function onFiltersStateChange() {
        var hasFiltersValues = false,
            i = null;
        for (i in currentState.filters) {
            if (currentState.filters[i] && currentState.filters[i] !== "") {
                hasFiltersValues = true;
                break;
            }
        }
        if (hasFiltersValues) {
            headerController.setFiltersValuesNotEmpty();
        } else {
            headerController.setFiltersValuesEmpty();
        }
    }
    
    this.setComponentValue = function (value) {
        if(value.headerValue){
            mainController.setWindowHeader(value.headerValue);
        }
        currentState.rowLinkClickedBefore = false;
        currentState.selectedEntityId = value.selectedEntityId;

        if (value.belongsToEntityId) {
            currentState.belongsToEntityId = value.belongsToEntityId;
        } else {
            currentState.belongsToEntityId = null;
        }
        if (value.firstEntity) {
            currentState.firstEntity = value.firstEntity;
        }
        if (value.maxEntities) {
            currentState.maxEntities = value.maxEntities;
        }

        if (value.isEditable) {
            currentState.isEditable = value.isEditable;
        }

        if (value.entities === null) {
            return;
        }

        currentState.deleteEnabled = value.deleteEnabled;

        grid.jqGrid('clearGridData');
        var rowCounter = 1;
        currentEntities = {};
        for (var entityNo in value.entities) {
            var entity = value.entities[entityNo];
            currentEntities[entity.id] = entity;
            var fields = {
                id : entity.id
            };
            for (var fieldName in columnModel) {
                if (hiddenColumnValues[fieldName]) {
                    hiddenColumnValues[fieldName][entity.id] = entity.fields[fieldName];
                    if (entity.fields[fieldName] && entity.fields[fieldName] !== "") {
                        fields[fieldName] = entity.fields[fieldName];
                    } else {
                        fields[fieldName] = "";
                    }
                } else {
                    if (entity.fields[fieldName] && entity.fields[fieldName] !== "") {
                        fields[fieldName] = entity.fields[fieldName];
                    } else {
                        fields[fieldName] = "";
                    }
                }
            }
            grid.jqGrid('addRowData', entity.id, fields);
            if (rowCounter % 2 === 0) {
                grid.jqGrid('setRowData', entity.id, false, "darkRow");
            } else {
                grid.jqGrid('setRowData', entity.id, false, "lightRow");
            }
            if (!entity.active) {
                grid.jqGrid('setRowData', entity.id, false, "inactive");
            }
            rowCounter++;
        }

        if (rowCounter === 1) {
            noRecordsDiv.show();
        } else {
            noRecordsDiv.hide();
        }

        $("." + elementSearchName + "_link").click(function (e) {
            var idArr = e.target.id.split("_"),
                entityId = idArr[idArr.length - 1],
                colName = idArr[idArr.length - 2];
            linkClicked(entityId, colName);
        });

        headerController.updatePagingParameters(currentState.firstEntity, currentState.maxEntities, value.totalEntities);

        currentState.selectedEntities = value.selectedEntities;
        for (var i in currentState.selectedEntities) {
            if (currentState.selectedEntities[i]) {
                var row = grid.getRowData(i);
                if (!row || jQuery.isEmptyObject(row)) {
                    currentState.selectedEntities[i] = false;
                } else {
                    grid.setSelection(i, false);
                }
            }
        }
        aferSelectionUpdate();

        setSortColumnAndDirection(value.order);
        
        if (value.filters) {
            currentState.filters = value.filters;
            for (var filterIndex in currentState.filters) {
                getColumnFilterElement(filterIndex).val(currentState.filters[filterIndex]);
            }
            findMatchingPredefiniedFilter();
            onFiltersStateChange();
        }

        if (value.entitiesToMarkAsNew) {
            for (var id in value.entitiesToMarkAsNew) {
                var row = element.find("#" + id);
                if (row.length > 0) {
                    row.addClass("lastAdded");
                }
            }
        }

        if (value.entitiesToMarkWithCssClass) {
            for (var styledEntityId in value.entitiesToMarkWithCssClass) {
                var row = element.find("#" + styledEntityId);
                if (row.length > 0) {
                    var entityCssClasses = value.entitiesToMarkWithCssClass[styledEntityId];
                    var entityCssClassesLen = entityCssClasses.length;
                    for (var i = 0; i < entityCssClassesLen; i++) {
                        row.addClass(entityCssClasses[i]);
                    }
                }
            }
        }

        if (addedEntityId) {
            var row = element.find("#" + addedEntityId);
            if (row.length > 0) {
                row.addClass("lastAdded");
                addedEntityId = null;
            }
        }

        if (currentState.isEditable === false) {
            this.setComponentEditable(false);
        } else if (typeof value.isEditable !== 'undefined' && value.isEditable !== null) {
            this.setComponentEditable(value.isEditable);
        }


        headerController.setDeleteEnabled(currentState.deleteEnabled);
        if(gridParameters.footerrow){

            grid.jqGrid('footerData', 'set', { cb: 'Σ'});
            addSummaryDataForNumberRows();
            addSummaryDataForNumberTimeRows();

        }
        unblockGrid();
    };

    function addSummaryDataForNumberRows(){
        if(isEmpty(gridParameters.columnsToSummary)){
            return;
        }
        var locale = window.top.document.documentElement.lang;

        var rows = grid.jqGrid('getDataIDs');

        var tmp = gridParameters.columnsToSummary;
        var columnsToSummary = tmp.split(",");
        for (var n = 0; n < columnsToSummary.length; ++n) {
            var c = columnsToSummary[n];

            var totalSum = 0;
            for (var i = 0; i < rows.length; ++i) {
                var row = rows[i];
                var val = grid.jqGrid('getCell', row, c);
                if(val === false){
                    totalSum = false;
                    break;
                } else if (val.indexOf("gridLink") > 0) {
                    val = $(val).text();
                }
                if(locale === "pl_PL" || locale === "pl"){
                    totalSum += parseFloat(nanToZero(val.split('&nbsp;').join('').replace(',','.'))) || 0;
                } else if(locale === "de"){
                    totalSum += parseFloat(nanToZero(val.split('&nbsp;').join('').replace(/\./g,'').replace(',','.'))) || 0;
                } else {
                    totalSum += parseFloat(nanToZero(val.split('&nbsp;').join('').replace(/\,/g,''))) || 0;
                }
            }
            if(totalSum!==false){
                var total = nanToZero(parseFloat(totalSum.toFixed(5)));
                var obj;
                if(locale === "pl_PL" || locale === "pl"){
                    obj = '[{"' + c + '": "' + numberWithSpaces(total.toString().replace('.',',')) + '"}]';
                } else if (locale === "de") {
                    obj = '[{"' + c + '": "' + numberWithDot(total.toString().replace('.',',')) + '"}]';
                } else {
                    obj = '[{"' + c + '": "' + numberWithComa(total.toString().replace('.',',')) + '"}]';
                }
                var colFoot = JSON.parse(obj);
                grid.jqGrid('footerData', 'set', colFoot[0]);
            }
        }
    }

    function numberWithSpaces(x) {
        var parts = x.toString().split(",");
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, " ");
        return parts.join(",");
    }

    function numberWithDot(x) {
        var parts = x.toString().split(",");
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ".");
        return parts.join(",");
    }

    function numberWithComa(x) {
        var parts = x.toString().split(",");
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        return parts.join(".");
    }

    function addSummaryDataForNumberTimeRows() {
        if(isEmpty(gridParameters.columnsToSummaryTime)){
            return;
        }

        var rows = grid.jqGrid('getDataIDs');

        var tmp = gridParameters.columnsToSummaryTime;
        var columnsToSummary = tmp.split(",");

        for (var n = 0; n < columnsToSummary.length; ++n) {
            var c = columnsToSummary[n];
            var totalSum = 0;

            for (var i = 0; i < rows.length; ++i) {
                var row = rows[i];
                var val = grid.jqGrid('getCell', row, c)

                totalSum += toSeconds(val);
            }

            var total = nanToZero(totalSum);
            var obj = '[{"' + c + '": "' + secondsToTime(total) + '"}]';
            var colFoot = JSON.parse(obj);

            grid.jqGrid('footerData', 'set', colFoot[0]);
        }
    }

    function toSeconds(time) {
        if (isEmpty(time)){
            return 0;
        }

        var minus = false;

        if (time.startsWith("-")) {
            minus = true;

            time = time.replace("-", "");
        }

        var parts = time.split(':');

        var hours = (+parts[0]) * 60 * 60;
        var minutes = (+parts[1]) * 60;
        var seconds = (+parts[2]);

        return (minus ? -(hours + minutes + seconds) : (hours + minutes + seconds));
    }

    function secondsToTime(secs) {
        var minus = false;

        var sec_num = parseInt(secs);

        if (sec_num < 0) {
            minus = true;

            sec_num = -sec_num;
        }

        var hours = Math.floor(sec_num / 3600);
        var minutes = Math.floor(sec_num % 3600 / 60);
        var seconds = Math.floor(sec_num % 3600 % 60);

        return (minus ? "-" : "") +
            ((hours < 10) ? "0" : "") + hours + ":" +
            ((minutes < 10) ? "0" : "") + minutes + ":" +
            ((seconds < 10) ? "0" : "") + seconds;
    }

    function nanToZero(val) {
        if (isNaN(val)) {
            return 0;
        }

        return val;
    }

    function isEmpty(str) {
        return (!str || 0 === str.length);
    }

    this.setComponentEnabled = function (isEnabled) {
        componentEnabled = isEnabled;
        headerController.setEnabled(currentState.isEditable && isEnabled);
    };

    function blockGrid() {
        QCD.components.elements.utils.LoadingIndicator.blockElement(element);
    }

    function unblockGrid() {
        QCD.components.elements.utils.LoadingIndicator.unblockElement(element);
    }

    this.setComponentLoading = function (isLoadingVisible) {
        if (isLoadingVisible) {
            blockGrid();
        } else {
            unblockGrid();
        }
    };

    this.setComponentEditable = function (isEditable) {
        currentState.isEditable = isEditable;
        if (currentState.isEditable) {
            grid.removeClass("componentNotEditable");
        } else {
            grid.addClass("componentNotEditable");
        }
        headerController.setEnabled(currentState.isEditable && componentEnabled);
    };

    function onCurrentStateChange(forceUpdate) {
        currentState.selectedEntities = null;
        currentState.multiselectMode = false;
        currentState.selectedEntityId = null;
        if (!forceUpdate) {
            findMatchingPredefiniedFilter();
        }
        mainController.callEvent("refresh", elementPath, function () {
            unblockGrid();
        });
    }
    
    function onSortColumnChange(index, iCol, sortorder) {
        blockGrid();
        var orderIndex = -1;
        $.each(currentState.order, function (i, currentStateOrderItem){
    		if(currentStateOrderItem.column === index){
    			orderIndex = i;
    			return;
    		}
    	});
    	if(orderIndex === -1){
    		currentState.order.push({
    			column: index,
    			direction: "asc"
    		});
    	} else {
    		if (currentState.order[orderIndex].direction === "asc") {
    			currentState.order[orderIndex].direction = "desc";
    		} else {
    			currentState.order.splice(orderIndex, 1);
    		}
    	}

    	if(!jQuery.isEmptyObject(currentState.order)) {
            updateSavedOptions("sorters", currentState.order);
        }
        onCurrentStateChange();
        return 'stop';
    }

    this.onPagingParametersChange = function () {
        blockGrid();
        currentState.firstEntity = headerController.getPagingParameters()[0];
        currentState.maxEntities = headerController.getPagingParameters()[1];
        onCurrentStateChange();
    };

    function onSelectAllClicked() {
        if (selectAllCheckBox.is(':checked')) {
            for (var i in currentEntities) {
                if (!currentState.selectedEntities[i]) {
                    grid.setSelection(i, false);
                    currentState.selectedEntities[i] = true;
                }
            }
        } else {
            for (var i in currentState.selectedEntities) {
                if (currentState.selectedEntities[i]) {
                    grid.setSelection(i, false);
                    currentState.selectedEntities[i] = null;
                }
            }
        }
        aferSelectionUpdate();
        onSelectChange();
    }

    function applyFilters() {
        if (currentState.filtersEnabled) {
            currentState.filters = {};
            for (var i in columnModel) {
                var column = columnModel[i];
                if (column.isSerchable) {
                    var filterValue = getColumnFilterElement(column.name).val();
                    filterValue = $.trim(filterValue);
                    if (filterValue && filterValue !== "") {
                        currentState.filters[column.name] = filterValue;
                    }
                }
            }
        } else {
            currentState.filters = null;
        }
    }
    
    function performFilter() {
        blockGrid();
        applyFilters();
        onCurrentStateChange();
        onFiltersStateChange();
    }
    
    function onFilterChange() {
        performFilter();
    }

    function isEnterKeyPressed(ev) {
        var key = ev.keyCode || ev.which;
        return key === 13;
    }

    function getColumnKeyUpCallback(columnFilterElement) {
        return function (e) {
            if (!isEnterKeyPressed(e)) {
                return;
            }
    
            var val = columnFilterElement.val(),
                columnName = columnFilterElement.attr("id").substring(3),
                currentFilter = "";
                
            if (currentState.filters && currentState.filters[columnName]) {
                currentFilter = currentState.filters[columnName];
            }
            if (currentState.filters && val === currentFilter) {
                return;
            }
            onFilterChange();
        };
    }

    function updateSearchFields() {
        var i = null,
            column,
            columnElement;

        for (i in columnModel) {
            column = columnModel[i];
            if (column.isSerchable) {
                columnElement = getColumnFilterElement(column.name);
                columnElement.unbind('change keyup');
                if (column.filterValues) {
                    columnElement.change(onFilterChange);
                } else {
                    columnElement.keyup(getColumnKeyUpCallback(columnElement));
                }
            } else {
                getColumnFilterElement(column.name).hide();
            }
        }
    }

    this.onFilterButtonClicked = function () {
        grid[0].toggleToolbar();
        currentState.filtersEnabled = !currentState.filtersEnabled;
        if (currentState.filtersEnabled) {
            currentGridHeight -= 23;
            updateSearchFields();
            getColumnFilterElement(options.columns[0].name).focus();
        } else {
            currentGridHeight += 23;
        }
        grid.setGridHeight(currentGridHeight);
        onCurrentStateChange(true);
        onFiltersStateChange();
    };

    this.onClearFilterClicked = function () {
        currentState.filters = {};
        var i = null,
            column;
        for (i in columnModel) {
            column = columnModel[i];
            getColumnFilterElement(column.name).val("");
        }
        onFiltersStateChange();
        onCurrentStateChange();
    };

    this.onSaveFilterClicked = function () {
        updateSavedOptions("filters", currentState.filters);
    };

    this.onSaveColumnWidthClicked = function () {
        saveColumns();
    };

    function saveColumns() {
        var columns = [];
        for (var i in grid[0].p.colModel) {
            var column = grid[0].p.colModel[i];
            if (!column.hidden && !column.hidedlg) {
                columns.push({name: column.name, width: column.width});
            }
        }

        updateSavedOptions("columns", columns);
        updateUserHiddenColumns();
    }

    this.onColumnChooserClicked = function () {
        grid.jqGrid('setColumns', {
            dataheight: currentGridHeight,
            colnameview: false,
            recreateForm: true,
            caption: translations.columnChooserCaption,
            bSubmit: translations.columnChooserSubmit,
            bCancel: translations.columnChooserCancel,
            afterSubmitForm: function () {
                saveColumns();
            },
            afterShowForm: function () {
                for (var key in hiddenColumnValues) {
                    $("#col_"+key).closest("tr").css({"display": "none"})
                }
            }

        });
    };

    this.onResetFilterClicked = function () {
        updateSavedOptions("filters", {});
        this.onClearFilterClicked();
    };

    this.setFilterState = function (column, filterText) {
        if (!currentState.filtersEnabled) {
            grid[0].toggleToolbar();
            currentState.filtersEnabled = true;
            headerController.setFilterActive();
            currentGridHeight -= 21;
            if (currentGridHeight) {
                grid.setGridHeight(currentGridHeight);
            }
        }
        currentState.filters = {};
        currentState.filters[column] = filterText;
        var columnFilterElement = getColumnFilterElement(column);
        columnFilterElement.val(filterText);
        columnFilterElement.focus();
        updateSearchFields();
        onFiltersStateChange();
    };

    this.setOnlyActive = function (onlyActive) {
        blockGrid();
        currentState.onlyActive = onlyActive;
        onCurrentStateChange(gridParameters.hasPredefinedFilters);
    };

    this.setOnlyInactive = function (onlyInactive) {
        blockGrid();
        currentState.onlyInactive = onlyInactive;
        onCurrentStateChange(gridParameters.hasPredefinedFilters);
    };

    this.setOnlyActiveAndOnlyInactive = function (onlyActive, onlyInactive) {
        blockGrid();
        currentState.onlyActive = onlyActive;
        currentState.onlyInactive = onlyInactive;
        onCurrentStateChange(gridParameters.hasPredefinedFilters);
    };

    this.setFilterObject = function (filter) {
        blockGrid();

        var filterObject = filter.filter,
            i = null,
            column = null,
            fieldsNo = 0,
            col = null;

        for (i in columnModel) {
            column = columnModel[i];
            getColumnFilterElement(column.name).val("");
        }
        
        for (col in filterObject) {
            filterObject[col] = Encoder.htmlDecode(filterObject[col]);
            getColumnFilterElement(col).val(filterObject[col]);
            fieldsNo++;
        }
        currentState.filters = filterObject;

        if (fieldsNo === 0) {
            if (!gridParameters.filtersDefaultEnabled) {
                if (currentState.filtersEnabled) {
                    currentGridHeight += 23;
                    grid.setGridHeight(currentGridHeight);
                    $(grid[0]).find('.ui-search-toolbar').hide();
                }
                headerController.setFilterNotActive();
                currentState.filtersEnabled = false;
            }
        } else {
            if (!currentState.filtersEnabled) {
                currentGridHeight -= 23;
                grid.setGridHeight(currentGridHeight);
                $(grid[0]).find('.ui-search-toolbar').show();
                getColumnFilterElement(options.columns[0].name).focus();

                headerController.setFilterActive();
                currentState.filtersEnabled = true;
            }
        }

        setSortColumnAndDirection(filter.orderColumn ? [{
            column : filter.orderColumn,
            direction : filter.orderDirection
        }] : []);

        updateSearchFields();
        onFiltersStateChange();
        onCurrentStateChange(true);
    };
	
	this.onMultiSearchClicked = function(data){
        blockGrid();
		currentState.multiSearchEnabled = true;
		currentState.multiSearchFilter = data;
        onCurrentStateChange();
	}
	
	this.onMultiSearchReset = function(data){
        blockGrid();
		currentState.multiSearchEnabled = false;
		currentState.multiSearchFilter = null;
        onCurrentStateChange();
	}

    this.onNewButtonClicked = function () {
        that.performNew();
    };

    this.onAddExistingButtonClicked = function () {
        that.showModalForAddExistingEntity();
    };

    this.onDeleteButtonClicked = function () {
        that.performDelete();
    };

    this.setDeleteEnabled = function (enabled) {
        headerController.setDeleteEnabled(enabled);
    };

    this.onUpButtonClicked = function () {
        blockGrid();
        mainController.callEvent("moveUp", elementPath, function () {
            unblockGrid();
        });
    };

    this.onDownButtonClicked = function () {
        blockGrid();
        mainController.callEvent("moveDown", elementPath, function () {
            unblockGrid();
        });
    };

    function getMargin(width) {
        var margin = Math.round(width * 0.02);
        if (margin < 20) {
            margin = 20;
        }
        return margin;
    }

    this.updateSize = function (width, height) {
        if (!width) {
            width = 300;
        }
        if (!height || height < 150) {
            height = 300;
        }

        if (this.options.fixedHeight) {
            var windowHeight = $(window).height(),
                ribbonHeight = $(".windowContainer .windowContainerRibbon").height() || 70,
                containerHeight = Math.round(windowHeight - 2 * getMargin(width) - ribbonHeight);
                
            height = containerHeight;
            if ($("#window_windowHeader").length > 0) {
                height -= 35;
            }
            height -= 55;
        }

        element.css("height", height + "px");

        var h = $(".ui-jqgrid-labels").height();
        var HEIGHT_DIFF = 90;
        currentGridHeight = height - HEIGHT_DIFF - h;
        if (currentState.filtersEnabled) {
            currentGridHeight -= 21;
        }
        if (!gridParameters.paging) {
            currentGridHeight += 35;
        }
        if(this.options.footerRow){
            currentGridHeight -= 22;
        }
        grid.setGridHeight(currentGridHeight);
        grid.setGridWidth(width - 24, this.options.shrinkToFit);
    };

    function onSelectChange() {
        if (componentEnabled && !gridParameters.suppressSelectEvent && gridParameters.listeners.length > 0) {
            mainController.callEvent("select", elementPath, null);
        }
    }

    function rowClicked(rowId, col) {
        if (!componentEnabled) {
            grid.setSelection(rowId, false);
            return;
        }

        if (currentState.selectedEntities[rowId]) {
            if (col === 0 && currentState.multiselectMode) {
                currentState.selectedEntities[rowId] = null;
            } else if (currentState.multiselectMode) {
                // diselect all but this
                for (var i in currentState.selectedEntities) {
                    if (currentState.selectedEntities[i]) {
                        grid.setSelection(i, false);
                        currentState.selectedEntities[i] = null;
                    }
                }
                currentState.selectedEntities[rowId] = true;
            } else {
                currentState.selectedEntities[rowId] = null;
            }
        } else {
            if (col !== 0 || !gridParameters.allowMultiselect) {
                // diselect all
                for (var i in currentState.selectedEntities) {
                    if (currentState.selectedEntities[i]) {
                        grid.setSelection(i, false);
                        currentState.selectedEntities[i] = null;
                    }
                }
            }
            currentState.selectedEntities[rowId] = true;
        }

        aferSelectionUpdate();

        // FIRE JAVA LISTENERS
        onSelectChange();
    }

    function restoreSavedOptions() {
        var savedOptions = getSavedOptions();
        if (savedOptions.filters){
            currentState.filters = savedOptions.filters;
        }
        if (savedOptions.sorters) {
            if(!jQuery.isEmptyObject(savedOptions.sorters)) {
                currentState.order = savedOptions.sorters;
            }
        }
    }

    function restoreSavedColumns(colModel) {
        var savedOptions = getSavedOptions();
        if (savedOptions.columns) {
            for (var i in colModel) {
                var contains = false;
                for (var columnIndex in savedOptions.columns) {
                    contains = savedOptions.columns[columnIndex].name === colModel[i].name;
                    if (contains) {
                        colModel[i].width = savedOptions.columns[columnIndex].width;
                        break;
                    }
                }
                if (!contains) {
                    colModel[i].hidden = true;
                }
            }
        }
    }

    function getSavedOptions() {
        return JSON.parse(localStorage.getItem(localStorageKey)) || {};
    }

    function setSavedOptions(options) {
        localStorage.setItem(localStorageKey, JSON.stringify(options));
    }

    function updateSavedOptions(attribute, value) {
        var savedOptions = getSavedOptions();
        savedOptions[attribute] = value;
        setSavedOptions(savedOptions);
    }

    this.performNew = function (actionsPerformer) {
        currentState.newButtonClickedBefore = true;
        currentState.selectedEntities = null;
        currentState.multiselectMode = false;
        currentState.selectedEntityId = null;

        redirectToCorrespondingPage({});
        if (actionsPerformer) {
            actionsPerformer.performNext();
        }
    };

    this.performRefresh = function (actionsPerformer) {
        blockGrid();
        mainController.callEvent('refresh', elementPath, function () {
            unblockGrid();
        });
    };

    this.showModalForAddExistingEntity = function (actionsPerformer) {
        currentState.addExistingButtonClickedBefore = true;
        currentState.selectedEntities = null;
        currentState.multiselectMode = false;
        currentState.selectedEntityId = null;

        showCorrespondingLookupGridModal({});

        if (actionsPerformer) {
            actionsPerformer.performNext();
        }
    };

    this.onGridLinkClicked = function (selectedEntities) {
        that.performAddExistingEntity(null, selectedEntities);
        mainController.closeThisModalWindow();
    };

    function getSelectedRowsCount() {
        var selectionCounter = 0,
            i = null;
        for (i in currentState.selectedEntities) {
            if (currentState.selectedEntities[i]) {
                selectionCounter++;
            }
        }
        return selectionCounter;
    }

    this.performAddExistingEntity = function (actionsPerformer, selectedEntities) {
        blockGrid();
        mainController.callEvent("addExistingEntity", elementPath, function () {
            unblockGrid();
        }, [selectedEntities], actionsPerformer);
    };

    function performOnSelectedEntities(eventName, actionsPerformer, requireConfirmMsg) {
        if (currentState.selectedEntityId || getSelectedRowsCount() > 0) {
            if (!requireConfirmMsg || window.confirm(requireConfirmMsg)) {
                blockGrid();
                mainController.callEvent(eventName, elementPath, function () {
                    unblockGrid();
                }, null, actionsPerformer);
            }
        } else {
            mainController.showMessage({
                type : "error",
                content : translations.noRowSelectedError
            });
        }
    }

    this.performDelete = function (actionsPerformer) {
        performOnSelectedEntities("remove", actionsPerformer, translations.confirmDeleteMessage + " (" + getSelectedRowsCount() + ")?");
    };

    //WARNING use at your own risk - should only be used manually, when delete action is confirmed otherwise!
    this.performDeleteWithoutConfirm = function (actionsPerformer) {
        performOnSelectedEntities("remove", actionsPerformer);
    };

    this.performCopy = function (actionsPerformer) {
        performOnSelectedEntities("copy", actionsPerformer);
    };

    this.performActivate = function (actionsPerformer) {
        performOnSelectedEntities("activate", actionsPerformer);
    };

    this.performDeactivate = function (actionsPerformer) {
        performOnSelectedEntities("deactivate", actionsPerformer);
    };

    this.generateReportForEntity = function (actionsPerformer, arg1, args) {
        var selectedItems = [],
            i = null;
        for (i in currentState.selectedEntities) {
            if (currentState.selectedEntities[i]) {
                selectedItems.push(i);
            }
        }
        if (selectedItems.length > 0) {
            mainController.generateReportForEntity(actionsPerformer, arg1, args, selectedItems);
        } else {
            mainController.showMessage({
                type : "error",
                content : translations.noRowSelectedError
            });
        }
    };

    this.performEvent = function (eventNameOrObj, args, type) {
        var eventObj = null;
        if (typeof eventNameOrObj === 'string') {
            eventObj = {
                name : eventNameOrObj,
                args : args,
                type : type
            };
        } else if (typeof eventNameOrObj === 'object') {
            eventObj = eventNameOrObj;
        } else {
            QCD.error("Illegal first argument type - expected event's name or object, but given " + eventNameOrObj);
        }
        this.fireEvent(null, eventObj);
    };

    var origSendEvent = this.sendEvent;
    this.sendEvent = function (actionsPerformer, eventObj) {
        var origCallback = eventObj.callback;
        if (typeof origCallback === 'function') {
            eventObj.callback = function () {
                try {
                    origCallback();
                } catch (e) {
                    QCD.error(e);
                } finally {
                    unblockGrid();
                }
            };
        } else {
            eventObj.callback = unblockGrid;
        }
        blockGrid();
        origSendEvent.call(this, actionsPerformer, eventObj);
    };

    this.performLinkClicked = function (actionsPerformer) {
        if (currentState.selectedEntities) {
            var selectedEntitiesId = [],
                key = null;
            for (key in currentState.selectedEntities) {
                if (currentState.selectedEntities[key]) {
                    selectedEntitiesId.push(key);
                }
            }
            if (selectedEntitiesId.length === 1) {
                linkClicked(selectedEntitiesId[0]);
            } else {
                linkClicked(selectedEntitiesId);
            }

            if (actionsPerformer) {
                actionsPerformer.performNext();
            }
        } else {
            mainController.showMessage({
                type : "error",
                content : translations.noRowSelectedError
            });
        }
    };
    
    function stripTags(value) {
        if (typeof value === 'string') {
            return value.replace(/<[\/]{0,1}[a|span|b|i|u|br][^>]*>/g, '');
        }
        return value;
    }

    this.getLookupData = function (entityId) {
        return {
            entityId : entityId,
            lookupValue : hiddenColumnValues.lookupValue[entityId],
            lookupCode : stripTags(grid.getRowData(entityId).lookupCode)
        };
    };

    function updateUserHiddenColumns() {
        currentState.userHiddenColumns = [];
        var savedOptions = getSavedOptions();
        if (savedOptions.columns) {
            for (var i in columnModel) {
                var contains = false;
                for (var columnIndex in savedOptions.columns) {
                    contains = savedOptions.columns[columnIndex].name === columnModel[i].name;
                    if (contains) {
                        break;
                    }
                }
                if (!contains) {
                    currentState.userHiddenColumns.push(columnModel[i].name);
                }

            }
        }
    }

    function constructor() {

        parseOptions(that.options, that);

        gridParameters.modifiedPath = elementPath.replace(/\./g, "_");
        gridParameters.element = gridParameters.modifiedPath + "_grid";

        $("#" + elementSearchName + "_grid").attr('id', gridParameters.element);

        translations = that.options.translations;
        belongsToFieldName = that.options.belongsToFieldName;

        headerController = new QCD.components.elements.grid.GridHeaderController(that, mainController, gridParameters, that.options.translations);

        $("#" + elementSearchName + "_gridHeader").append(headerController.getHeaderElement());
        $("#" + elementSearchName + "_gridFooter").append(headerController.getFooterElement());

        currentState.firstEntity = headerController.getPagingParameters()[0];
        if (gridParameters.paging) {
            currentState.maxEntities = headerController.getPagingParameters()[1];
        }

        gridParameters.onCellSelect = function (rowId, iCol, cellcontent, e) {
            if (window.getSelection().toString()) {
                grid.setSelection(rowId, false);
                return;
            }
            if (!currentState.rowLinkClickedBefore) {
                rowClicked(rowId, iCol);
            }
        };

        gridParameters.ondblClickRow = function (id) {
        };
        gridParameters.onSortCol = onSortColumnChange;
        gridParameters.headertitles = true;


        grid = $("#" + gridParameters.element).jqGrid(gridParameters);

        $("#cb_" + gridParameters.element).hide();
        // hide 'select all' check-box
        if (gridParameters.allowMultiselect) {
            selectAllCheckBox = $("<input type='checkbox'>");
            $("#" + elementSearchName + " #jqgh_cb").append(selectAllCheckBox);
            selectAllCheckBox.change(function () {
                onSelectAllClicked();
            });
        }

        for (var i in gridParameters.sortColumns) {
            $("#" + gridParameters.modifiedPath + "_grid_" + gridParameters.sortColumns[i]).addClass("sortableColumn");
        }

        element.width("100%");

        grid.jqGrid('filterToolbar', {
            stringResult : true
        });

        if (gridParameters.isLookup || gridParameters.filtersDefaultEnabled) {
            headerController.setFilterActive();
            currentState.filtersEnabled = true;
            setTimeout(function(){
                getColumnFilterElement(options.columns[0].name).focus();
            }, 0);
        } else {
            grid[0].toggleToolbar();
            currentState.filtersEnabled = false;
        }

        if (gridParameters.filtersDefaultEnabled) {
            updateSearchFields();
        }

        restoreSavedOptions();

        noRecordsDiv = $("<div>").html(translations.noResults).addClass("noRecordsBox");
        noRecordsDiv.hide();
        $("#" + gridParameters.element).parent().append(noRecordsDiv);
    }

    constructor();

};
