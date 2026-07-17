<template>
  <div class="front-container">
    <div style="display: flex;grid-gap: 20px;align-items: center;margin-bottom: 10px">
      <div style="flex:1;display:flex;grid-gap: 20px;">
        <div :class="{'active': null===data.categoryId}"
             @click="loadByCategory(null)"
             style="font-size: 14px;cursor: pointer;padding-bottom: 5px;">全部</div>
        <div :class="{'active':item.id===data.categoryId}" @click="loadByCategory(item.id)"
             style="font-size: 14px;cursor: pointer;padding-bottom: 5px;"
             v-for="item in data.categoryList" :key="item.id">{{item.name}}</div>
      </div>
      <div>
        <el-input clearable @clear="load" style="height: 40px;width:300px" v-model="data.name" placeholder="请输入商品名称搜索"></el-input>
        <el-button @click="load" type="primary" style="height: 40px;margin-left: 5px">搜 索</el-button>
      </div>
    </div>

    <div v-if="data.total>0">
      <el-row :gutter="20">
        <el-col :span="6" v-for="item in data.tableData" :key="item.id">
          <div @click="router.push('/front/goodsDetail?id=' + item.id)" class="card goods-item"
               style="width: 100%; padding: 0; border-radius: 5px; margin-bottom: 20px;overflow: hidden">
            <img :src="item.img" alt="" style="width:100%;height: 200px;border-radius: 5px 5px 0 0">
            <div style="padding:10px">
              <div class="line1" style="font-size: 18px;">{{item.name}}</div>
              <div>
                <b style="color: red;font-size: 20px"><span>¥</span>{{item.price}}</b>
                <span style="margin-left: 20px;color: #666;font-size: 12px">销量，{{item.saleCount}}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <div style="margin-top:20px" >
        <el-pagination @current-change="load"  style="background-color: white;width:fit-content;padding: 5px 10px" layout="total,prev, pager, next" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
      </div>
    </div>
    <div v-else style="padding: 50px;text-align: center;color: #666">暂无数据...</div>
  </div>
</template>

<script setup>


import {reactive} from "vue";
import router from "@/router";
import request from "@/utils/request";

const data=reactive({
  name:router.currentRoute.value.query.name,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  categoryId:null,
  categoryList:[]
})

const loadByCategory=(categoryId)=>{
  data.categoryId=categoryId
  load()
}
const load=()=>{
  request.get('/goods/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
      categoryId:data.categoryId
    }
  }).then(res => {
    data.tableData = res.data?.list
    data.total = res.data?.total
  })
}
load()

const loadCategory=()=>{
  request.get('/category/selectAll').then(res => {
    data.categoryList = res.data
  })
}
loadCategory()

const clearPathParam=()=>{
  let url=location.href
  url=url.replace(/(\?|#)[^'"]*/,'');
  window.history.pushState({},0,url)
}
clearPathParam()

</script>

<style scoped>
.active{
  font-weight: bold;
  color: #74c2d8;
  border-bottom: 2px solid #74c2d8;
}
.goods-item{
  cursor: pointer;
  transition: all 0.5s;
}
.goods-item :hover{
  transform: scale(1.05);
}
</style>