<template>
  <div v-if="isLoading" class="popup">
    <div class="popup-content">
      <h2 class="popup-title">教师信息</h2>
      <table class="teacher-table" border="1">
        <thead>
        <tr>
          <th>序号</th>
          <th>Job Number</th>
          <th>操作</th>
          <th>在线情况</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(teacher, index) in teacherInfo" :key="index">
          <td>{{ index + 1 }}</td>
          <td>{{ teacher.jobNumber }}</td>
          <td>
            <button v-if="currentTeacherJobNumber !== teacher.jobNumber" @click="handleButtonClick(index)">聊天</button>
          </td>
          <td>
            <span v-if="isTeacherOnline(teacher.jobNumber)">在线</span>
            <span v-else>离线</span>
          </td>
        </tr>
        </tbody>
      </table>
      <button class="close-btn" @click="close">关闭</button>
    </div>

    <!-- 聊天界面部分 -->
    <div v-if="showChatRoom" class="chat-room">
      <div class="popup-content">
        <h2 class="popup-title">聊天</h2>
        <div class="message-list">
          <div v-for="(message, index) in chatMessages" :key="index" class="message">{{ message }}</div>
        </div>
        <div class="input-box">
          <input type="text" v-model="newMessage" placeholder="输入消息...">
          <button @click="sendMessage">发送</button>
        </div>
        <button class="close-btn" @click="closeChatRoomPopup">关闭</button>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  created() {
    this.getOnlineUsers();
    setTimeout(() => {
      this.isLoading = true; // 在2秒后将isLoading设置为false，表示加载完成
    }, 2000); // 设置延迟时间为2秒，即2000毫秒
  },
  mounted() {
    // 创建 WebSocket 连接
    this.websocket = new WebSocket('ws://localhost:9090/websocket/'+this.currentTeacherJobNumber);

    // 监听 WebSocket 连接打开事件
    this.websocket.onopen = () => {
      console.log('WebSocket 连接已打开');
    };

    // 监听 WebSocket 收到消息事件
    this.websocket.onmessage = (event) => {
      this.chatMessages.push(event.data);
    };

    // 监听 WebSocket 连接关闭事件
    this.websocket.onclose = () => {
      console.log('WebSocket 连接已关闭');
    };

    // 监听 WebSocket 连接发生错误事件
    this.websocket.onerror = (error) => {
      console.error('WebSocket 连接发生错误:', error);
    };
  },
  data() {
    return {
      showChatRoom: false, // 控制聊天界面显示
      chatMessages: [], // 存储聊天消息
      newMessage: '', // 输入的新消息
      websocket: null, // WebSocket 对象
      chatRommId:'',
      onlineUserList:[],
      isLoading:false
    };
  },
  props: {
    teacherInfo: {
      type: Array,
      default: () => []
    },
    onlineUserList: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    currentTeacherJobNumber() {
      return sessionStorage.getItem("currentId");
    }
  },
  methods: {
    async getOnlineUsers() {
      try {
        const res = await request.get("/course/onlineUsers");
        console.log(res);
        this.onlineUserList = res.data.list;
        // 强制组件重新渲染，确保渲染时在线用户列表已经更新
        this.$forceUpdate();
      } catch (error) {
        console.error("获取在线用户列表失败：", error);
      }
    },
    async close() {
      console.log("关闭聊天室");
      console.log("当前聊天室名为："+this.chatRommId);
      if (this.chatRommId === '') {
        this.$emit('close');
        return;
      }//如果空，那么直接关闭
      try {
        // 发送请求给后端，请求删除对应的 roomId
        await request.delete(`/chatRoom/${this.chatRommId}`);
        // 触发 close 事件
        this.$emit('close');
      } catch (error) {
        console.error("删除聊天室失败：", error);
        this.$message.error("删除聊天室失败，请稍后重试");
      }
    },
    async handleButtonClick(index) {
      try {
        const selectedTeacher = this.teacherInfo[index];
        const selectedTeacherJobNumber = selectedTeacher.jobNumber;

        let chatRoomId;

        if (selectedTeacherJobNumber > this.currentTeacherJobNumber) {
          chatRoomId = `${this.currentTeacherJobNumber}-${selectedTeacherJobNumber}`;
        } else {
          chatRoomId = `${selectedTeacherJobNumber}-${this.currentTeacherJobNumber}`;
        }
        this.chatRommId = chatRoomId;

        const response = await request.post("/chatRoom/createChatRoom", { roomId: chatRoomId });
        console.log(response);

        if (response.code==='聊天室已存在')
        {
          console.log("已经存在聊天室，显示！");
          this.showChatRoom = true;
        }else if (response.data.message) {
          // || response.data.message !== "聊天室已存在"
          // 如果后端返回了消息，表示请求失败，显示错误消息
          console.log("请求失败：", response.data.message);
          this.$message.error(response.data.message || "创建聊天室失败，请稍后重试");
        } else {
          // 否则，请求成功，可以显示聊天界面
          console.log("聊天室创建成功");
          this.showChatRoom = true;
        }
      } catch (error) {
        // 请求失败，显示默认错误消息
        console.log("实际返回的状态码：", error.response.status);
        console.error("创建聊天室失败：", error);
        this.$message.error("创建聊天室失败，请稍后重试!!!");
      }
      console.log(this.showChatRoom);
    },
    sendMessage() {
      // 确保消息不为空
      if (!this.newMessage) {
        this.$message.warning("消息不能为空");
        return;
      }

      // 构造要发送的数据对象
      const data = {
        message: this.currentTeacherJobNumber+"发送："+this.newMessage+"\n发送时间: "+new Date().toLocaleString()+'\n'
      };

      // 发送 HTTP POST 请求到后端
      request.post(`/chatRoom/sendMessage/${this.chatRommId}`, data)
          .then(response => {
            // 请求成功，可以根据需要处理后端返回的响应
            console.log("消息发送成功", response);

            // 清空聊天消息列表
            this.chatMessages = [];

            // 将新消息添加到聊天消息列表中
            this.chatMessages = this.chatMessages.concat(response.data.messages);

            // 清空输入框
            this.newMessage = '';

            // 更新页面上的内容或者执行其他操作
          })
          .catch(error => {
            // 请求失败，显示错误消息
            console.error("消息发送失败", error);
            this.$message.error("消息发送失败，请稍后重试");
          });
    },
    isTeacherOnline() {

      return (jobNumber) => {
        console.log("10001"+this.onlineUserList.includes('10001'));
        return this.onlineUserList.includes(jobNumber);
      }
    },
    closeChatRoomPopup() {
      this.showChatRoom = false;
    }
  }
};
</script>

<style scoped>
.popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.popup-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
}

.popup-title {
  margin-top: 0;
  margin-bottom: 20px;
}

.teacher-table {
  width: 100%;
  border-collapse: collapse;
}

.teacher-table th,
.teacher-table td {
  padding: 10px;
  text-align: center;
}

.teacher-table th {
  background-color: #f2f2f2;
}

.close-btn {
  display: block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.close-btn:hover {
  background-color: #0056b3;
}
</style>
