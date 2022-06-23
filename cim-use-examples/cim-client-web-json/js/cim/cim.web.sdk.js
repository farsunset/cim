/*CIM服务器IP*/
const CIM_HOST = "127.0.0.1";
/*
 *服务端 websocket端口
 */
const CIM_PORT = 34567;
const CIM_URI = "ws://" + CIM_HOST + ":" + CIM_PORT;

const APP_VERSION = "1.0.0";
const APP_CHANNEL = "web";
const APP_PACKAGE = "com.farsunset.cim";

/*
 *特殊的消息类型，代表被服务端强制下线
 */
const ACTION_999 = "999";
const DATA_HEADER_LENGTH = 1;

const MESSAGE = 2;
const REPLY_BODY = 4;
const SENT_BODY = 3;
const PING = 1;
const PONG = 0;

/*
 * 握手鉴权常量
 */
const KEY_HANDSHAKE = "client_handshake";
const CODE_UNAUTHORIZED = "401";

const CODE_OK = "200";
const KEY_CLIENT_BIND = "client_bind";

/**
 * PONG字符串转换后
 * @type {Uint8Array}
 */
const PONG_BODY = "PONG";


let socket;
let manualStop = false;
const CIMPushManager = {};
CIMPushManager.connect = function () {
    manualStop = false;
    window.localStorage.account = '';
    socket = new WebSocket(CIM_URI);
    socket.cookieEnabled = false;
    socket.onopen = CIMPushManager.innerOnConnectFinished;
    socket.onmessage = CIMPushManager.innerOnMessageReceived;
    socket.onclose = CIMPushManager.innerOnConnectionClosed;
};

CIMPushManager.bind = function (account) {

    window.localStorage.account = account;

    let deviceId = window.localStorage.deviceId;
    if (deviceId === '' || deviceId === undefined) {
        deviceId = generateUUID();
        window.localStorage.deviceId = deviceId;
    }

    let browser = getBrowser().name;
    let body = {};
    body.key = KEY_CLIENT_BIND;
    body.timestamp=new Date().getTime();
    body.data = {};
    body.data.uid = account;
    body.data.channel = APP_CHANNEL;
    body.data.appVersion = APP_VERSION;
    body.data.packageName = APP_PACKAGE;
    body.data.deviceId = deviceId;
    body.data.deviceName = browser;

 
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

    let data = JSON.parse(e.data);
    let type = data.type;
    let body = data.content;

    if (type === PING) {
        CIMPushManager.pong();
        return;
    }

    if (type === MESSAGE) {
        let message = JSON.parse(body);
        onInterceptMessageReceived(message);
        return;
    }

    if (type === REPLY_BODY) {
        let reply = JSON.parse(body);
        /*
         * 判断是否是握手鉴权失败
         * 终止后续自动重连
         */
        if(reply.key === KEY_HANDSHAKE && reply.code === CODE_UNAUTHORIZED){
            manualStop = true;
        }
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
    let data = {};
    data.type = SENT_BODY;
    data.content = JSON.stringify(body);
    socket.send(JSON.stringify(data));
};

CIMPushManager.pong = function () {
    let data = {};
    data.type = PONG; 
    data.content = PONG_BODY;
    socket.send(JSON.stringify(data));
};

function onInterceptMessageReceived(message) {
    /*
     *被强制下线之后，不再继续连接服务端
     */
    if (message.action === ACTION_999) {
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
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid.replace(/-/g, '');
}
	 