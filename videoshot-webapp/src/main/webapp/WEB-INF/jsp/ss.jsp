<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utils" uri="utils" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="ssList" scope="request" type="java.util.List<videoshot.webapp.model.ScreenshotModel>"/>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Screenshot</title>
    <script type="text/javascript" src="js/jquery-2.0.2.js"></script>
</head>
<style>
    .ss_image {
        position: relative;
        width: 100%; /* for IE 6 */
    }

    .ss_image h2 {
        position: absolute;
        top: 20px;
        left: 0;
        width: 100%;
    }

    .ss_image h2 span {
        color: white;
        font: bold 6px/8px Helvetica, Sans-Serif;
        letter-spacing: -1px;
        background: rgb(0, 0, 0); /* fallback color */
        background: rgba(0, 0, 0, 0.7);
        padding: 10px;
    }

    .ss_image h2 span.spacer {
        padding:0 5px;
    }

    div.main_grid {
        width: 1600px;
        margin-left: auto;
        margin-right: auto;

        text-align: left;
    }

    div.each_image {
        width: 360px;
        margin-right: 3px;
        float: left;
        padding:10px;
        /*background-image: url('http://subtlepatterns.subtlepatterns.netdna-cdn.com/patterns/debut_dark.png'); *//*Image From SubtlePatterns.com*/
        margin-bottom: 3px;
    }

    div.each_image:nth-child(4n) {
        margin-right:0px;
    }

</style>
<body>

<div>
    <label>Video screenshot is in period of ${periodInSec} seconds</label>

    <form action="chop.html" method="GET">
        <input name="id" type="hidden" value="${param['id']}" />
        <label>Start microsec</label>
        <input type="text" name="startMicroSec" value="" />
        <label>End microsec</label>
        <input type="text" name="endMicroSec" value="" />
        <input type="submit" value="Chop!!!" />
    </form>

    <div class="main_grid">
        <c:forEach var="ss" items="${ssList}">
            <fmt:formatNumber var="timeStampSecond" value="${ss.timeStampInMicroSec / 1000000}"
                              maxFractionDigits="0" />
            <c:set var="isInRange"
                   value="${startMicroSec == null || endMicroSec == null ||
                   (ss.timeStampInMicroSec >= startMicroSec && ss.timeStampInMicroSec <= endMicroSec) }" />
            <c:if test="${ timeStampSecond % periodInSec == 0 && isInRange}">
            <div class="each_image">
                <c:if test="${periodInSec != 1}">
                    <a href="?periodInSec=1&startMicroSec=${ss.timeStampInMicroSec}&endMicroSec=${ss.timeStampInMicroSec + (periodInSec * 1000000)}&id=${param['id']}">
                        expand
                    </a>
                </c:if>

                <div class="ss_image">
                    <c:set var="img64" value="${utils:base64(utils:fileToByte(utils:getFileByPath(ss.imagePath)))}" />
                    <img width="100%" src="data:image/gif;base64,${img64}" />
                    <h2><span>${ss.timeStampInMicroSec} : ${utils:formatMs(ss.timeStampInMicroSec / 1000)}</span><span class="spacer"></span></h2>
                </div>
            </div>
            </c:if>
        </c:forEach>
    </div>
</div>

</body>