<%
	String navBasePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ request.getContextPath();
%>
<%@ page language="java" pageEncoding="UTF-8"%>
<div id="_main_nav" class="ui-vnav">
	<ul class="ui-nav-inner">
	
 
		<li class="ui-bar">
			<b class="ui-bd"></b>
		</li>
			<li  class="ui-item" id="sessionMenu">
				<a href="<%=navBasePath %>/admin/session_list.action">
					<span class="ui-text">在线用户</span> <i class="ui-bg nav-recycle"></i> </a>
			</li>
		 
	</ul>
	<!-- 阴影 -->
	<b class="ui-shd"></b>
</div>