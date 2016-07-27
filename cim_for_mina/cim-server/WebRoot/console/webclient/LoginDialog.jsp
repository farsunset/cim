	<%@ page language="java" pageEncoding="utf-8"%>
	<%
	String loginBasePath = request.getScheme() + "://"
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
			document.getElementById("CIMBridge").connect(CIM_HOST,CIM_PORT);
		}
		  
   </script>
 <div class="modal-dialog" id="LoginDialog" style="width: 300px;display:none;">
		<div class="modal-content">
			<div class="modal-body" style="padding:0px;">

            <div  style="height:150px;text-align: center; background: #5FA0D3; color: #ffffff; border: 0px; border-top-left-radius: 4px; border-top-right-radius: 4px;">
	        <img src="<%=loginBasePath %>/resource/img/icon.png" style="height: 60px;width: 60px;margin-top:30px;"/>
	        <div style="margin-top: 20px; color: #ffffff;font-size: 16px;">使用用户帐号登录</div>
 		    </div>
		   
	        	<div class="input-group" style="margin-top: 20px;margin-left:10px;margin-right:10px;margin-bottom:20px;">
	        	  <span class="input-group-addon"><span class="glyphicon glyphicon-user" aria-hidden="true"></span></span>
				  <input type="text" class="form-control" id="account" maxlength="32" placeholder="帐号"
					style="display: inline; width: 100%; height: 50px;" />
				</div>	 
			 
		    </div>
			<div class="modal-footer" style="text-align: center;">
				<a type="button" class="btn btn-success btn-lg" onclick="doLogin()"
					style="width: 250px;">登录</a>
			</div>
      </div>
</div>

 