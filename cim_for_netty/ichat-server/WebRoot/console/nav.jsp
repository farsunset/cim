
<%
	String navBasePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath();
%>
<%@ page language="java" pageEncoding="UTF-8"%>
<div id="_main_nav" class="ui-vnav">
	<ul class="ui-nav-inner">


		<li style="height: 50px; text-align: center; margin-top: 10px;">
			<div class="btn-group" style="margin-top: 5px;">
				<a type="button" class="btn btn-danger" target="_blank"
					href="javascript:openWebclient();">CIM for
					Web(测试版)</a>
			</div>

		</li>
		<li style="border-bottom: 1px solid #D1D6DA;"></li>
		<li class="ui-item" id="sessionMenu">
			<a href="<%=navBasePath%>/admin/session_list.action"> <span
				class="ui-text">在线用户</span> <i class="ui-bg nav-recycle"></i> </a>
		</li>

	</ul>
</div>