
<![CDATA[ERROR PAGE:LoginPage]]>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html>

<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="shortcut icon" href="/qcadooView/public/img/core/icons/favicon.png">

    <title>${applicationDisplayName} :: login</title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/qcadooView/public/css/core/lib/bootstrap.min.css?ver=${buildNumber}"
        type="text/css"/>
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/qcadooView/public/css/core/lib/languages.min.css?ver=${buildNumber}"
        type="text/css"/>
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/qcadooView/public/css/core/login-min.css?ver=${buildNumber}"
        type="text/css"/>

    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-3.2.1.min.js?ver=${buildNumber}"></script>
    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/popper.min.js?ver=${buildNumber}"></script>
    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/bootstrap.min.js?ver=${buildNumber}"></script>
    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/utils/serializator.js?ver=${buildNumber}"></script>
    <script type="text/javascript"
        src="${pageContext.request.contextPath}/qcadooView/public/js/core/login-min.js?ver=${buildNumber}"></script>
</head>

<body class="text-center" role="document">
    <div class="container" role="main">
        <div id="messagePanel" class="alert" role="alert">
            <h6 class="alert-heading" id="messageHeader"></h6>
            <p id="messageContent"></p>
        </div>

        <div class="loginContainer">
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

            <form id="loginForm" name="loginForm" action="<c:url value='j_spring_security_check'/>" method="POST">
            <img class="logo mb-4" src="${logoPath}" alt="Logo"/>
            <h1 class="h3 mb-4 font-weight-normal">${translation["security.form.header"]}</h1>

            <div class="input-group">
                <label for="usernameInput" class="sr-only">${translation["security.form.label.login"]}</label>
                <input type="text" id="usernameInput" name="j_username" class="form-control" placeHolder="${translation["security.form.label.login"]}" value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>' required autofocus>
                <div class="invalid-feedback" style="margin-top: -65px;">
                    ${translation["security.message.wrongLogin"]}
                </div>
            </div>

            <div class="input-group">
                <label for="passwordInput" class="sr-only">${translation["security.form.label.password"]}</label>
                <input type="password" id="passwordInput" name="j_password" class="form-control" placeHolder="${translation["security.form.label.password"]}" required>
                <div class="invalid-feedback" style="margin-top: -25px;">
                    ${translation["security.message.wrongPassword"]}
                </div>
            </div>

            <div class="checkbox mb-3">
                <label>
                    <input id="rememberMeCheckbox" type="checkbox" name="_spring_security_remember_me"> ${translation["security.form.label.rememberMe"]}
                </label>
            </div>

            <button type="button" class="btn btn-lg btn-primary btn-block" id="loginButton"><span>${translation['security.form.button.logIn']}</span></button>

            <p class="mt-3 mb-3">
                <a href="#" id="forgotPasswordLink">${translation['security.form.link.forgotPassword']}</a>
            </p>
            </form>
        </div>
    </div>

    <script type="text/javascript" charset="utf-8">
        var errorHeaderText = '${translation["security.message.errorHeader"]}';
        var errorContentText = '${translation["security.message.errorContent"]}';

        var wrongLoginText = '${translation["security.message.wrongLogin"]}';
        var wrongPasswordText = '${translation["security.message.wrongPassword"]}';

        var isPopup = "${popup}";
        var targetUrl = "${targetUrl}";

        var serverMessageType;
        var serverMessageHeader;
        var serverMessageContent;

        <c:if test="${messageType != null}">
        serverMessageType = '<c:out value="${messageType}"/>';
        serverMessageHeader = '<c:out value="${translation[messageHeader]}"/>';
        serverMessageContent = '<c:out value="${translation[messageContent]}"/>';
        </c:if>

        jQuery(document).ready(function() {
            QCD.login.init();
        });
    </script>
</body>

</html>
