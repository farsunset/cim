<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.farsunset.cim.sdk.server.session.CIMSession"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
			 
	List<CIMSession> sessionList  = (List<CIMSession> )request.getAttribute("sessionList");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<title>CIM管理系统</title>

		<link charset="utf-8" rel="stylesheet" 	href="<%=basePath%>/resource/bootstrap-3.3.6-dist/css/bootstrap.min.css" />
		<link charset="utf-8" rel="stylesheet" href="<%=basePath%>/resource/css/base-ui.css" />
		<script type="text/javascript" 	src="<%=basePath%>/resource/js/jquery-2.2.3.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=basePath%>/resource/js/framework.js"></script>
		<script>
	 
		  
		  function showMessageDialog(account)
			{
			   doShowDialog("messageDialog");
			   $('#Saccount').val(account);
			   
			}
		 function doSendMessage()
		  {
		    var message = $('#message').val();
		    var account = $('#Saccount').val();
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
		  
		  
		  function onImageError(obj)
			{
			    obj.src="<%=basePath%>/resource/img/icon.png";   
			}
			
		  function  openWebclient(){
		     window.open ("<%=basePath%>/console/webclient/main.jsp", "","height="+800+", width="+600+", top=0, left=0, toolbar=no,menubar=no, scrollbars=no, resizable=no,location=no, status=no");
		  }


		</script>
	</head>
	<body class="web-app ui-selectable">


		<%@include file="../header.jsp"%>

		<%@include file="../nav.jsp"%>

		<div id="mainWrapper">
		
		<div class="lay-main-toolbar">
                   
           </div>
           
           
		 
				<div>
					<form action="<%=basePath%>/admin/user_manage.action" method="post"
						id="searchForm" style="padding: 0px;">
						<input type="hidden" name="currentPage" id="currentPage" />
						<table style="width: 100%" class="utable">

							<thead>
								<tr class="tableHeader">
                                    <th width="4%">头像</th>
									<th width="15%">账号</th>
									<th width="10%">终端</th>
									<th width="10%">终端版本</th>
									<th width="10%">应用版本</th>
									<th width="10%">设备型号</th>
									<th width="10%">在线时长</th>
									<th width="28%">位置</th>
									<th width="12%">操作</th>
								</tr>
								 
							</thead>
							<tbody id="checkPlanList">

                                <%
                                  for(CIMSession ios:sessionList)
                                  {
                                    if(ios.getAccount()!=null)
                                    {
                                 %>
                                 	<tr id="<%=ios.getAccount() %>" style=" height: 50px;">
                                        <td>
											<img width="40px" height="40px" onerror='onImageError(this)' src="http://cim.oss-cn-hangzhou.aliyuncs.com/UI_<%=ios.getAccount() %>"/>
										</td>
										<td>
											<%=ios.getAccount() %>
										</td>
										<td>
											<%=ios.getChannel()%>
										</td>
										<td>
											<%=ios.getSystemVersion()==null?"":ios.getSystemVersion()%>
										</td>
										<td>
											<%=ios.getClientVersion()==null?"":ios.getClientVersion()%>
										</td>
										<td>
											<%=ios.getDeviceModel()==null?"":ios.getDeviceModel()%>
										</td>
										<td>
										    <%=(System.currentTimeMillis()-ios.getBindTime())/1000 %>秒
										</td>
										<td>
										   <%=ios.getAttribute("location")==null?"":ios.getAttribute("location") %>
										</td>
										<td>
											<div class="btn-group btn-group-xs">
											  <button type="button" class="btn btn-primary" style="padding: 5px;" 
onclick="showMessageDialog('<%=ios.getAccount() %>')"><span class="glyphicon glyphicon-send" style="top:2px;"></span>  发送消息</button>
											   
											</div>
										</td>
									</tr>	
								<%}} %>		
								 
						 
							</tbody>
							 
						</table>
					</form>

				</div>
			</div>
			
			
<div class="modal fade" id="messageDialog" tabindex="-1" role="dialog" >
		<div class="modal-dialog" style="width: 420px;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	                <h4 class="modal-title">发送消息</h4>
				</div>
				<div class="modal-body">
					<div class="form-groupBuy">
							<label for="Amobile">
								接收账号:
							</label>
							<input type="text" class="form-control" id="Saccount"
								name="account"
								style="width: 100%; font-size: 20px; font-weight: bold;height:40px;"
								disabled="disabled" />
						</div>
						<div class="form-groupBuy" style="margin-top: 20px;">
							<label for="exampleInputFile">
								消息内容:
							</label>
							<textarea rows="10" style="width: 100%; height: 200px;"
								id="message" name="message" class="form-control"></textarea>
						</div>
				</div>
				<div class="modal-footer" style="padding: 5px 10px; text-align: center;">
					<button type="button" class="btn btn-success btn-lg" style="width: 200px;" onclick="doSendMessage()">
						<span class="glyphicon glyphicon-send" style="top:2px;"></span> 发送
					</button>
				</div>
			</div>
		</div>
</div>



		<script>
		       $('#sessionMenu').addClass('current');
		       
		</script>
	</body>
</html>
