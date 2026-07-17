<template>
  <div class="home-container">
    <!-- 顶部 时间+天气+欢迎 -->
    <div class="header-card">
      <div class="welcome">系统首页</div>
      <div class="info-group">
        <div class="time">{{ nowTime }}</div>
        <div class="date">{{ nowDate }}</div>
        <div class="weather">☀️ 晴  25℃</div>
      </div>
    </div>

    <!-- 主体空白卡片 -->
    <div class="content-card">
      <p class="desc">祝您使用愉快</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, onUnmounted } from "vue";

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  nowTime: '',
  nowDate: ''
})

// 更新时间
const updateTime = () => {
  const date = new Date()
  data.nowDate = `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
  const h = String(date.getHours()).padStart(2, '0')
  const m = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  data.nowTime = `${h}:${m}:${s}`
}

let timer = null
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}
.header-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #4e73f5, #6c63ff);
  color: #fff;
  padding: 24px 30px;
  border-radius: 16px;
  box-shadow: 0 6px 18px rgba(78, 115, 245, 0.2);
  margin-bottom: 20px;
}
.welcome {
  font-size: 26px;
  font-weight: bold;
}
.info-group {
  text-align: right;
}
.time {
  font-size: 32px;
  font-weight: bold;
  line-height: 1.2;
}
.date, .weather {
  font-size: 16px;
  margin-top: 4px;
  opacity: 0.9;
}
.content-card {
  background: #fff;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
}
.desc {
  font-size: 18px;
  color: #666;
}
</style>