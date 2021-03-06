package com.yemu.mallportal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yemu.mallportal.Exception.OrderException;
import com.yemu.mallportal.entity.*;
import com.yemu.mallportal.mapper.OrderMapper;
import com.yemu.mallportal.service.CartService;
import com.yemu.mallportal.service.OrderService;
import com.yemu.mallportal.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author yemuc
 * @date 2020/3/25
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    final ProductService productService;
    final CartService cartService;
    public OrderServiceImpl(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }


    /***
     * 创建订单
     * @param cartItem 购物车项
     * @param address 地址
     * @param payWay 支付方式
     * @param remarks 备注
     * @param user 用户
     * @return 创建的订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(CartItem cartItem, Address address,User user, String payWay, String remarks) {
        if (cartItem==null){
            throw new OrderException(500,"未在购物车找到该商品");
        }
        if (address==null){
            throw new OrderException(500,"地址错误");
        }
        if (user==null){
            throw new OrderException(500,"请尝试重新登录");
        }
        Product product = productService.getById(cartItem.getPid());
        // 检查库存是否足够
        if (product.getStock()<cartItem.getNum()){
            throw new OrderException(400,"库存不足");
        }
        try {
            // 减去库存
            product.setStock(product.getStock()-cartItem.getNum());
            // 更新库存
            productService.updateById(product);
            // 生成订单
            Order order = generateOrder(cartItem,address,user,payWay,remarks);
            getBaseMapper().insert(order);
            // 从购物车删去已生成的项
            cartService.deleteCartItem(cartItem);
            return order;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Order> search(int uid, String content) {
        return baseMapper.search(uid,content);
    }

    @Override
    public Order getByOrderNumber(String orderNumber) {
        return baseMapper.getByOrderNumber(orderNumber);
    }

    /***
     * 生成订单
     * @param cartItem 购物车项
     * @param address 地址
     * @param payWay 支付方式
     * @param remarks 备注
     * @param user 用户
     * @return 创建的订单
     */
    private Order generateOrder(CartItem cartItem, Address address,User user, String payWay, String remarks){
        Product product = productService.getById(cartItem.getPid());
        Order order = new Order();
        order.setUid(user.getId());
        order.setPid(product.getId());
        order.setNumber(cartItem.getNum());
        order.setTotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getNum())));
        order.setAddressId(address.getId());
        order.setStatus(0);
        order.setRemarks(remarks);
        order.setPayWay(payWay);
        createOrderNumber(order);
        return order;
    }

    /**
     * 创建订单号 编号规则为时间戳（到毫秒）+用户id+商品id
     * @param order 订单
     */
    private void createOrderNumber(Order order){
        String orderNumber = String.valueOf(new Date().getTime()) +
                order.getUid() +
                order.getPid();
        order.setOrderNumber(orderNumber);
    }
}
