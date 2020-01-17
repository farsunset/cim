/*CIM服务器IP*/
const CIM_HOST = "127.0.0.1";
/*
 *服务端 websocket端口
 */
const CIM_PORT = 34567;
const CIM_URI = "ws://" + CIM_HOST + ":" + CIM_PORT;

const APP_VERSION = "1.0.0";
const APP_CHANNEL = "browser";
const APP_PACKAGE = "com.farsunset.cim";

/*
 *特殊的消息类型，代表被服务端强制下线
 */
const ACTION_999 = "999";
const DATA_HEADER_LENGTH = 1;

const MESSAGE = 2;
const REPLY_BODY = 4;

let socket;
let manualStop = false;
const CIMPushManager = {};
CIMPushManager.connect = function () {
    manualStop = false;
    window.localStorage.account = '';
    socket = new WebSocket(CIM_URI);
    socket.cookieEnabled = false;
    socket.binaryType = 'arraybuffer';
    socket.onopen = CIMPushManager.innerOnConnectFinished;
    socket.onmessage = CIMPushManager.innerOnMessageReceived;
    socket.onclose = CIMPushManager.innerOnConnectionClosed;
};

CIMPushManager.bindAccount = function (account) {

    window.localStorage.account = account;

    let deviceId = window.localStorage.deviceIddeviceId;
    if (deviceId == '' || deviceId == undefined) {
        deviceId = generateUUID();
        window.localStorage.deviceId = deviceId;
    }

    let browser = getBrowser();
    let body = new proto.com.farsunset.cim.sdk.web.model.SentBody();
    body.setKey("client_bind");
    body.setTimestamp(new Date().getTime());
    body.getDataMap().set("account", account);
    body.getDataMap().set("channel", APP_CHANNEL);
    body.getDataMap().set("appVersion", APP_VERSION);
    body.getDataMap().set("osVersion", browser.version);
    body.getDataMap().set("packageName", APP_PACKAGE);
    body.getDataMap().set("deviceId", deviceId);
    body.getDataMap().set("device", browser.name);
    CIMPushManager.sendRequest(body);
};

CIMPushManager.stop = function () {
    manualStop = true;
    socket.close();
};

CIMPushManager.resume = function () {
    manualStop = false;
    CIMPushManager.connect();
};


CIMPushManager.innerOnConnectFinished = function () {
    let account = window.localStorage.account;
    if (account === '' || account === undefined) {
        onConnectFinished();
    } else {
        CIMPushManager.bindAccount(account);
    }
};


CIMPushManager.innerOnMessageReceived = function (e) {
    let data = new Uint8Array(e.data);
    let type = data[0];
    let body = data.subarray(DATA_HEADER_LENGTH, data.length);

    if (type == MESSAGE) {
        let message = proto.com.farsunset.cim.sdk.web.model.Message.deserializeBinary(body);
        onInterceptMessageReceived(message.toObject(false));
        return;
    }

    if (type == REPLY_BODY) {
        let message = proto.com.farsunset.cim.sdk.web.model.ReplyBody.deserializeBinary(body);
        /**
         * 将proto对象转换成json对象，去除无用信息
         */
        let reply = {};
        reply.code = message.getCode();
        reply.key = message.getKey();
        reply.message = message.getMessage();
        reply.timestamp = message.getTimestamp();
        reply.data = {};

        /**
         * 注意，遍历map这里的参数 value在前key在后
         */
        message.getDataMap().forEach(function (v, k) {
            reply.data[k] = v;
        });

        onReplyReceived(reply);
    }
};

CIMPushManager.innerOnConnectionClosed = function (e) {
    if (!manualStop) {
        let time = Math.floor(Math.random() * (30 - 15 + 1) + 15);
        setTimeout(function () {
            CIMPushManager.connect();
        }, time);
    }
};

CIMPushManager.sendRequest = function (body) {
    let data = body.serializeBinary();
    let protobuf = new Uint8Array(data.length);
    protobuf.set(data, 0);
    socket.send(protobuf);
};

function onInterceptMessageReceived(message) {
    /*
     *被强制下线之后，不再继续连接服务端
     */
    if (message.action == ACTION_999) {
        manualStop = true;
    }
    /*
     *收到消息后，将消息发送给页面
     */
    if (onMessageReceived instanceof Function) {
        onMessageReceived(message);
    }
}

function getBrowser() {
    let explorer = window.navigator.userAgent.toLowerCase();
    if (explorer.indexOf("msie") >= 0) {
        let ver = explorer.match(/msie ([\d.]+)/)[1];
        return {name: "IE", version: ver};
    }
    else if (explorer.indexOf("firefox") >= 0) {
        let ver = explorer.match(/firefox\/([\d.]+)/)[1];
        return {name: "Firefox", version: ver};
    }
    else if (explorer.indexOf("chrome") >= 0) {
        let ver = explorer.match(/chrome\/([\d.]+)/)[1];
        return {name: "Chrome", version: ver};
    }
    else if (explorer.indexOf("opera") >= 0) {
        let ver = explorer.match(/opera.([\d.]+)/)[1];
        return {name: "Opera", version: ver};
    }
    else if (explorer.indexOf("Safari") >= 0) {
        let ver = explorer.match(/version\/([\d.]+)/)[1];
        return {name: "Safari", version: ver};
    }

    return {name: "Other", version: "1.0.0"};
}

function generateUUID() {
    let d = new Date().getTime();
    let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid.replace(/-/g, '');
}
	 