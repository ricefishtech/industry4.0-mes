<%--
    ***************************************************************************
    Copyright (c) 2018 RiceFish Limited
    Project: SmartMES Framework
    Version: 1.6

    This file is part of SmartMES.
    http://www.cloudmes.io
    Email:contact@cloudmes.io
    ***************************************************************************
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<c:choose>
		<c:when test="${useCompressedStaticResources}">
			<link rel="stylesheet" href="${pageContext.request.contextPath}/smartView/public/smartmes-min.css?ver=${buildNumber}" type="text/css" />
			<script type="text/javascript" src="${pageContext.request.contextPath}/smartView/public/js/core/lib/_jquery-1.4.2.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/smartView/public/js/core/lib/jquery-ui-1.8.5.custom.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/smartView/public/js/core/lib/jquery.jqGrid.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/smartView/public/smartmes-min.js?ver=${buildNumber}"></script>
		</c:when>
		<c:otherwise>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/smartView/public/css/core/dashboard.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/smartView/public/css/core/menu/style.css?ver=${buildNumber}" type="text/css" />
			
			<script type="text/javascript" src="${pageContext.request.contextPath}/smartView/public/js/core/lib/_jquery-1.4.2.min.js?ver=${buildNumber}"></script>
		</c:otherwise>
	</c:choose>
	<script type="text/javascript">
		jQuery(document).ready(function(){
			if (window.parent.hasMenuPosition && window.parent.hasMenuPosition('orders.productionOrders')) {
				$("#productionOrdersLink").show();
			}
			if (window.parent.hasMenuPosition && window.parent.hasMenuPosition('technology.technologies')) {
				$("#technologiesLink").show();
			}
			if (window.parent.hasMenuPosition && window.parent.hasMenuPosition('reports.materialRequirements')) {
				$("#materialRequirementsLink").show();
			}
		});

		function goToMenuPosition(position) {
			if (window.parent.goToMenuPosition) {
				window.parent.goToMenuPosition(position);
			} else {
				window.location = "/main.html"
			}
		}
		
	</script>
</head>
<body>

	<div id="windowContainer">
		<div id="windowContainerRibbon">
			<div id="q_row3_out">
				<div id="q_menu_row3"></div>
			</div>
			<div id="q_row4_out"></div>
		</div>
		<div id="windowContainerContentBody">


	<div id="contentWrapperOuter">
	<div id="contentWrapperMiddle">
	<div id="dashboardContentWrapper">
		<div id="buttonsElement">
			<div class="dashboardButton">
				<div class="dashboardButtonIcon icon1"></div>
				<div class="dashboardButtonContent">
					<div class="dashboardButtonContentHeader">
						${translationsMap['basic.dashboard.organize.header']}
					</div>
					<div class="dashboardButtonContentText">
					 	${translationsMap['basic.dashboard.organize.content']}
					</div>
					<div class="dashboardButtonContentLink" id="productionOrdersLink" style="display: none;">
						<a href="#" onclick="goToMenuPosition('orders.productionOrders')">${translationsMap['basic.dashboard.organize.link']}</a>
					</div>
				</div>
			</div>
			<div class="dashboardButton">
				<div class="dashboardButtonIcon icon2"></div>
				<div class="dashboardButtonContent">
					<div class="dashboardButtonContentHeader">
						${translationsMap['basic.dashboard.define.header']}
					</div>
					<div class="dashboardButtonContentText">
					 	${translationsMap['basic.dashboard.define.content']}
					</div>
					<div class="dashboardButtonContentLink" id="technologiesLink" style="display: none;">
						<a href="#" onclick="goToMenuPosition('technology.technologies')">${translationsMap['basic.dashboard.define.link']}</a>
					</div>
				</div>
			</div>
			<div class="dashboardButton">
				<div class="dashboardButtonIcon icon3"></div>
				<div class="dashboardButtonContent">
					<div class="dashboardButtonContentHeader">
						${translationsMap['basic.dashboard.react.header']}
					</div>
					<div class="dashboardButtonContentText">
					 	${translationsMap['basic.dashboard.react.content']}
					</div>
					<div class="dashboardButtonContentLink" id="materialRequirementsLink" style="display: none;">
						<a href="#" onclick="goToMenuPosition('reports.materialRequirements')">${translationsMap['basic.dashboard.react.link']}</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
	</div>
	
	</div>
	</div>
</body>
</html>