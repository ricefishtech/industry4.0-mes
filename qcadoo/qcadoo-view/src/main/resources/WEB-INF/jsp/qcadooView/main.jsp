
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="<c:out value="${languageCode}" />">
<head>

	<title>${applicationDisplayName}</title>
	
	<c:choose>
		<c:when test="${useCompressedStaticResources}">
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/qcadoo-min.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/custom.css?ver=${buildNumber}" type="text/css" />
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-1.8.3.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-ui-1.8.5.custom.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery.jqGrid.min.js?ver=${buildNumber}"></script>

			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/packaged/jquery.noty.packaged.min.js?ver=${buildNumber}"></script>
            <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/layouts/top.min.js?ver=${buildNumber}"></script>
            <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/layouts/center.min.js?ver=${buildNumber}"></script>

            <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/notyController.min.js?ver=${buildNumber}"></script>

			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/qcadoo-min.js?ver=${buildNumber}"></script>
		</c:when>
		<c:otherwise>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/qcd.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/qcadoo-min.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/mainPage.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/menuTopLevel.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/menu/style.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/notification.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/jqModal.css?ver=${buildNumber}" type="text/css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/alert/animate.css?ver=${buildNumber}" type="text/css" />

			<link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/custom.css?ver=${buildNumber}" type="text/css" />

            <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-1.8.3.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery.pnotify.js?ver=${buildNumber}"></script>

			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery.blockUI.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jqModal.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/utils/logger.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/utils/modal.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/utils/connector.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/menu/model.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/menu/menuController.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/core/windowController.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/core/messagesController.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/crud/qcd/components/elements/utils/loadingIndicator.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/highlight.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/liveUpdate.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery.menu-aim.js?ver=${buildNumber}"></script>

			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/packaged/jquery.noty.packaged.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/layouts/top.min.js?ver=${buildNumber}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/layouts/center.min.js?ver=${buildNumber}"></script>

			<script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/alert/notyController.min.js?ver=${buildNumber}"></script>

		</c:otherwise>
	</c:choose>
	
	<link rel="shortcut icon" href="/qcadooView/public/img/core/icons/favicon.png">
	
	<script type="text/javascript">

		var menuStructure = ${menuStructure}

		var windowController;

        // ************ open request page

		jQuery(document).ready(function(){

			windowController = new QCD.WindowController(menuStructure);

			var notifications = new QCD.Notifications();
 			notifications.getNotifications(${dbNotificationsEnabled}, ${systemNotificationsEnabled}, ${systemNotificationsIntervalInSeconds});

			notifications.getActivityStream(${activityStreamEnabled}, ${activityStreamIntervalInSeconds});

			$("#mainPageIframe").load(function() {
				try {
					el = $('body', $('iframe').contents());
					el.click(function() {
					    windowController.restoreMenuState();
					    if ($('.activityStreamContainer').css('display') == 'block') {
					        notifications.markActivityStreamAsRead();
					        $('.activityStreamContainer').hide();
					    }
					});
					$(document.getElementById('mainPageIframe').contentWindow.document).keydown(function(event){
					    var keycode = (event.keyCode ? event.keyCode : event.which);

                            if(event.ctrlKey){
                                if(keycode == 77){
                                    $logoDropdownBox.toggleClass('open');
                                    $userMenuBackdoor.toggleClass('open');
            						$('.subMenuBox').hide();
                                    $('.subMenuBox .maintainHover').removeClass('maintainHover');
                                    if($logoDropdownBox.hasClass('open')){
                                        $headerSearchInput.val('').keyup().focus();
            					        $('.mainMenu .maintainHover').removeClass('maintainHover');
            					        $('.mainMenu .currentMainActive').addClass('maintainHover');
            					        activateSubmenu($('.maintainHover', $mainMenu).parent());
                                    }
            					}
                            }
                            if($logoDropdownBox.hasClass('open')) {
                                if (keycode == 27) {
                                    $('.userMenuBackdoor').click();
                                }
                            }
					});

				} catch(e) {
				}
			});

			// ************ base variable
            var $logoDropdownBox = $('.logoDropdownBox');
            var $userMenuBackdoor = $('.userMenuBackdoor');
            var $headerSearchInput = $('.headerSearchForm [type="text"]');
            var $mainMenu = $('.mainMenu');

            // ************ toogle menu visible by arrow
            $('.logoDropdownBoxToggle a.arrow, .userMenuBackdoor').click(function(e){
                $logoDropdownBox.toggleClass('open');
                $userMenuBackdoor.toggleClass('open');
                $('.subMenuBox').hide();
                $('.subMenuBox .maintainHover').removeClass('maintainHover');
                if($logoDropdownBox.hasClass('open')){
                    $headerSearchInput.val('').keyup().focus();
                    $('.mainMenu .maintainHover').removeClass('maintainHover');
                    $('.mainMenu .currentMainActive').addClass('maintainHover');
                    activateSubmenu($('.maintainHover', $mainMenu).parent());
                }
                e.preventDefault();
            });

            // ************ main menu disabled click
            $('.mainMenu a').click(function(e){
                e.preventDefault();
            });

            // ************ main menu live search
            $headerSearchInput.liveUpdate('.subMenuBoxLiveSearch .subMenu');


            // ************ main menu search, clear
            $('.headerSearchForm .iconDel').click(function(e){
                $headerSearchInput.val('').keyup().blur();
                e.preventDefault();
            });

            if (${activityStreamEnabled}) {
                $('.activityStreamIcon').show();
            } else {
                $('.activityStreamIcon').hide();
            }

            $('.activityStreamIcon').click(function(e){
                if ($('.activityStreamContainer').css('display') == 'block') {
                    notifications.markActivityStreamAsRead();
                }
                var position = $('.activityStreamIcon').position();
                $('.activityStreamContainer').css({ left: position.left - 150 + "px", top: position.top + 30 + "px"});
                $('.activityStreamContainer').toggle();
            });

            // ************ lazy menu show item
            function activateSubmenu(row) {
                deactivateSubmenu($('.maintainHover', $mainMenu).parent());

                var $row = $(row);
                $row.find("a").addClass("maintainHover");
                var target = $row.find("a").attr('href');
                $("#"+target).show();
            }

            // ************ lazy menu hide item
            function deactivateSubmenu(row) {
                $('.subMenu .maintainHover').removeClass('maintainHover');

                var $row = $(row);
                $row.find("a").removeClass("maintainHover");
                var target = $row.find("a").attr('href');
                $("#"+target).hide();
            }


            $('body').keydown(function(event) {
                var keycode = (event.keyCode ? event.keyCode : event.which);

                if(event.ctrlKey){
                    if(keycode == 77){
                        $logoDropdownBox.toggleClass('open');
                        $userMenuBackdoor.toggleClass('open');
                        $('.subMenuBox').hide();
                        $('.subMenuBox .maintainHover').removeClass('maintainHover');
                        if($logoDropdownBox.hasClass('open')){
                            $headerSearchInput.val('').keyup().focus();
                            $('.mainMenu .maintainHover').removeClass('maintainHover');
                            $('.mainMenu .currentMainActive').addClass('maintainHover');
                            activateSubmenu($('.maintainHover', $mainMenu).parent());
                        }
                    }
                }

                if($logoDropdownBox.hasClass('open') && $.trim($headerSearchInput.val()).length < 1){

                    // enter
                    if(keycode == '13'){
                        if($('.subMenu .maintainHover').length > 0){
                            var href = $('.subMenu .maintainHover').parent().attr('id');
                            var itemParts = href.split("_");
                            $('.userMenuBackdoor').click();
                            windowController.goToMenuPosition(itemParts[1] + "." + itemParts[2]);
                        //	openPage(href);
                        }
                    }

                    // down arrow
                    if(keycode == '40'){
                        // chek main or sub menu
                        if($('.subMenu .maintainHover').length < 1){
                            // main menu
                            var actualIndex = $('.maintainHover', $mainMenu).parent().index();
                            if(actualIndex == $('li', $mainMenu).length - 1){
                              actualIndex = -1;
                            }
                            activateSubmenu($('li:eq(' + (actualIndex + 1) + ')', $mainMenu));
                        } else {
                            // sub menu
                            var actualIndex = $('.maintainHover', '.subMenu:visible').parent().index();
                            $('.maintainHover', '.subMenu:visible').removeClass('maintainHover');
                            if(actualIndex == $('li', '.subMenu:visible').length - 1){
                              actualIndex = -1;
                            }
                            $('li:eq(' + (actualIndex + 1) + ') a', '.subMenu:visible').addClass('maintainHover');
                        }
                    }

                    // up arrow
                    if(keycode == '38'){
                        // chek main or sub menu
                        if($('.subMenu .maintainHover').length < 1){
                            // main menu
                            var actualIndex = $('.maintainHover', $mainMenu).parent().index();
                            if(actualIndex <= 0){
                                actualIndex = $('li', $mainMenu).length;
                            }
                            activateSubmenu($('li:eq(' + (actualIndex - 1) + ')', $mainMenu));
                        } else {
                            // sub menu
                            var actualIndex = $('.maintainHover', '.subMenu:visible').parent().index();
                            $('.maintainHover', '.subMenu:visible').removeClass('maintainHover');
                            if(actualIndex <= 0){
                                actualIndex = $('li', '.subMenu:visible').length;
                            }
                            $('li:eq(' + (actualIndex - 1) + ') a', '.subMenu:visible').addClass('maintainHover');
                        }
                    }

                    // right arrow
                    if(keycode == '39'){
                        if($('.subMenu .maintainHover').length < 1){
                            $('.subMenu:visible li:eq(0) a').addClass('maintainHover');
                        }
                    }

                    // left arrow
                    if(keycode == '37'){
                        if($('.subMenu .maintainHover').length > 0){
                            $('.subMenu a.maintainHover').removeClass('maintainHover');
                        }
                    }

                    // escape
                    if($logoDropdownBox.hasClass('open')) {
                        if (keycode == 27) {
                            $('.userMenuBackdoor').click();
                         }
                     }

                }
            });

		});
        function openPage(href){
			alert(href);
			$('.userMenuBackdoor').click();
		}
		window.goToPage = function(url, serializationObject, isPage) {
			windowController.goToPage(url, serializationObject, isPage);
		}

		window.openModal = function(id, url, serializationObject, onCloseListener) {
			windowController.openModal(id, url, serializationObject, onCloseListener);
		}

		window.changeModalSize = function(width, height) {
			windowController.changeModalSize(width, height);
		}

		window.goBack = function(pageController) {
			windowController.goBack(pageController);
		}

		window.closeThisModalWindow = function(status) {
			windowController.closeThisModalWindow(status);
		}

		window.getLastPageController = function() {
			return windowController.getLastPageController();
		}

		window.goToLastPage = function() {
			windowController.goToLastPage();
		}

		window.onSessionExpired = function(serializationObject, isModal) {
			windowController.onSessionExpired(serializationObject, isModal);
		}

		window.addMessage = function(message) {
		    message.closerTitle = window.translationsMap['qcadooView.notification.closerTitle'];
		    message.closeAllTitle = window.translationsMap['qcadooView.notification.closeAllTitle'];

			windowController.addMessage(message);
		}

		window.onLoginSuccess = function() {
			windowController.onLoginSuccess();
		}

		window.goToMenuPosition = function(position) {
			windowController.goToMenuPosition(position);
		}

		window.activateMenuPosition = function(position) {
 			windowController.activateMenuPosition(position);
 		}

		window.hasMenuPosition = function(position) {
			return windowController.hasMenuPosition(position);
		}

		window.updateMenu = function() {
			windowController.updateMenu();
		}

		window.getCurrentUserLogin = function() {
			return "${userLogin}";
		}

		window.translationsMap = new Object();
		<c:forEach items="${commonTranslations}" var="translation">
			window.translationsMap["${translation.key}"] = "${translation.value}";
		</c:forEach>
	
		
	</script>
