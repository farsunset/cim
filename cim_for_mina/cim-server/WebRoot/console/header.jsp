<%@ page language="java" pageEncoding="UTF-8"%>

<%
	String headerBasePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath();
	Object admin = session.getAttribute("admin");
%>
<script type="text/javascript">
  function showUserMenu()
  {
      if($('#_main_face_menu').is(":hidden"))
      {
         $('#_main_face_menu').fadeIn();
        $('.user-avatar').addClass('user-avatar-hover');
      }else
      {
         $('#_main_face_menu').hide();
         $('.user-avatar').removeClass('user-avatar-hover');
      }
      
  }
  
  
  $(function(){
     $(".user-avatar").mouseenter(function(){
       $(".user-avatar").addClass('user-avatar-hover');
       $('#_main_face_menu').fadeIn();
     });
     
     $("#_main_face_menu").mouseleave(function(){
       $(".user-avatar").removeClass('user-avatar-hover');
       $('#_main_face_menu').fadeOut();
     });
 
  });
   
 
		  
</script>


<div id="_main_fixed_header" class="header-fixed">

	<!-- 头部 -->
	<div id="_main_header_banner" class="header">
		<div id="_main_header_cnt" class="header-cnt">
			<div class="logo" style="left: -200px;">
			</div>

			<div class="btn-group" style="float: right; margin-top: 50px;">
				<a type="button" class="btn btn-primary"
					onclick="doShowDialog('aboutDialog')">关于</a>
			</div>
		</div>

	</div>

	<!--web的导航在左侧-->

</div>


<div class="panel panel-primary gdialog" id="aboutDialog"
	style="display: none; width: 400px; position: absolute;">
	<div class="panel-heading">
		关于
		<a class="close" onclick="doHideDialog('aboutDialog')">&times;</a>
	</div>
	<div class="panel-body" style="padding: 0px;">
	    <div style="text-align: center;background: #5FA0D3;height: 150px;">
									<img src="<%=headerBasePath%>/resource/img/icon.png" style="margin-top: 35px;height: 80px;height: 80px;"/>
		</div>
		<ul class="list-group">
			<li class="list-group-item" style="border-radius: 0px;">
				CIM是一个提供于二次开发的即时通信解决方案，可实现基于移动应用，桌面端应用或者系统应用之间的实时消息推送。
				<p/><p/>项目主页地址<br/><a  target="_blank" href="http://git.oschina.net/farsunset/cim">http://git.oschina.net/farsunset/cim</a>
			</li>
			 
			<li class="list-group-item" style="border-radius: 0px;">
				作者:远方夕阳
			</li>
			<li class="list-group-item" style="border-radius: 0px;">
				Q Q:3979434
			</li>
			<li class="list-group-item" style="border-radius: 0px;">
				微信:farbluesky
			</li>

		</ul>
	</div>
</div>
<div class="panel panel-primary gdialog" id="LoginDialog"
	style="display: none; width: 300px; position: absolute; height: 395px;">
	<div class="panel-heading">
		系统管理员登录
		<a class="close" onclick="doHideDialog('LoginDialog')">&times;</a>
	</div>
	<div style="text-align: center; background: #5FA0D3; height: 100px;">
	</div>
	<div class="panel-body">


		<div class="form-group" style="margin-top: 20px;">
			<label style="width: 50px;">
				<font color="red">*</font>账号:
			</label>
			<input type="text" class="form-control" id="account" maxlength="15"
				style="display: inline; width: 200px; height: 50px;" />
		</div>
		<div class="form-group" style="margin-top: 20px;">
			<label style="width: 50px;">
				<font color="red">*</font>密码:
			</label>
			<input type="password" class="form-control" id="password"
				maxlength="32" style="display: inline; width: 200px; height: 50px;" />
		</div>
	</div>
	<div class="panel-footer" style="text-align: center;">
		<a type="button" class="btn btn-success btn-lg" onclick="doLogin()"
			style="width: 200px;"> 登录</a>
	</div>
</div>

<div id="global_mask"
	style="display: none; position: absolute; top: 0px; left: 0px; z-index: 998; background-color: rgb(190, 209, 216); opacity: 0.5; width: 100%; height: 100%; overflow: hidden; background-position: initial initial; background-repeat: initial initial;"></div>
