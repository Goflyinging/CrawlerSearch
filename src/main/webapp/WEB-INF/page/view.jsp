<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="basePath" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html class="no-js" lang="">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/main.css">
<script type="text/javascript" src="${basePath}/resources/js/jquery-3.0.0.min.js"></script>
<script type="text/javascript" src="${basePath}/resources/js/bootstrap.min.js"></script>
<body>
<h2>Hello World!</h2>
<ul>
    <c:forEach items="${requestScope.ArticleList}" var="article">
        <li>
                ${article.id}
        </li>
        <li>
                ${article.title}
        </li>
        <li>
                ${article.content}
        </li>
    </c:forEach>
</ul>
</body>
</html>
