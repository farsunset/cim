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
<title>CIM for Web</title>
<link charset="utf-8" rel="stylesheet" 	href="<%=basePath%>/resource/bootstrap-3.3.6-dist/css/bootstrap.min.css" />
<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/base-ui.css" />
<script type="text/javascript" 	src="<%=basePath%>/resource/js/jquery-2.2.3.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/resource/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/resource/js/framework.js"></script>
<script type="text/javascript" src="cim.js"></script>



<script type="text/javascript"> 


   /**CIMBridge.swf提供的接口,源码是CIMBridge工程  用flahs builder 工具 导入开发。
       connect(host,port);连接服务端 host:服务器IP ,port: CIM端口
       bindAccount(account,deviceId) 绑定客户端账号到服务端 account账号
       logout()  退出连接
       getOfflineMessage(account) 拉取离线消息，需要服务端实现 请求key  client_get_offline_message
       
       CIMBridge.swf回调的接口
       
       flashBridgeCreated()
       sessionCreated()
       sessionClosed()
       sessionFailed()
       onReplyReceived()
       onMessageReceived()
   **/
   
   
   
   /**  当socket连接成功回调 **/
   function sessionCreated()
   {
       
      //使用session id作为唯一标示，区分同一设备的多个用户
      document.getElementById("CIMBridge").bindAccount(ACCOUNT,"<%=session.getId()%>");
   }

   /**  当socket断开时回调，通知用，并不需要做其他操作   **/
   function sessionClosed()
   {
         //这里可以进行页面上的操作
   }
   
    /**  当socket连接服务器失败时回调，通知用，并不需要做其他操作   **/
   function sessionFailed()
   {
        //这里可以进行页面上的操作
   }
   
   
   /** 当收到请求回复时候回调  **/
   function onReplyReceived(data)
   {
     var json = JSON.parse(data)
     if(json.key=='client_bind' && json.code==200)
     {
        hideProcess();
        $("#LoginDialog").hide();
        $("#MessageDialog").fadeIn();
        $("#current_account").text("当前账号:"+ACCOUNT);
        
     }
   }
   
   /** 当收到消息时候回调  **/
   
   function onMessageReceived(data)
   { 
       
        var message = JSON.parse(data);
        if(message.type=='2')
        {
            document.getElementById("CIMBridge").playSound("dingdong.mp3");
            var line = "<div class='alert alert-info' id ="+message.timestamp+" STYLE='white-space: nowrap; overflow: hidden; text-overflow: ellipsis;'></div>";
            $("#messageList").append(line);
            $("#"+message.timestamp).text(message.content);
        }
        
        
   }
   
   /**  当flex socket 组件(CIMBridge.swf) 加载完成是回调  **/
   
   function flashBridgeCreated(){
         hideProcess();
   }
   
   
    $(document).ready(function(){
   
       showProcess("加载中......");
       $("#LoginDialog").show();
    });
    
    
</script>
 
</head>
<body style="background-color: rgb(190, 209, 216);width: 600px;">
<object type="application/x-shockwave-flash" data="CIMBridge.swf" id="CIMBridge"  width="0px" height="0px"> 
				<param name="quality" value="low"/>
				<param name="allowScriptAccess" value="always"/>
				<param name="wmode" value="transparent"/>
				<param name="movie" value="CIMBridge.swf"/>
</object>

<%@include file="LoginDialog.jsp"%>
<%@include file="MessageDialog.jsp"%>
</body>
</html>