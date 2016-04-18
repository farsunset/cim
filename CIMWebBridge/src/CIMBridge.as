package
{
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.events.SecurityErrorEvent;
	import flash.events.TimerEvent;
	import flash.external.ExternalInterface;
	import flash.net.Socket;
	import flash.system.Security;
	import flash.utils.Timer;
	import flash.media.Sound;
	import flash.net.URLRequest;
	
	public class CIMBridge extends Sprite
	{
		internal var CIM_HOST:String = "127.0.0.1";
		internal var CIM_PORT:String = "23456";
		internal var socket:Socket; 
		internal var froceOffline :Boolean = false;
		internal var _sound:Sound = new Sound(new URLRequest("dingdong.mp3"));
		
		/**
		 * 服务端心跳请求命令  cmd_server_hb_request
		 */
		internal  const CMD_HEARTBEAT_REQUEST:String="S_H_RQ";
		/**
		 * 客户端心跳响应命令  cmd_client_hb_response
		 */
		internal const  CMD_HEARTBEAT_RESPONSE:String ="C_H_RS"; 
		
		public function CIMBridge()
		{
			
			ExternalInterface.addCallback("connect",connect);
			ExternalInterface.addCallback("setAccount",setAccount);
			ExternalInterface.addCallback("getOfflineMessage",getOfflineMessage);
			ExternalInterface.addCallback("logout",logout);
			ExternalInterface.addCallback("playSound",playSound);
			
			ExternalInterface.call("bridgeCreationComplete");
			
		}
		public function connect(host:String):void
		{	
			 
			CIM_HOST = host;
			var policyfile:String="xmlsocket://"+CIM_HOST+":"+CIM_PORT;
			Security.loadPolicyFile(policyfile);
			socket=new Socket();
			socket.addEventListener(Event.CONNECT,sessionCreated);//监听是否连接上服务器
			socket.addEventListener(Event.CLOSE,sessionClosed);//监听套接字连接是否关闭
			socket.addEventListener(IOErrorEvent.IO_ERROR,exceptionCaught);
			socket.addEventListener(SecurityErrorEvent.SECURITY_ERROR,securityErrorFun);
			//监听服务器新信息
			socket.addEventListener(ProgressEvent.SOCKET_DATA,messageReceived); 
			
			socket.connect(CIM_HOST,parseInt(CIM_PORT));//连接服务器    
			
		}
		
		public function setAccount(account:String,deviceId:String):void
		{
			var xml:String="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			xml+="<sent>";
			xml+="<key>client_bind</key>";   
			xml+="<data>";
			xml+="<account>"+account+"</account>";
			xml+="<deviceId>"+deviceId+"</deviceId>";
			xml+="<channel>web</channel>";
			xml+="<device>flash</device>";
			xml+="<version>2.0.0</version>";
			xml+="<osVersion>"+flash.system.Capabilities.version+"</osVersion>";
			xml+="</data>";
			xml+="</sent>";
			
			send(xml);
			
		}
		
		public function getOfflineMessage(account:String):void
		{
			
			var xml:String="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			xml+="<sent>";
			xml+="<key>client_get_offline_message</key>";   
			xml+="<data>";
			xml+="<account>"+account+"</account>";
			xml+="</data>";
			xml+="</sent>";
			send(xml);
			
		}
		
		public function logout():void
		{
			
			socket.close();
			
		}
		
		private function sessionCreated(event:Event):void
		{
			
			ExternalInterface.call("sessionCreated");
			froceOffline = false;
		}
		
		 
		
		private function sessionClosed(event:Event):void
		{
			ExternalInterface.call("sessionClosed");
			
			if(!froceOffline)
			{
				connect(CIM_HOST);
			}
		}
		private function exceptionCaught(event:Event):void{
			
			//Alert.show("exceptionCaught","提示");
		}
		
		private function securityErrorFun(event:Event):void{
			
			//Alert.show("securityErrorFun"+event.toString(),"提示");
		}
		
		
		
		/**接受服务器信息*/
		internal function messageReceived(event:ProgressEvent):void
		{
			var msg:String="";
			//循环读取数据
			while(socket.bytesAvailable)
			{
				msg+=socket.readUTFBytes(socket.bytesAvailable);
			}
			
			var index:int = msg.indexOf("\b");
			if(msg.indexOf("\b")>=0)
			{
				msg = msg.substring(0,index);
			}
			
			
			if(msg.toUpperCase() == CMD_HEARTBEAT_REQUEST)
			{
				send(CMD_HEARTBEAT_RESPONSE);
				return;
			}
			
			
			var xml:XML=XML(msg);
			var data:Object = xml as Object;
			if(xml.name()=="reply"){
				ExternalInterface.call("onReplyReceived",ReplyBody.mappingToJSON(xml));
			}
			if(xml.name()=="message"){
				
				
				if(xml["type"]=="999")
				{
					froceOffline = true;
				}
				
				ExternalInterface.call("onMessageReceived",Message.mappingToJSON(xml),xml["content"].toString());
			}
			
		}
		
		
		/**发送数据到服务器*/
		internal function send(msg:String):void 
		{
			//新建一个ByteArray来存放数据
			socket.writeUTFBytes(msg+"\b");
			//调用flush方法发送信息
			socket.flush();
		} 
		
		public function playSound():void
		{
			_sound.play(1);
		}
		
		 
		
	}
}