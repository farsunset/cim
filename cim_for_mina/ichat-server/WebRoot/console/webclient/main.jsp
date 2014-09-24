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
<title>ICHAT for  web(beta)  </title>
		<link charset="utf-8" rel="stylesheet" 	href="<%=basePath%>/resource/bootstrap/css/bootstrap.min.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/dialog.css" />
		<script type="text/javascript" 	src="<%=basePath%>/resource/js/jquery-1.8.3.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/js/framework.js"></script>
		<script type="text/javascript" src="cim.js"></script>
</head>


<script type="text/javascript"> 


   /**CIMBridge.swf提供的接口,源码是CIMBridge工程  用flahs builder 工具 导入开发。
       connect(host);连接服务端 host:服务器IP
       setAccount(account) 绑定客户端账号到服务端 account账号
       logout()  退出连接
       getOfflineMessage(account) 拉取离线消息，需要服务端实现 请求key  client_get_offline_message
       
       CIMBridge.swf回调的接口
       bridgeCreationComplete()
       sessionCreated()
       sessionClosed()
       onReplyReceived()
       onMessageReceived()
   **/
   
   
   
   
   /**  当socket连接成功回调 **/
   function sessionCreated()
   {
      document.getElementById("CIMBridge").setAccount(ACCOUNT,ACCOUNT);
   }

   /**  当socket断开是回调   **/
   function sessionClosed()
   {
   }
   
   
   /** 当收到请求回复时候回调  **/
   function onReplyReceived(data)
   {
     var json = JSON.parse(data)
     if(json.key=='client_bind' && json.code==200)
     {
        hideProcess();
        doHideDialog('LoginDialog');
        $("#MessageDialog").fadeIn();
        $("#global_mask").fadeIn();
        $("#current_account").text("当前账号:"+ACCOUNT);
        
     }
   }
   
   /** 当收到消息时候回调  **/
   
   function onMessageReceived(data,content)
   { 
        
        var message = JSON.parse(data);
        
        if(message.type=='2')
        {
            document.getElementById("CIMBridge").playSound();
            message.content = content;
            $("#messageList").append("<div class='alert alert-info' >"+content+"</div>");
        }
        
        
   }
   
   /**  当flex socket 组件(CIMBridge.swf) 加载完成是回调  **/
   
   function bridgeCreationComplete(){
         hideProcess();
   }
   
   
    $(document).ready(function(){
   
       showProcess("加载中......");
       doShowDialog('LoginDialog');
       
    });
    
    
   
</script>
<script   language="Javascript">   
   document.oncontextmenu = function   (){   
      return   false;   
  }  
  
  
  window.onload=function()
{
	 window.onkeydown=function(e)
	 {
		   if(e.which)
		   {
		     if(e.which==116)
		     {       
		       return false;       
		     }
		   }
		   else if(event.keyCode)
		   {
			  if(event.keyCode==116)
			  {
			    return false;   
			  }
		   }
	}
  }   
  </script>

<body style="background-color: rgb(190, 209, 216);width: 600px;">
<object type="application/x-shockwave-flash" data="CIMBridge.swf" id="CIMBridge"  width="0" height="0"> 
				<param name="quality" value="low"/>
				<param name="allowScriptAccess" value="always"/>
				<param name="wmode" value="transparent"/>
				<param name="movie" value="CIMBridge.swf"/>
</object>
<div id="global_mask" style="display: none; position: absolute; top: 0px; left: 0px; z-index: 998; background-color: rgb(190, 209, 216); opacity: 0.5; width: 100%; height: 100%; overflow: hidden; background-position: initial initial; background-repeat: initial initial;"></div>

<%@include file="LoginDialog.jsp"%>
<%@include file="MessageDialog.jsp"%>
</body>
</html>