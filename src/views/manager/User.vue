<template>
  <div>
      <div class="card" style="margin-bottom: 5px">
        <el-input style='width:300px' v-model="data.name" placeholder="请输入名称查询" :prefix-icon="Search" />
        <el-button @click="load" type="primary" style="margin-left: 8px">查询</el-button>
        <el-button @click="reset" type="info" >重置</el-button>
      </div>

      <div class="card" style="margin-bottom: 5px">
        <div>
          <el-button @click="handleAdd" type="info" >新增</el-button>
        </div>
        <div>
          <el-table :data="data.tableData" stripe style="width: 100%">
            <el-table-column prop="username" label="账号"  />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="avatar" label="头像">
              <template #default="scope">
                   <el-image v-if="scope.row.avatar" style="width:50px;height: 50px;display: block;
                   border-radius: 50%" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" preview-teleported>
                   </el-image>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="角色" />
            <el-table-column prop="account" label="账户余额" />
            <el-table-column  label="操作" width="180px" fixed="right">
              <template #default="scope">
                <el-button @click="handleEdit(scope.row)" type="primary">编辑</el-button>
                <el-button @click="deleteUser(scope.row.id)" type="danger">删除</el-button>
              </template>

            </el-table-column>
          </el-table>
        </div>

      </div>
    <div>
      <el-pagination v-model:current-page="data.pageNum"  v-model:page-size="data.pageSize"
                     @current-change="load" size="small" background layout="total, prev, pager, next" :total="data.total"/>
    </div>


    <el-dialog title="用户信息" v-model="data.FormVisible" width="35%">
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding-right: 40px;padding-top: 20px">
        <el-form-item  prop="username" label="账号">
          <el-input :disabled="data.form.id!==undefined" v-model="data.form.username" placeholder="请输入账号" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="name" label="姓名">
          <el-input v-model="data.form.name" placeholder="请输入姓名" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item prop="avatar" label="头像">
          <el-upload  :action="baseUrl+'/files/upload'"  list-type="picture" :on-success="handlerFileUpload" :file-list="data.form.fileList">
            <el-button type="primary">点击上传</el-button>

          </el-upload>
        </el-form-item>


      </el-form>
      <template #footer>
    <span class="dialog-footer">
      <el-button @click="data.FormVisible = false">取消</el-button>
      <el-button type="primary" @click="save">确定</el-button>
    </span>
      </template>
    </el-dialog>



  </div>
</template>


<script setup>
import {reactive,ref} from "vue"
import {Search} from "@element-plus/icons-vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";


const data=reactive({
  name:null,
  tableData:[],
  total:0,
  pageNum:1,
  pageSize:5,
  id:0,
  FormVisible:false,
  form:{},
  rules:{
    //name:[{required:true,message:'请输入名字',trigger:'blur'}],
    username:[{required:true,message:'请输入账号',trigger:'blur'}],
  },
  avatar:null,
})

const baseUrl=import.meta.env.VITE_BASE_URL

const formRef=ref()

const load=()=>{
  request.get('/user/selectPage',{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      name:data.name
    }
  }).then(res=>{
    if(res.code=='200'){
      data.tableData=res.data.list
      data.total=res.data.total
    }else{
      ElMessage.error(res.msg)
    }
  })
}
load()
const reset=()=>{
  data.name=null
  load()
}

const handleAdd=()=>{
  data.form={
    fileList: []  // 清空
  }
  data.FormVisible=true
}

const update=()=>{
  request.put('/user/update',data.form).then(res=>{
    if(res.code==='200'){
      ElMessage.success("操作成功")
      data.FormVisible=false
      load()
    }else{
      ElMessage.error(res.msg)
    }
  })
}

const add=()=>{
  request.post('/user/add',data.form).then(res=>{
    if(res.code==='200'){
      ElMessage.success("操作成功")
      data.FormVisible=false
      load()
    }else{
      ElMessage.error(res.msg)
    }
  })
}

const save=()=>{
  formRef.value.validate((valid)=>{
    if(valid){
      if(data.form.id){ //有id就是更新
          update()
      }else{ //没有ID就是新增
          add()
      }
    }
  })
}

const deleteUser=(id)=>{
  ElMessageBox.confirm('确认删除吗？','删除确认',{type:'warning'}).then(res=>{
    request.delete('/user/delete',{params:{id:id}}).then(res=>{
      if(res.code==='200'){
        ElMessage.success('删除成功')
        load()
      }else{
        ElMessage.error(res.msg)
      }
    })
  }).catch(err=>{})
}

const handleEdit=(row)=>{
    data.form=JSON.parse(JSON.stringify(row))
    data.FormVisible=true
     data.form.fileList = row.avatar ? [{ url: row.avatar }] : []
    request.post('/user/update')
}

const handlerFileUpload=(response)=>{
    data.form.avatar=response.data
  data.form.fileList = [{ url: response.data }] // 加这行
}


</script>