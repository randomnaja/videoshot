<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utils" uri="utils" %>

<jsp:useBean id="ssList" scope="request" type="java.util.List<videoshot.webapp.model.ScreenshotModel>"/>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Screenshot</title>
</head>
<body>

<div>
    <ul>
        <c:forEach var="ss" items="${ssList}">
            <li>
                ${ss.timeStampInMicroSec} :
                ${ss.imagePath}
                <c:set var="img64" value="${utils:base64(utils:fileToByte(utils:getFileByPath(ss.imagePath)))}" />
                <img src="data:image/gif;base64,${img64}" />
            </li>
        </c:forEach>
    </ul>
</div>

</body>