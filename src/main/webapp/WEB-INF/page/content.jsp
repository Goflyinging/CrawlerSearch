<%@page language="java" pageEncoding="utf-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="basePath" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html class="no-js" lang="">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/main.css">
<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/mricode.pagination.css">
<script type="text/javascript" src="${basePath}/resources/js/jquery-3.0.0.min.js"></script>
<script type="text/javascript" src="${basePath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${basePath}/resources/js/mricode.pagination.js"></script>
<style type="text/css">
    .header {
        position: fixed;
        top: 0px;
        padding-top: 5px;
        height: 50px;
        border-bottom: 1px solid #f8f8f8;
        z-index: 111111;
        background-color: #fafafb;
    }

    .content {
        margin-top: 50px;
    }

    .articleBox {
        border-right: 1px solid #f8f8f8;
    }

    .articlePanel {
        padding: 0px 0px;
        border: none;
        margin-bottom: 0px;
    }

    .articleBody {
        padding: 10px;
        border: none;
    }

    /*文章标题*/

    .title a {
        text-decoration: underline;
        font-size: 17px;
    }

    /*文章内容*/
    /*文章信息*/
    .info {
        color: #666666;
    }

    .author {
        float: left;

    }

    .time {
        margin-left: 10px;
        float: left;
    }
</style>
<body>
<div class="container-fluid">
    <%--搜索头--%>
    <div class="row header">
        <div class="col-lg-1"></div>
        <div class="col-lg-5">
            <form role="form" id="searchForm" action="${basePath}/article/search" method="post">
                <div class="input-group">
                    <input type="text" class="form-control" name="queryString" id="searchText"
                           value="${requestScope.queryString}">
                    <span class="input-group-btn">
                            <button class="btn btn-default" type="button">Go!</button>
                        </span>
                </div>
            </form>
        </div>
        <div class="col-lg-6"></div>
    </div>
    <%--搜索内容--%>
    <div class="row content">
        <div class="col-lg-1"></div>
        <div class="col-lg-7 articleBox">
            <%--文章内容--%>
            <div id="Box"></div>
            <%--分页--%>
            <div id="page" class="m-pagination"></div>
        </div>
        <div class="col-lg-4"></div>
    </div>
</div>
</body>
<script type="text/javascript">
    var $articleBox = $('#Box');
    function checkSearchText(result) {
        if (result == "") {
            alert("搜索内容不能为空");
            return false;
        }
        return true;
    }
    $(":button").click(function () {
        var result = $("#searchText").val().trim();
        if (checkSearchText(result)) {
            $("form:first").submit();
        } else {
            return false;
        }

    });
    $(document).ready(function () {
        //查询的内容
        var query = $("#searchText").val().trim();
        $("#page").pagination({
            showFirstLastBtn: true,
            firstBtnText: '首页',
            lastBtnText: '尾页',
            prevBtnText: '上一页',
            nextBtnText: '下一页',
            pageIndex: 0,
            pageSize: 5,
            loadFirstPage: true,
            remote: {
                url: '${basePath}/article/list',
                params: {
                    queryString: query,
                },
                success: function (data) {
                    var articleList = data.list;
                    var trs = "";
                    if (null == articleList) {
                        trs = "您查找的内容不存在！！！"
                        return;
                    } else {
                        $.each(articleList, function (n, value) {
                            trs += "<div  class='panel panel-default articlePanel'><div class='panel-body articleBody'> <div class='title'> <a href='"
                                + value.url +
                                "' target='_blank'>"
                                + value.title +
                                "</a></div> <div class='article'>"
                                + value.content +
                                "</div><div class='info'><div class='author'><span>作者：</span>"
                                + value.author +
                                "</div> <div class='time'><span>时间：</span>"
                                + value.date +
                                "</div> </div> </div></div>";
                        });
                    }
                    $articleBox.empty();
                    $articleBox.append(trs);
                },
                totalName: 'totalRecord'
            }
        });
    });

</script>
</html>
