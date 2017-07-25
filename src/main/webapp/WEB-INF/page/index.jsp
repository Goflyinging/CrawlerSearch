<%@page language="java" pageEncoding="utf-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="basePath" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html class="no-js" lang="">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/main.css">
<script type="text/javascript" src="${basePath}/resources/js/jquery-3.0.0.min.js"></script>
<script type="text/javascript" src="${basePath}/resources/js/bootstrap.min.js"></script>
<style type="text/css">
    .searchbox {
        margin-top: 8%;
        height: 200px;
    }

    .footer {
        padding-top: 5px;
        position: absolute;
        height: 40px;
        width: 100%;
        bottom: 0px;
        background-color: #f2f2f2;
        text-align: center;
        font-size: 20px;
    }
</style>
<head>
</head>
<body>
<div class="container-fluid">
    <div class="row searchbox">
        <div class="col-lg-3"></div>
        <div class="col-lg-6" style="text-align: center">
            <img src="${basePath}/resources/img/l.gif">
        </div>
        <div class="col-lg-3"></div>
    </div>
    <div class="row">
        <div class="col-lg-3"></div>
        <form role="form" id="searchForm" action="${basePath}/article/search" method="post">
            <div class="col-lg-6" style="text-align: center">
                <div class="input-group input-group-lg ">
                    <input type="text" class="form-control" name="queryString" id="searchText"
                           placeholder="Search for...">
                    <span class="input-group-btn">
            <button class="btn btn-default" type="button">Go!</button>
            </span>
                </div>
            </div>
        </form>
        <div class="col-lg-3"></div>
    </div>
    <div class="row footer">
        <div class="col-lg-2"></div>
        <div class="col-lg-2"></div>
        <div class="col-lg-2"></div>
        <div class="col-lg-2"></div>
        <div class="col-lg-2"></div>
        <div class="col-lg-2">made by lxing</div>
    </div>
</div>

</body>
<script type="text/javascript">
    function checkSearchText(result) {
        if (result == "") {
            alert("搜索内容不能为空");
            return false;
        }
        return true;
    }
    $(":button").click(function () {
        var result = $("#searchText").val().trim();
        if(checkSearchText(result)){
            $("form:first").submit();
        }else{
            return false;
        }

    });
</script>
</html>

