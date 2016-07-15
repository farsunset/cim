package
{
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.external.ExternalInterface;
	import flash.media.Sound;
	import flash.net.Socket;
	import flash.net.URLRequest;
	import flash.system.Security;
	
	public class CIMBridge extends Sprite
	{
		internal var CIM_HOST:String = "127.0.0.1";
		internal var CIM_PORT:String = "23456";
		internal var socket:Socket; 
		internal var froceOffline :Boolean = false;
		internal const MESSAGE_SEPARATE :String = '\b';
		/**
		 * 服务端心跳请求命令   
		 */
		internal  const CMD_HEARTBEAT_REQUEST:String="S_H_RQ";
		/**
		 * 客户端心跳响应命令   
		 */
		internal const  CMD_HEARTBEAT_RESPONSE:String ="C_H_RS"; 
		
		internal var mBuffer :String = '\b';
		
		public function CIMBridge()
		{
			
			ExternalInterface.addCallback("connect",connect);
			ExternalInterface.addCallback("bindAccount",bindAccount);
			ExternalInterface.addCallback("getOfflineMessage",getOfflineMessage);
			ExternalInterface.addCallback("logout",logout);
			ExternalInterface.addCallback("playSound",playSound);
			
			ExternalInterface.call("bridgeCreationComplete");
			
		}
		public function connect(host:String):void
		{	
			 
			CIM_HOST = host;
			var policyfile:String="xmlsocket://"+CIM_HOST+":"+CIM_PORT;
			Security.loadPolicyFile(policyfile);//加载安全策略文件，得到相应的返回才会创建连接
			socket=new Socket();
			socket.addEventListener(Event.CONNECT,sessionCreated);//监听是否连接上服务器
			socket.addEventListener(Event.CLOSE,sessionClosed);//监听套接字连接是否关闭
			socket.addEventListener(ProgressEvent.SOCKET_DATA,messageReceived); //监听服务器消息
			
			socket.connect(CIM_HOST,parseInt(CIM_PORT));//连接服务器    
			
		}
		
		public function bindAccount(account:String,deviceId:String):void
		{
			var xml:String="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			xml+="<sent>";
			xml+="<key>client_bind</key>";   
			xml+="<data>";
			xml+="<account>"+account+"</account>";
			xml+="<deviceId>"+deviceId+"</deviceId>";
			xml+="<channel>browse</channel>";
			xml+="<device>Flash</device>";
			xml+="<version>2.0.0</version>";
			xml+="<osVersion>"+flash.system.Capabilities.os+"</osVersion>";
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
		 
		
		internal function handleMessage(message:String):void{
		
			if(message.toUpperCase() == CMD_HEARTBEAT_REQUEST)
			{
				send(CMD_HEARTBEAT_RESPONSE);
				return;
			}
			
			
			var xml:XML=XML(message);
			if(xml.name()=="reply"){
				ExternalInterface.call("onReplyReceived",ReplyBody.mappingToJSON(xml));
			}
			if(xml.name()=="message"){
				
				
				if(xml["type"]=="999")
				{
					froceOffline = true;
				}
				
				ExternalInterface.call("onMessageReceived",Message.mappingToJSON(xml));
			}
		}
		
		/**接受服务器信息*/
		internal function messageReceived(event:ProgressEvent):void
		{
			
			mBuffer+=socket.readMultiByte(socket.bytesAvailable,"UTF-8");;
			if(mBuffer.charAt(mBuffer.length-1)!=MESSAGE_SEPARATE){
				
			   return ;
			   
			}else
			{
				var array:Array = mBuffer.split(MESSAGE_SEPARATE);
				for each(var message:String in array) {
					handleMessage(message);
				}
				
				mBuffer = "";
				
			}
			
		}
		
		
		/**发送数据到服务器*/
		internal function send(msg:String):void 
		{
			//新建一个ByteArray来存放数据
			socket.writeUTFBytes(msg+MESSAGE_SEPARATE);
			//调用flush方法发送信息
			socket.flush();
		} 
		
		public function playSound(name:String):void
		{
			internal var _sound:Sound = new Sound(new URLRequest(name));
			_sound.play(1);
		}
		
		 
		
	}
}