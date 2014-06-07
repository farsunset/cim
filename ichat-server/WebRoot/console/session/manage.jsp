<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="java.util.Collection"%>
<%@ page import="com.farsunset.ichat.common.util.StringUtil"%>
<%@ page import="org.apache.mina.core.session.IoSession"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
			 
	Collection<IoSession> sessionList  = (Collection<IoSession>)request.getAttribute("sessionList");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<title>CIM管理系统</title>

		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/webbase.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/main-layout.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/base-ui.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/table.css" />
		<link charset="utf-8" rel="stylesheet" 	href="<%=basePath%>/resource/bootstrap/css/bootstrap.min.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/dialog.css" />
		<script type="text/javascript" 	src="<%=basePath%>/resource/js/jquery-1.8.3.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/js/framework.js"></script>
		<script>
		
		  function doOffLine(account)
		  {
		     var setting = {hint:"确定让这个用户强制下线吗?",
		                    onConfirm:function(){
		                      $.post("<%=basePath%>/admin/session_offline.action", {account:account},
							  function(data){
							      showSTip("下线成功");
					              $('#'+account).fadeOut().fadeIn().fadeOut();
					              doHideConfirm();
						      });
		                     
		                    }};
		     
		     doShowConfirm(setting);
		  }
		  
		  function showMessageDialog(account)
			{
			   doShowDialog("messageDialog");
			   $('#account').val(account);
			   
			}
		 function doSendMessage()
		  {
		    var message = $('#message').val();
		    var account = $('#account').val();
		    if($.trim(message)=='')
		    {
		       return;
		    }
		    showProcess('正在发送，请稍后......');
		    $.post("<%=basePath%>/cgi/message_send.action", {content:message,type:2,sender:'system',receiver:account},
			   function(data){
			   
			      hideProcess();
			      showSTip("发送成功");
			      doHideDialog("messageDialog");
			      
		     });
		  }
		  
		</script>
	</head>
	<body class="web-app ui-selectable">


		<%@include file="../header.jsp"%>

		<%@include file="../nav.jsp"%>

		<div id="mainWrapper">
			<div class="panel panel-default">
				<div class="panel-heading">
					在线用户
				</div>
				<div class="panel-body" style="padding: 5px;">
					<form action="<%=basePath%>/admin/user_manage.action" method="post"
						id="searchForm" style="padding: 0px;">
						<input type="hidden" name="currentPage" id="currentPage" />
						<table style="margin: 5px;width: 100%" class="utable">

							<thead>
								<tr class="tableHeader">
									<th width="20%">账号</th>
									<th width="20%">终端</th>
									<th width="20%">在线时长</th>
									<th width="20%">心跳时间</th>
									<th width="40%">操作</th>
								</tr>
								 
							</thead>
							<tbody id="checkPlanList">

                                <%
                                  for(IoSession ios:sessionList)
                                  {
                                
                                 %>
                                 	<tr id="<%=ios.getAttribute("account") %>">
										<td>
											<%=ios.getAttribute("account") %>
										</td>
										<td>
											<%=ios.getAttribute("channel") %>
										</td>
										<td>
										    <%=(System.currentTimeMillis()-Long.valueOf(ios.getAttribute("loginTime").toString()))/1000 %>秒
										</td>
										<td>
										    <%if(ios.getAttribute("heartbeat")!=null){ %>
										    <%=StringUtil.transformDateTime(Long.valueOf(ios.getAttribute("heartbeat").toString())) %>
										    <%} %>
										</td>
										<td>
											<div class="btn-group btn-group-xs">
											  <button type="button" class="btn btn-primary" style="padding: 5px;" onclick="showMessageDialog('<%=ios.getAttribute("account") %>')">发送消息</button>
											  <button type="button" class="btn btn-danger"  style="padding: 5px;" onclick="doOffLine('<%=ios.getAttribute("account") %>')">强制下线</button>
											</div>
										</td>
									</tr>	
								<%} %>		
								 
						 
							</tbody>
							<tfoot>
								 
							</tfoot>
						</table>
					</form>

				</div>
			</div>
			
			
		<div class="panel panel-primary gdialog" id="messageDialog" style="display: none;width: 420px;position: absolute;z-index: 1001;">
		  <div class="panel-heading">发送消息
		  <a class="close"  onclick="doHideDialog('messageDialog'),$('#messageDialog').css('z-index',1000);">&times;</a>
		  </div>
		  <div class="panel-body">
		   <form role="form">
		      <div class="form-groupBuy">
			    <label for="Amobile">
					接收者账号:
				</label>
				<input type="text" class="form-control" id="account" name="account"
					  style="  width: 100%;font-size: 20px;font-weight: bold;" disabled="disabled" />
			  </div>
			  <div class="form-groupBuy" style="margin-top: 20px;">
			    <label for="exampleInputFile" style="padding-left: 7px;">消 息 内 容:</label>
			    <textarea rows="10" style="width: 100%;height: 120px;" id="message" name="message"  class="form-control"></textarea>
			  </div>
			   <div class="form-groupBuy" style="margin-top: 20px;">
			      <center>
				     <button type="button" style="width: 150px;"  class="btn btn-success btn-lg" onclick="doSendMessage()">发 送</button>
			      </center>
			   </div>
			   
			</form>
		  </div>
		</div>

		<script>
		       $('#sessionMenu').addClass('current');
		       
		</script>
	</body>
</html>