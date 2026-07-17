<template>
  <div>
      <div>
        <el-form ref="formRef" :model="data.user" :rules="data.rules" label-width="80px" style="padding-right: 40px;padding-top: 20px">
          <el-form-item prop="avatar" label="头像">
            <el-upload  class="avatar-uploader" :action="baseUrl+'/files/upload'"  :show-file-list="false" :on-success="handlerFileUpload">
              <img v-if="data.user.avatar" :src="data.user.avatar " class="avatar"/>
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>

            <el-form-item  prop="username" label="账号">
              <el-input :disabled="data.user.id!==undefined" v-model="data.user.username" placeholder="请输入账号" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item prop="name" label="姓名">
              <el-input v-model="data.user.name" placeholder="请输入姓名" autocomplete="off"></el-input>
            </el-form-item>

            <div style="text-align: center">
              <el-button type="primary" size="large" @click="save">保 存</el-button>
            </div>

        </el-form>
      </div>
  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import request from "@/utils/request";
import {ElMessage} from "element-plus";



const baseUrl=import.meta.env.VITE_BASE_URL
const form=ref()
const data=reactive({
  user:JSON.parse(localStorage.getItem('system-user'||'{}')),
  rules:{
    //name:[{required:true,message:'请输入名字',trigger:'blur'}],
    username:[{required:true,message:'请输入账号',trigger:'blur'}],
  },
})

const emit=defineEmits(['updateUser'])
const loadUser=()=>{
  request.get('/user/selectById/'+data.user.id).then(res=>{
    data.user=res.data
    localStorage.setItem('system-user',JSON.stringify(res.data))
    //先存储在取值  这是接收子页面的发射请求，进行更新
    emit('updateUser')
  })
}
loadUser()

const handlerFileUpload=(response)=>{
  data.user.avatar=response.data
  data.user.fileList = [{ url: response.data }] // 加这行
}


const save=()=>{
  request.put('/user/update',data.user).then(res=>{
    if(res.code==='200'){
      ElMessage.success("保存成功")
      loadUser()
    }else{
      ElMessage.error("失败")
    }
  })
}




</script>

<style scoped>
.avatar-uploader .avatar {
  width: 130px;
  height: 130px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 130px;
  height: 130px;
  text-align: center;
}
</style>
