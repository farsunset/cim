var CIM_URI="ws://127.0.0.1:23456";// 修改为服务器的真实IP
var CMD_HEARTBEAT_REQUEST = "SR";
var CMD_HEARTBEAT_RESPONSE = "CR";
var SDK_VERSION = "1.0.0";
var SDK_CHANNEL = "browser";
var APP_PACKAGE = "com.farsunset.webcim";
var ACCOUNT;
var socket;
var manualStop = false;
var CIMWebBridge = new Object();
CIMWebBridge.connection = function(){
	manualStop = false;
	socket = new WebSocket(CIM_URI);
	socket.onopen = CIMWebBridge.innerOnConnectionSuccessed;
	socket.onmessage = CIMWebBridge.innerOnMessageReceived;
    socket.onclose = CIMWebBridge.innerOnConnectionClosed;
};
CIMWebBridge.bindAccount = function(id,deviceId){
	ACCOUNT=id;
	var browser = getBrowser();
	var body = {};
	body.key = "client_bind";
	body.data = {};
	body.data.account = id;
	body.data.channel = SDK_CHANNEL;
	body.data.version = SDK_VERSION;
	body.data.osVersion = browser.version;
	body.data.packageName = APP_PACKAGE;
	body.data.deviceId = deviceId;
	body.data.device = browser.name;
	CIMWebBridge.sendRequest(body);
};
CIMWebBridge.stop = function(){
	manualStop = true;
	socket.close();
};
CIMWebBridge.resume = function(){
	manualStop = false;
	CIMWebBridge.connection();
};
CIMWebBridge.innerOnConnectionSuccessed = function(){
	if(typeof onConnectionSuccessed != 'undefined' && onConnectionSuccessed instanceof Function){
		onConnectionSuccessed();
	}
};
CIMWebBridge.innerOnMessageReceived = function(e){
	var data = e.data;
	/**
	 * 收到服务端发来的心跳请求，立即回复响应，否则服务端会在10秒后断开连接
	 */
	if(data == CMD_HEARTBEAT_REQUEST){
		socket.send(CMD_HEARTBEAT_RESPONSE);
		return 
	}
	var json = JSON.parse(data);
	if(json.contentType == "Message" && typeof onMessageReceived != 'undefined' && onMessageReceived instanceof Function){
		onMessageReceived(json);
	}
	if(json.contentType == "ReplyBody" && typeof onReplyReceived != 'undefined' && onReplyReceived instanceof Function){
		onReplyReceived(json);
	}
};
CIMWebBridge.innerOnConnectionClosed = function(e){
	if(!manualStop){
		CIMWebBridge.connection();
	}
};
CIMWebBridge.sendRequest = function(body){
	var json = JSON.stringify(body);
	socket.send(json);
};

function getBrowser() {
	 var explorer = window.navigator.userAgent.toLowerCase() ;
	 if (explorer.indexOf("msie") >= 0) {
	    var ver=explorer.match(/msie ([\d.]+)/)[1];
	    return {name:"IE",version:ver};
	 }
	 else if (explorer.indexOf("firefox") >= 0) {
	    var ver=explorer.match(/firefox\/([\d.]+)/)[1];
	    return {name:"Firefox",version:ver};
	 }
	 else if(explorer.indexOf("chrome") >= 0){
	    var ver=explorer.match(/chrome\/([\d.]+)/)[1];
	    return {name:"Chrome",version:ver};
	 }
	 else if(explorer.indexOf("opera") >= 0){
	    var ver=explorer.match(/opera.([\d.]+)/)[1];
	    return {name:"Opera",version:ver};
	 }
	 else if(explorer.indexOf("Safari") >= 0){
	    var ver=explorer.match(/version\/([\d.]+)/)[1];
	    return {name:"Safari",version:ver};
	 }
	 return {name:"Other",version:"1.0.0"};
}
	 