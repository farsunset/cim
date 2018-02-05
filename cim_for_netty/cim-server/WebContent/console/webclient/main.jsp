<%@ page language="java" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
			
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<meta charset="utf-8"/>
<title>cim for  web(beta)  </title>
		<link charset="utf-8" rel="stylesheet" 	href="<%=basePath%>/resource/bootstrap-3.3.6-dist/css/bootstrap.min.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/dialog.css" />
		<script type="text/javascript" 	src="<%=basePath%>/resource/js/jquery-2.2.3.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/js/framework.js"></script>
		<script type="text/javascript" src="cim.web.sdk.js"></script>
</head>


<script type="text/javascript"> 

   
   /**  当socket连接成功回调 **/
   function onConnectionSuccessed()
   {
	   var sessionId = '<%=session.getId()%>';
	   CIMWebBridge.bindAccount($('#account').val(),sessionId);
   }
   
   /** 当收到请求回复时候回调  **/
   function onReplyReceived(json)
   {
     if(json.key=='client_bind' && json.code==200)
     {
        hideProcess();
        doHideDialog('LoginDialog');
        doShowDialog('MessageDialog');
        $("#current_account").text(ACCOUNT);
        
     }
   }
   
   /** 当收到消息时候回调  **/
   
   function onMessageReceived(message)
   { 
	   if(message.acction == ACTION_999){
		   doHideDialog('MessageDialog');
	       doShowDialog('LoginDialog');
		   return ;
	   }
       $("#messageList").append("<div class='alert alert-info' >"+message.content+"</div>");
   }
   
    $(document).ready(function(){
       doShowDialog('LoginDialog');
    });
    
    
   
</script>
 

<body style="background-color: rgb(190, 209, 216);width: 600px;">
<div id="global_mask" style="display: none; position: absolute; top: 0px; left: 0px; z-index: 998; background-color: rgb(190, 209, 216); opacity: 0.5; width: 100%; height: 100%; overflow: hidden; background-position: initial initial; background-repeat: initial initial;"></div>

<%@include file="loginDialog.jsp"%>
<%@include file="messageDialog.jsp"%>
</body>
</html>