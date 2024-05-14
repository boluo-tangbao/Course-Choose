<template>
  <div style="padding: 10px;">

    <div style="margin: 10px 0;">
      <el-input v-model="search" placeholder="请输入关键字" style="width: 30%;" clearable/>
      <el-button type="primary" style="margin-left: 5px;" @click="load">查询</el-button>
    </div>

    <el-table :data="tableData" border stripe style="width: 100%;">
      <el-table-column prop="jobNumber" label="教师号"/>
      <el-table-column prop="courseNumber" label="课程号"/>
      <el-table-column prop="term" label="学期" sortable/>
      <el-table-column prop="courseName" label="课名" sortable/>
      <el-table-column prop="credit" label="学分"/>
      <el-table-column prop="time" label="时间"/>
      <el-table-column prop="classroom" label="地点"/>
      <el-table-column prop="curCapacity" label=" 当前人数"/>
      <el-table-column prop="capacity" label="人数上限"/>

      <el-table-column fixed="right" label="操作" width="120">
        <template #default="scope">
          <el-button type="primary" plain size="small" @click="goToCheckGrade(scope.row)">成绩登入</el-button>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="120">
        <template #default="scope">
          <el-button type="primary" plain size="small" @click="goToTeacherChat(scope.row)">课题组群聊</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin: 10px 10px 10px; font-size: 10px; color: rgb(96, 98, 102);">共 {{ total }} 条</div>
  </div>
</template>

<script>
import request from "@/utils/request";
import WebSocketReconnect from "@/utils/WebSocketReconnect";

export default {
  name: "TeacherCurriculum",
  created() {
    this.getCredit();
    this.load();
  },
  data() {
    return {
      search: '',
      tableData: [],
      total: 0,
      credit: []
    }
  },
  mounted () {
    if ('WebSocket' in window) {
      // 连接WebSocket节点
      this.websocket = new WebSocketReconnect('ws://127.0.0.1:9090' + '/websocket/123')
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
      request.get("/classes/forTeacher", {
        params: {
          search: this.search,
          teacherId: sessionStorage.getItem("currentId")
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.list;
        this.total = res.data.total;
        for (let i = 0; i < this.tableData.length; i++) {
          for (let j = 0; j < this.credit.length; j++) {
            if (this.tableData[i].courseId == this.credit[j].id) {
              this.tableData[i].credit = this.credit[j].credit;
            }
          }
        }
      })
    },
    goToCheckGrade(row) {//成绩登陆操作
      // 缓存当前课程信息
      sessionStorage.setItem("currentCourse", row.courseNumber);
      sessionStorage.setItem("currentCourseName", row.courseName);
      sessionStorage.setItem("currentTerm", row.term);
      sessionStorage.setItem("currentTime", row.time);
      sessionStorage.setItem("currentCredit", row.credit);
      sessionStorage.setItem("currentLimitNum", row.capacity);
      sessionStorage.setItem("currentCurrentNum",row.curCapacity);
      // 跳转路由
      this.$router.push("/teacherGrade");
    },
    goToTeacherChat(row) {//成绩登陆操作
      // 缓存当前课程信息
      sessionStorage.setItem("currentCourse", row.courseNumber);
      sessionStorage.setItem("currentCourseName", row.courseName);
      sessionStorage.setItem("currentTerm", row.term);
      sessionStorage.setItem("currentCredit", row.credit);
      // 跳转路由
      this.$router.push("/teacherChat");
    },
    getCredit() {
      request.get("/course/getCredit").then(res => {
        console.log(res);
        this.credit = res.data;
      })
    }
  }
}
</script>

<style scoped>

</style>