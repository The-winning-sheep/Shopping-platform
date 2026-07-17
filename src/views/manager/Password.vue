<template>
    <div class="card" style="width: 40%">
      <el-form ref="formRef" :model="data.user" :rules="data.rules" label-width="100px" style="padding-right: 40px;padding-top: 20px">
        <el-form-item  prop="password" label="密码">
          <el-input  v-model="data.user.password" show-password  autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="newPassword" label="新密码">
          <el-input v-model="data.user.newPassword" placeholder="请输入" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword" label="确认新密码">
          <el-input v-model="data.user.confirmPassword" placeholder="请输入" autocomplete="off"></el-input>
        </el-form-item>
        <div style="text-align: center">
          <el-button type="primary" size="large" @click="updatePassword">保 存</el-button>
        </div>
      </el-form>
    </div>
</template>


<script setup>
import {reactive,ref} from "vue"
import {Search} from "@element-plus/icons-vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
import router from "@/router";

const baseUrl=import.meta.env.VITE_BASE_URL
const formRef=ref()
const data=reactive({
  user:JSON.parse(localStorage.getItem('system-user')||'{}'),
  rules:{
    //name:[{required:true,message:'请输入名字',trigger:'blur'}],
    password:[{required:true,message:'请输入原密码',trigger:'blur'}],
    newPassword:[{required:true,message:'请输入新密码',trigger:'blur'}],
    confirmPassword:[{required:true,message:'请再次输入新密码',trigger:'blur'}],
  },
})

const updatePassword=()=>{
  if(data.user.newPassword!==data.user.confirmPassword){
      ElMessage.warning("两次输入的密码不同")
      return
  }
  request.put('/updatePassword',data.user).then(res=>{
    if(res.code==='200'){
      ElMessage.success("更新成功")
      logout()
    }else{
      ElMessage.error(res.msg)
    }
  })
}

const logout = () => {
  router.push('/login')
  localStorage.removeItem('system-user')
}

</script>