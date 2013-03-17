<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="allVideos" scope="request" type="java.util.List<videoshot.webapp.model.VideoModel>"/>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Video</title>
</head>
<body>

<div>
    <ul>
        <c:forEach var="video" items="${allVideos}">
            <li>Video : <a href="ss.html?id=${video.id}">${video.sourceVideoPath}</a>
            </li>
        </c:forEach>
    </ul>
</div>

</body>