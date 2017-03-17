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

			<div class="btn-group" style="float: right; margin-top: 50px;margin-right:20px;">
			 
				<a type="button" class="btn btn-primary"
					onclick="doShowDialog('aboutDialog')"><span class="glyphicon glyphicon-info-sign"  style="top:2px;"></span> 关于</a>
			</div>
		</div>

	</div>

	<!--web的导航在左侧-->

</div>

 
<div class="modal fade" id="aboutDialog" tabindex="-1" role="dialog">
		<div class="modal-dialog" style="width: 420px;">
			<div class="modal-content">
				<div class="modal-header" >
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	                <h4 class="modal-title">侣信管理系统</h4>
				</div>
				<div class="modal-body">
 
	    <div style="text-align: center;border:none;height: 150px;">
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
</div>
</div>
 