
package  
{
	import com.adobe.serialization.json.JSON;

	public class ReplyBody
	{
		public  var key:String;
		public  var code:String;
		public  var message:String;
		public  var data:Object;
		public  var timestamp:Number;
		
	 

		public static function mappingToJSON(xml:XML):Object
		{
			var  body:ReplyBody = new ReplyBody();
			body.key = xml["key"];
			body.code = xml["code"];
			body.timestamp = xml["timestamp"];
			return com.adobe.serialization.json.JSON.encode(body);
		}
	}
	
	
	
}