<%@ page language="java"   pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<title>CIM管理系统</title>

	    <link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/webbase.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/main-layout.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/base-ui.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/bootstrap/css/bootstrap.min.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/dialog.css" />
		<script type="text/javascript" src="<%=basePath%>/resource/js/jquery-1.8.3.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/js/framework.js"></script>
	 
	</head>
	<body class="web-app ui-selectable">


           <br />&lt;<br /><%@include file="header.jsp"%>

           <%@include file="nav.jsp"%>

			<div id="mainWrapper" style="padding-top: 25px;" >
				  
			</div>
			 <script>
		      $('#indexMenu').addClass('current');
		      </script>
	</body>
	
           
</html>