</head>
<body>
    <div id="mainTopMenu" class="pageTopHeader clearfix">
        <div class="userMenuBackdoor"></div>
	    <div class="logoDropdownBox">
			<div class="logoDropdownBoxToggle">
				<div class="logo">
					<img src="${logoPath}" class="logoDark" alt="ricefish MES logo" onclick="windowController.goToDashboard()">
					<img src="/qcadooView/public/css/core/menu/images-new/qcadoo-white-logo.png" class="logoWhite" alt="ricefish MES logo" onclick="windowController.goToDashboard()">
				</div>
				<a href="#" class="arrow">
					<i></i>
				</a>
			</div>
			<div class="logoDropdownBoxContent">
				<div class="headerSearchForm">

					<div class="headerSearchFormContent">
						<input type="text" value="" />
						<i class="icon iconSearch"></i>
						<a href="#" class="iconDel hidden">&times;</a>
					</div>
				</div>
				<div class="headerMenuBox">
					<div class="headerMenuContent">
						<div class="headerMenuRowMain">
                            <ul class='mainMenu'></ul>
						</div>
						<div class="headerMenuRowSub">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="pageTitle">
        </div><div class="topTitle">${commonTranslations["smartView.menu.topTitle"] }</div>
		<div class="userMenu">
		        <ul>
        				<li><a href="http://manual.cloudmes.io//" target="_blank" class="help"><i class="icon iconHelp"></i> ${commonTranslations["qcadooView.button.help"] }</a></li>
        				<li><i class="icon iconUser"></i> <a href='#' id="profileButton" onclick="windowController.goToMenuPosition('administration.profile')">${userLogin}</a>
        					<div class="userMenuDropdown">
        						<a href="#" class="toggle"><i class="icon iconDropdown"></i></a>
        						<ul>
        							<li>
        							    <a href='#' onclick="windowController.performLogout()"><i class="icon iconLogout"></i>${commonTranslations["qcadooView.button.logout"] }</a>
        							</li>
        						</ul>
        					</div>
        				</li>
        		</ul>
        </div>

        <div class="activityStreamMenu">
            <a href="#" class="activityStreamIcon">
        	    <i></i>
          	</a>
        	<div class="activityStreamContainer">
        	</div>
        </div>

	</div>
	<div id="mainPageIframeWrapper"><iframe id="mainPageIframe" frameborder="0"></iframe></div>
</body>
</html>
