<%--

    ***************************************************************************
    Copyright (c) 2010 Qcadoo Limited
    Project: Qcadoo Framework
    Version: 1.4

    This file is part of Qcadoo.

    Qcadoo is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation; either version 3 of the License,
    or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty
    of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    ***************************************************************************

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html>

<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="shortcut icon" href="/qcadooView/public/img/core/icons/favicon.png">

	<title>${applicationDisplayName}</title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/qcadooView/public/css/core/lib/bootstrap.min.css?ver=${buildNumber}"
        type="text/css"/>
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/qcadooView/public/css/core/lib/languages.min.css?ver=${buildNumber}"
        type="text/css"/>
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/qcadooView/public/css/core/browserNotSupported-min.css?ver=${buildNumber}"
        type="text/css"/>

    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-3.2.1.min.js?ver=${buildNumber}"></script>
    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/popper.min.js?ver=${buildNumber}"></script>
    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/bootstrap.min.js?ver=${buildNumber}"></script>
    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/browserNotSupported-min.js?ver=${buildNumber}"></script>
</head>

<body class="text-center" role="document">
    <div class="container" role="main">
	    <div class="browserNotSupportedContainer">
            <c:if test="${! iframe && ! popup}">
                <div class="text-right">
                    <div class="btn-group dropup">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                            <span class="lang-sm" lang="${currentLanguage}"></span> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <c:forEach items="${locales}" var="localesEntry">
                                <li><span class="lang-sm lang-lbl" lang="${localesEntry.key}"></span></li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                <div class="mt-3"></div>
            </c:if>

            <img class="logo mb-4" src="/qcadooView/public/css/core/images/login/new/qcadoo-logo.png" alt="Logo"/>

            <div class="card">
                <h5 class="card-header bg-secondary text-white">${translation["qcadooView.browserNotSupported.content"]}</h5>
                <div class="card-body">
                    <h5 class="card-title">${translation["qcadooView.browserNotSupported.listHeader"]}</h5>
                    <ul class="card-text text-dark text-left">
                        <li>
                            <a href="http://www.google.com/chrome" target="_blank">Chrome</a>
                        </li>
                        <li>
                            <a href="http://www.firefox.com" target="_blank">Firefox</a>
                            <span class="fromVersionText">${translation["qcadooView.browserNotSupported.listFromVersion"]}</span>
                            <span class="fromVersionNumber">3.0</span>
                        </li>
                        <li>
                            <a href="http://windows.microsoft.com/en-US/internet-explorer/products/ie/home" target="_blank">Internet Explorer</a>
                            <span class="fromVersionText">${translation["qcadooView.browserNotSupported.listFromVersion"]}</span>
                            <span class="fromVersionNumber">8</span>
                        </li>
                        <li>
                            <a href="http://www.apple.com/safari/" target="_blank">Safari</a>
                        </li>
                    </ul>
                </div>
            </div>
		</div>
    </div>

	<script type="text/javascript" charset="utf-8">
        jQuery(document).ready(function() {
            QCD.browserNotSupported.init();
        });
    </script>
</body>

</html>