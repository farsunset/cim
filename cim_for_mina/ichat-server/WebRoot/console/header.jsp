<%@ page language="java" pageEncoding="UTF-8"%>
 
<%
	String headerBasePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ request.getContextPath();
 
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
			<div class="logo" style="left: -200px;"> </div>
			<!-- 头像 -->
			<div id="_main_face" data-no-selection="" class="header-right" style="cursor: pointer;" onclick="showUserMenu();">
				<span class="user-info"> <span
					class="user-avatar" style="width: 80px;"> <img
							src="<%=headerBasePath %>/resource/img/icon.png"><i
						class="ui-arr"></i> </span> </span>
			</div>
			<!-- 头像下方的菜单 -->
			<div id="_main_face_menu" style="display:none;"  class="ui-pop ui-pop-user" >
				<div class="ui-pop-head">
					<span id="_main_nick_name" class="user-nick" style="text-align: center;font-weight: bold;color: #555168;font-size: 18px;">管理员</span>
                   	<span class="user-nick" style="line-height: 10px;text-align: center;font-weight: bold;color: #3B20BB;font-size: 12px;">admin</span>
                   
				</div>

				<ul class="ui-menu">
					 
					 
					<li>
						<a id="_main_logout" href="javascript:doShowDialog('aboutDialog')"><i class="icon-fedbk"></i>关于</a>
					</li>
				</ul>
				<i class="ui-arr"></i>
				<i class="ui-arr ui-tarr"></i>
			</div>
		</div>
	</div>

	<!--web的导航在左侧-->

</div>
 

<div class="panel panel-primary gdialog" id="aboutDialog" style="display: none;width: 400px;position: absolute;">
		  <div class="panel-heading">关于
		  <a class="close"  onclick="doHideDialog('aboutDialog')">&times;</a>
		  </div>
		  <div class="panel-body">
		      <ul class="list-group">
				  <li class="list-group-item">CIM即时通讯后台管理系统</li>
				  <li class="list-group-item">Email:3979434@qq.com</li>
				  <li class="list-group-item">QQ:3979434</li>
				</ul>
		  </div>
</div>


<div id="global_mask" style="display: none; position: absolute; top: 0px; left: 0px; z-index: 998; background-color: rgb(190, 209, 216); opacity: 0.5; width: 100%; height: 100%; overflow: hidden; background-position: initial initial; background-repeat: initial initial;"></div>
 