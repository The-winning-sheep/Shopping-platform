<template>
  <div class="front-container">
    <div style="font-size: 20px; font-weight: bold; margin-bottom: 20px">我的充值记录（{{ data.total }}）</div>
    <div class="card" style="padding: 20px">
      <div style="margin-bottom: 20px; display: flex; align-items: center">
        <div style="flex: 1">
          <el-date-picker style="width: 300px; margin-right: 10px" v-model="data.time" type="date" placeholder="请输入日期查询" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
          <el-button type="primary" @click="load">查询</el-button>
          <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
        </div>

        <b style="margin-left: 20px; color: red">当前账户余额：{{ data.user.account }}元</b>
        <el-button @click="handleAdd" type="primary" style="margin-left: 20px">发起充值</el-button>
      </div>
      <div>
        <el-table :data="data.tableData" stripe>
          <el-table-column prop="money" label="充值金额">
            <template #default="scope">
              <b style="color: red">{{ scope.row.money }}元</b>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="支付方式"></el-table-column>
          <el-table-column prop="time" label="充值时间"></el-table-column>
        </el-table>
      </div>

      <div style="margin-top: 20px">
        <el-pagination @current-change="load" layout="total, prev, pager, next" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
      </div>
    </div>

    <!-- 充值弹窗 -->
    <el-dialog title="用户充值" width="30%" v-model="data.formVisible" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding-right: 30px; padding-top: 20px">
        <el-form-item label="充值金额" prop="money">
          <el-input-number style="width: 200px" :min="1" v-model="data.form.money" autocomplete="off" />
        </el-form-item>
        <el-form-item label="支付方式" prop="type" style="margin-top: 30px">
          <el-radio-group v-model="data.form.type">
            <el-radio value="微信支付">
              <img style="width: 100px; height: 40px" src="@/assets/imgs/wx.png" alt="">
            </el-radio>
            <el-radio value="支付宝">
              <img style="width: 100px; height: 40px" src="@/assets/imgs/zfb.png" alt="">
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="showPayCode">立即支付</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 支付二维码弹窗 + 倒计时 + 状态 + 取消支付 -->
    <el-dialog title="请完成支付" width="500" v-model="showPayDialog" :show-close="false" :close-on-click-modal="false">
      <div style="text-align:center; padding: 20px 0">
        <img :src="payImg" style="width:300px; height:300px" alt="支付码">
        <div style="margin-top:10px; font-size:16px">
          支付金额：<b style="color:red">{{ data.form.money }} 元</b>
        </div>
        <div style="color:#666; margin-top:5px">请扫码完成支付</div>

        <!-- 倒计时 + 状态 -->
        <div style="margin-top:15px; font-size:14px">
          支付剩余时间：
          <b :style="{color: timeLeft <= 10 ? 'red' : '#666'}">{{ timeLeft }}</b> 秒
        </div>
        <div style="margin-top:5px; font-size:13px; color: #999">
          {{ payStatus }}
        </div>
      </div>

      <template #footer>
        <el-button @click="cancelPay">取消支付</el-button>
        <el-button
            type="primary"
            @click="paySuccess"
            :disabled="timeLeft <= 0"
        >
          {{ timeLeft > 0 ? '已完成支付' : '支付已超时' }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive, ref, onUnmounted} from "vue";
import {ElMessageBox, ElMessage} from "element-plus";

// ✅ 顶部先导入图片（唯一能100%成功的方法）
import wxCode from '@/assets/imgs/wx_zf.jpg'
import zfbCode from '@/assets/imgs/wx_zf.jpg'

const formRef = ref()
const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  time: null,
  rules: {
    money: [{ required: true, message: '请输入金额', trigger: 'blur' }],
    type: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
  }
})

// ==================== 支付增强功能 ====================
const showPayDialog = ref(false)
const payImg = ref('')
const timeLeft = ref(30)
const payStatus = ref('请在倒计时结束前完成支付')
let timer = null

// 开始倒计时
const startTimer = () => {
  clearInterval(timer)
  timeLeft.value = 30
  payStatus.value = '请在倒计时结束前完成支付'

  timer = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 10) {
      payStatus.value = '支付即将超时，请尽快完成支付！'
    }
    if (timeLeft.value <= 0) {
      payStatus.value = '支付已超时，请重新发起充值'
      clearInterval(timer)
    }
  }, 1000)
}

// ✅ 显示支付码（直接用导入的变量，永远不会错）
const showPayCode = () => {
  formRef.value.validate(valid => {
    if (!valid) return

    // 根据选择自动切换
    if (data.form.type === '微信支付') {
      payImg.value = wxCode
    } else {
      payImg.value = zfbCode
    }

    showPayDialog.value = true
    startTimer()
  })
}

// 支付成功
const paySuccess = () => {
  if (timeLeft.value <= 0) {
    ElMessage.error('支付已超时，请重新充值')
    return
  }
  clearInterval(timer)
  showPayDialog.value = false
  data.formVisible = false
  add()
}

// 取消支付
const cancelPay = () => {
  ElMessageBox.confirm('确定要取消本次支付吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '再等等',
    type: 'warning'
  }).then(() => {
    clearInterval(timer)
    showPayDialog.value = false
    ElMessage.info('已取消支付')
  }).catch(() => {})
}

onUnmounted(() => clearInterval(timer))
// ======================================================

const loadAccount = () => {
  request.get('/user/selectById/' + data.user.id).then(res => {
    data.user.account = res.data.account
  })
}
loadAccount()

const load = () => {
  request.get('/recharge/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      userId: data.user.id,
      time: data.time
    }
  }).then(res => {
    data.tableData = res.data?.list
    data.total = res.data?.total
  })
}
load()

const handleAdd = () => {
  data.form = { money: 1, type: '微信支付' }
  data.formVisible = true
}

const add = () => {
  data.form.userId = data.user.id
  request.post('/recharge/add', data.form).then(res => {
    if (res.code === '200') {
      load()
      loadAccount()
      ElMessage.success('充值成功！')
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const reset = () => {
  data.time = null
  load()
}
</script>