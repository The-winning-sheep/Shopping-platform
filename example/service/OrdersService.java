package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 管理员业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private CartMapper cartMapper;

    /**
     * 单个购买和在购物车中的购买都利用这个函数
     */
    @Transactional
    public void add(Orders orders) {
        orders.setStatus("待接单");
        orders.setTime(DateUtil.now());
        // 随机订单编号
        String no = DateUtil.format(new Date(), "yyyyMMdd") + System.currentTimeMillis() + RandomUtil.randomNumbers(4);
        orders.setOrderNo(no);
        ordersMapper.insert(orders);
        Integer orderId = orders.getId();

        List<Cart> cartList = orders.getCartList();
        User user = userMapper.selectById(orders.getUserId());
        BigDecimal totalPrice = BigDecimal.ZERO;

        // ================== 第一步：先算订单总价 ==================
        for (Cart cart : cartList) {
            Integer goodsId = cart.getGoodsId();
            Goods goods = goodsMapper.selectById(goodsId);

            // 先检查库存够不够
            if (goods.getStore() < cart.getNum()) {
                throw new CustomException(goods.getName() + "库存不足");
            }

            // 计算总价
            totalPrice = totalPrice.add(goods.getPrice().multiply(BigDecimal.valueOf(cart.getNum())));
        }

        // ================== 第二步：先判断余额！！ ==================
        if (user.getAccount().compareTo(totalPrice) < 0) {
            throw new CustomException("您的账户余额不足，请充值");
        }

        // ================== 第三步：余额够了，再扣库存、生成订单 ==================
        for (Cart cart : cartList) {
            Integer goodsId = cart.getGoodsId();
            Goods goods = goodsMapper.selectById(goodsId);

            // 扣库存  加销量
            goods.setStore(goods.getStore() - cart.getNum());
            goods.setSaleCount(goods.getSaleCount()+cart.getNum());
            goodsMapper.updateById(goods);

            // 新增订单详情
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setGoodsId(goodsId);
            orderDetail.setNum(cart.getNum());
            orderDetail.setGoodsImg(goods.getImg());
            orderDetail.setGoodsName(goods.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setGoodsPrice(goods.getPrice());
            orderDetailMapper.insert(orderDetail);

            //删除购物车中的信息
            if(cart.getId()!=null){
                cartMapper.deleteById(cart.getId());
            }

        }

        // ================== 第四步：扣用户余额 ==================
        user.setAccount(user.getAccount().subtract(totalPrice));
        userMapper.updateById(user);

        // ================== 第五步：更新订单总价 ==================
        orders.setTotal(totalPrice);
        ordersMapper.updateById(orders);

    }

    /**
     * 删除
     */
    @Transactional
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
        orderDetailMapper.deleteByOrderId(id);
    }

    /**
     * 修改
     */
    @Transactional
    public void updateById(Orders orders) {
        if("已取消".equals(orders.getStatus())){
            //用户
            Integer userId = orders.getUserId();
            User user = userMapper.selectById(userId);
            user.setAccount(user.getAccount().add(orders.getTotal())); //归还金额
            userMapper.updateById(user);

            //商品
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            List<OrderDetail> o = orderDetailMapper.selectAll(orderDetail);
            for(OrderDetail detail : o){
                Integer goodsId = detail.getGoodsId();
                Goods goods = goodsMapper.selectById(goodsId);
                if(goods!=null){
                    goods.setStore(goods.getStore() + detail.getNum()); //加回库存
                    goods.setSaleCount(goods.getSaleCount() - detail.getNum());//减回销量
                }
            }


        }
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        //对每个订单查看其中的商品
        for(Orders order : list){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getId());
            List<OrderDetail> o = orderDetailMapper.selectAll(orderDetail);
            order.setOrderDetailList(o);
        }
        return PageInfo.of(list);
    }


}