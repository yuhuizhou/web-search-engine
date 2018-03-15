<%@ page import="java.util.ArrayList" %>
<%@ page import="com.dao.Page" %>
<%@ page import="com.dao.Article" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: zhouyuhui
  Date: 17-12-19
  Time: 下午8:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
  <title>My Search</title>
</head>
<body>
<jsp:useBean id="article" class="com.dao.Article" scope="session"/>
<form action="<%= basePath %>Search" method="post">

  <input type="text" name="keyWord"  autocomplete="off" placeholder="请输入关键字"/>
  <input type="submit" value="搜索" />
</form>
<div>
  <table >





      <%
          for (int i = 0,num=article.getArticleList().size(); i < num;)
          {
              String title = article.getArticleList().get(i).getTitle();
              String summary = article.getArticleList().get(i).getSummary();
              String href = article.getArticleList().get(i).getHref();


      %>
      <tr>
          <td><%= ++i %></td>
          <td><%= title %></td>
          <td><%= summary %></td>
          <td><%= href %></td>
      </tr>
      <%}
      %>


  </table>
</div>
</body>


</html>