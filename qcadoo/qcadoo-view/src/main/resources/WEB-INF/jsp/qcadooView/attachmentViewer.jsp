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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/lib/imageviewer.css?ver=${buildNumber}" type="text/css" />

        <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-1.8.3.min.js?ver=${buildNumber}"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/imageviewer.min.js?ver=${buildNumber}"></script>

        <link href="${pageContext.request.contextPath}/qcadooView/public/img/core/icons/favicon.png" rel="shortcut icon" />

        <title>${fileName} :: ${applicationDisplayName}</title>
    </head>

    <body id="documentBody" style="height: 700px">
        <div id="attachmentModal" style="height: 100%" >
            <div style="width: 100%; height: 100%">
                <div style="height: 100%">
                    <div id="attachmentPreviewContainer" style="height: 100%"></div>
                </div>
            </div>
        </div>

        <script type="text/javascript" charset="utf-8">
            jQuery(document).ready(function() {
                function setWindowHeight() {
                    var windowHeight = window.innerHeight - 20;
                    document.getElementsByTagName('body')[0].style.height = windowHeight + "px";
                }

                setWindowHeight();

                window.addEventListener("resize",setWindowHeight,false);

                var attachment = "${attachment}";
                var ext = "${ext};

                var imagePreview = /^(jpe?g|gif|png|bmp)$/.test(ext);
                var viewerJsPreview = /^(pdf|odt|odp|ods)$/.test(ext);

                if (imagePreview || viewerJsPreview) {
                    var attachmentContainer = $("#attachmentPreviewContainer");
                    var imageViewer = attachmentContainer.data('imageViewer');

                    if (imageViewer) {
                        imageViewer = imageViewer.destroy();
                    }
                    attachmentContainer.empty();

                    if (imagePreview) {
                        imageViewer = ImageViewer(attachmentContainer, {zoomValue: 101});
                        attachmentContainer.data('imageViewer', imageViewer);
                        imageViewer.load("/"+attachment);
                    } else if (viewerJsPreview) {
                        var iframeViewerUrlPrefix = "/qcadooView/public/ViewerJS/index.htm#../../../..";
                        var iframe = $("<iframe/>").attr("src", iframeViewerUrlPrefix + "/"+attachment).attr("allowfullscreen", "").attr("webkitallowfullscreen", "").css({
                            width: "100%",
                            height: "100%"
                        });
                        attachmentContainer.append(iframe);
                    }
                } else {
                    var link = document.createElement("a");
                    link.href =  "/"+attachment;
                    link.click();
                }
            });
        </script>
    </body>

</html>
