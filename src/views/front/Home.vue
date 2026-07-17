<template>
  <div class="front-container">
    <!--第一行-->
    <div style="display: flex;grid-gap: 20px;margin-bottom: 10px" >
      <div style="flex:1">
      <el-carousel height="450px">
        <el-carousel-item v-for="item in data.carouselList" :key="item.id">
            <img @click="router.push('/front/GoodsDetail?id='+item.goodsId)" :src="item.img" alt="" style="width: 100%;height: 450px;cursor: pointer;">
        </el-carousel-item>
      </el-carousel>
      </div>
      <div class='card' style="width: 250px; background: #fff; padding: 15px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.08);">
        <div style="display: flex; align-items: center; padding-bottom: 10px; border-bottom: 2px solid red; margin-bottom: 15px;">
          <img src="@/assets/imgs/love.png" alt="" style="width:24px; margin-right: 6px;">
          <div style="color:red; font-weight: bold; font-size: 18px;">为您推荐</div>
        </div>
        <div>
          <div class="top-item" @click="router.push('/front/GoodsDetail?id='+item.id)" style="cursor: pointer;display: flex; gap: 12px; align-items: center; margin-bottom: 20px;" v-for="item in data.recommendGoods" :key="item.id">
            <img :src="item.img" style="width:70px; height: 70px; object-fit: cover; border-radius: 4px;" alt="">
            <div style="flex: 1; display: flex; flex-direction: column; justify-content: center;">
              <div class="line2" style="margin-bottom: 6px; line-height: 1.4; font-size: 14px;">
                {{item.name}}
              </div>
              <div style="color: #e64340; font-size: 16px; font-weight: 600;">
                ¥{{item.price}}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!--第一行结束-->

    <div class="card" style="padding: 20px">
          <div style="align-item:center;display: flex;padding-bottom: 5px;border-bottom: 2px solid red;margin-bottom: 10px">
           <div style="flex:1;display: flex;align-items: center">
             <img src="@/assets/imgs/hot.png" alt="" style="width:30px">
             <div style="color:red;font-weight: bold;font-size: 20px">热销商品</div>
           </div>
          <div @click="router.push('/front/goods')" style="cursor: pointer;font-size: 14px;padding-top: 7px;">查看更多</div>
        </div>

      <div style="margin-bottom: 10px">
        <el-row :gutter="20">
          <el-col :span="6" v-for="item in data.hotGoods" :key="item.id">
            <div @click="router.push('/front/GoodsDetail?id='+item.id)"  class="item" style="width:100%;border-radius: 5px;height: 280px">
              <img :src="item.img" alt="" style="width:100%;height: 200px;border-radius: 5px 5px 0 0">
              <div style="padding:10px">
                <div class="line1" style="font-size: 16px">{{item.name}}</div>
                <div>
                  <b style="color: red;font-size: 20px"><span>¥</span>{{item.price}}</b>
                  <span style="margin-left: 20px;color: #666;font-size: 12px">销量，{{item.saleCount}}</span>
                </div>
              </div>

            </div>
          </el-col>
        </el-row>
      </div>


    <!--第三行-->

      <div style="align-item:center;display: flex;padding-bottom: 5px;border-bottom: 2px solid red;margin-bottom: 10px">
        <div style="flex:1;display: flex;align-items: center">
          <img src="@/assets/imgs/new.png" alt="" style="width:30px">
          <div style="color:red;font-weight: bold;font-size: 20px">最新上架</div>
        </div>
        <div @click="router.push('/front/goods')" style="cursor: pointer;font-size: 14px;padding-top: 7px;">查看更多</div>
      </div>

      <div>
        <el-row :gutter="20">
          <el-col :span="6" v-for="item in data.newGoods" :key="item.id">
            <div @click="router.push('/front/GoodsDetail?id='+item.id)" class="item" style="width:100%;border-radius: 5px;height: 280px">
              <img :src="item.img" alt="" style="width:100%;height: 200px;border-radius: 5px 5px 0 0;">
              <div style="padding:10px">
                <div class="line1" style="font-size: 16px">{{item.name}}</div>
                <div>
                  <b style="color: red;font-size: 20px"><span>¥</span>{{item.price}}</b>
                  <span style="margin-left: 20px;color: #666;font-size: 12px">销量，{{item.saleCount}}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>





    </div>
  </div>
</template>

<script setup>
import {reactive} from "vue"
import request from "@/utils/request";
import router from "@/router";

const data=reactive({
  carouselList:[],
  hotGoods:[],
  newGoods:[],
  recommendGoods:[]
})

request.get('/carousel/selectAll').then(res=>{
  data.carouselList=res.data
})

request.get('/goods/selectAll',{
  params:{
    status:'上架'
  }
}).then(res=>{
  data.hotGoods=res.data.sort((v1,v2)=>v2.saleCount-v1.saleCount).splice(0,4)
})

request.get('/goods/selectAll',{
  params:{
    status:'上架'
  }
}).then(res=>{
  data.newGoods=res.data.splice(0,4)
})

request.get('/goods/selectAll',{
  parems:{
    status:'上架'
  }
}).then(res=>{
  data.recommendGoods=res.data.filter(v=>v.recommend==='是').splice(0,4)
})

</script>

<style>
.item{
  cursor: pointer;
}

.item:hover{
  border:1px solid red;
}

.top-item:hover{
  color: red;
}


</style>