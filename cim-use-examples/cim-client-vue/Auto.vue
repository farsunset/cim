<template>
  <div>
  <!-- start -->
  新页面创建成功
  <!-- end -->
 </div>
</template>

<script>
import { onConnect, onbindAccount } from './../../assets/websocket/cim.web.sdk.js'
export default {
  data () { // 数据
    return {

    }
  },
  created () { // 挂载
    this.getlist()
    window.onMessageReceived = this.onMessageReceived
    window.onReplyReceived = this.onReplyReceived
    window.onConnectFinished = this.onConnectFinished
  },
  methods: { // 方法
    getlist () {
      console.log('Console')
      onConnect() // 初始化
    },
    onConnectFinished () { // 登陆
      onbindAccount('开香槟')
    },
    onReplyReceived (reply) { // 上线响应
      console.log(reply)
      if (reply.key === 'client_bind' && reply.code === 200) {
        console.log('上线响应')
      }
    },
    onMessageReceived (message) { // 收消息
      console.log(message)
      console.log(message.sender + ': ' + message.content)
      if (message.action === 'ACTION_999') { // 账号在其他设备登陆
        return false
      }
    }
  },
  computed: { // 计算机属性

  }
}
</script>

<style lang="less" scoped>

</style>
