	<%@ page language="java" pageEncoding="utf-8"%>
	<%
	String lbasePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath();
			
   %>
	<script type="text/javascript">
     function doLogin()
	{
		    ACCOUNT = $('#account').val();
		    if($.trim(ACCOUNT)=='' )
		    {
		       return;
		    }
		    showProcess('正在接入请稍后......');
			document.getElementById("CIMBridge").connect(CIM_HOST);
		}
		  
   </script>
 	 	<div class="panel panel-primary gdialog" id="LoginDialog"
						style="display: none; width: 300px; position: absolute;">
						<div class="panel-heading">
							登录
						</div>
						 <div style="text-align: center;background: #5FA0D3;height: 150px;">
									<img src="<%=lbasePath%>/resource/img/icon.png" style="margin-top: 35px;height: 80px;height: 80px;"/>
								</div>
						
						<div class="panel-body">
						       <div class="alert alert-info">
						  登录之前请在 cim.js里面设置当前服务器的IP地址
						</div>
								<div class="form-group" style="margin-top: 20px;">
									<label style="width: 50px;">
										<font color="red">*</font>账号:
									</label>
									<input type="text" class="form-control" id="account"
										maxlength="15" style="display: inline; width: 200px;height: 50px;" />
								</div>
						</div>
						<div class="panel-footer" style="text-align: center;">
						     <a type="button" class="btn btn-success btn-lg" onclick="doLogin()"  style="width: 200px;"> 登录</a>
						</div>
					</div>