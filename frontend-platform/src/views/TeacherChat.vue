<template>
  <div style="padding: 10px;">
    <el-button type="primary" plain :icon="ArrowLeft"
               @click="back">返回课程列表
    </el-button>

    <div style="margin: 20px 0;">

    </div>

    <el-table :data="tableData" border stripe style="width: 100%;">
      <el-table-column prop="courseNumber" label="课程号"/>
      <el-table-column prop="term" label="学期" sortable/>
      <el-table-column prop="courseName" label="课名" sortable/>
      <el-table-column prop="credit" label="学分"/>
      <el-table-column prop="teacherAmount" label="课题组人数"/>

      <el-table-column fixed="right" label="操作" width="120">
        <template #default="scope">
          <el-button type="primary" plain size="small" @click="getAllTeacherAmount(scope.row)">进入课题组</el-button>
          <!-- 教师信息弹窗组件 -->
          <teacher-info-popup :teacherInfo="teacherInfo" v-if="teacherInfo.length > 0" @close="closePopup"/>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin: 10px 10px 10px; font-size: 10px; color: rgb(96, 98, 102);">共 {{ total }} 条</div>
  </div>
</template>

<script>
import request from "@/utils/request";
import WebSocketReconnect from "@/utils/WebSocketReconnect";
import TeacherInfoPopup from './TeacherInfoPopup.vue';
import {ArrowLeft} from "@element-plus/icons-vue";

export default{
  name: "TeacherChat",
  computed: {
    ArrowLeft() {
      return ArrowLeft
    }
  },
  created() {
    //this.getAllTeacherAmount();
    this.load();
    this.currentTeacherJobNumber = sessionStorage.getItem("currentCourse");
    this.teacherId = sessionStorage.getItem("currentId");
    this.getOnlineUsers();
    setInterval(() => {
      this.getOnlineUsers();
    }, 10000);
  },
  components: {
    TeacherInfoPopup
  },
  data() {
    return {
      search: '',
      tableData: [],
      total: 0,
      credit: [],
      Job_number:[],
      teacherInfo: [],
      currentTeacherJobNumber: '',
      teacherId: ''
    }
  },
  mounted () {
    if ('WebSocket' in window) {
      // 连接WebSocket节点
      this.websocket = new WebSocketReconnect('ws://127.0.0.1:9090/websocket/'+this.teacherId)
      console.log(this.websocket);
    } else {
      alert('浏览器不支持webSocket')
    }

    // 接收到消息的回调方法
    this.websocket.socket.onmessage = function (event) {
      const data = event.data
      console.log('后端传递的数据:' + data)
      // 将后端传递的数据渲染至页面
      // textarea1.value = textarea1.value + data + '\n' + '【消息】---->'
    }
    // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
      this.websocket.close()
    }
    // 关闭连接
    function closeWebSocket () {
      this.websocket.close()
    }
    // 发送消息
    function send () {
      this.websocket.socket.send({ kk: 123 })
    }
  },
  methods: {
      load() {
        request.get("/course/forTeacherWithSameCourse", {
          params: {
            search: this.search,
            teacherId: sessionStorage.getItem("currentId")
          }
        }).then(res => {
          console.log(res)
          this.tableData = res.data.list;
          this.total = res.data.total;
        })
      },
/*      goToCheckGrade(row) { //成绩登陆操作
        // 缓存当前课程信息
        sessionStorage.setItem("currentCourse", row.courseNumber);
        sessionStorage.setItem("currentCourseName", row.courseName);
        sessionStorage.setItem("currentTerm", row.term);
        sessionStorage.setItem("currentTime", row.time);
        sessionStorage.setItem("currentCredit", row.credit);
        sessionStorage.setItem("currentLimitNum", row.capacity);
        sessionStorage.setItem("currentCurrentNum", row.curCapacity);
        // 跳转路由
        this.$router.push("/teacherGrade");
      },*/
    getAllTeacherAmount() {
      // 发起请求获取教师信息
      // 假设使用axios作为HTTP库
      request.get("/course/getAllTeacherAmount", {
        params: {
          courseNumber: sessionStorage.getItem("currentCourse"),
          term: sessionStorage.getItem("currentTerm")
        }
      }).then(res => {
        console.log(res);
        if (res.data && res.data.list && res.data.list.length > 0) {
          this.teacherInfo = res.data.list;
        } else {
          this.teacherInfo = [];
          alert("未找到教师信息");
        }
      }).catch(error => {
        console.error("Error fetching teacher information: ", error);
        alert("获取教师信息时出错");
      });
    },
    back() {
        this.$router.push('/teacherCurriculum');
    },
    closePopup() {
      // 关闭弹窗时清空教师信息
      this.teacherInfo = [];
    },
    getOnlineUsers(){
      request.get("/course/onlineUsers").then(res => {
        console.log(res)
        this.onlineUserList = res.data.list;
      })
      console.log("当前在线用户为："+this.onlineUserList);
    }
  }
}
</script>

<style scoped>

</style>