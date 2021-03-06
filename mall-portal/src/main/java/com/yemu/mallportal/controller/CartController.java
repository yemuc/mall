package com.yemu.mallportal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yemu.mall.common.R;
import com.yemu.mall.common.TokenUtil;
import com.yemu.mallportal.entity.CartItem;
import com.yemu.mallportal.service.CartService;
import com.yemu.mallportal.service.ImgService;
import com.yemu.mallportal.service.ProductService;
import com.yemu.mallportal.service.UserLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final ImgService imgService;
    private final UserLogService userLogService;

    public CartController(CartService cartService, ProductService productService, ImgService imgService, UserLogService userLogService) {
        this.cartService = cartService;
        this.productService = productService;
        this.imgService = imgService;
        this.userLogService = userLogService;
    }

    /**
     * 向购物车添加
     */
    @PostMapping("")
    public R<?> add(@RequestHeader(required = false) String token, CartItem cartItem) {
        int uid = (token == null || token.isEmpty()) ? 0 : TokenUtil.getUID(token);
        if (uid == 0) {
            return R.error("用户未登录！");
        }
        cartItem.setUid(uid);
        if (cartService.add(cartItem) != null) {
            // 收集添加进购物车数据
            userLogService.addCart(token, cartItem.getPid());
           return R.ok("加入购物车成功！", cartItem);
        }
       return R.error("加入购物车失败!");
    }
    /**
     * 查询购物车内容
     */

    @GetMapping("")
    public R<?> get(@RequestHeader(required = false) String token){
        int uid = (token == null || token.isEmpty()) ? 0 : TokenUtil.getUID(token);
        if (uid==0){
            return R.error("用户未登录！");
        }
        List<CartItem> cartItemList = cartService.getCartByUid(uid);
        List<Map<String,?>> cart = new ArrayList<>();
        for (CartItem cartItem : cartItemList){
            cart.add(getCartModel(cartItem));
        }
        return R.ok(cart);
    }

    /**
     * 根据cartItem拼装成返回前端的数据格式
     * @param cartItem 购物车项
     * @return cartModel
     */
    private Map<String,Object> getCartModel(CartItem cartItem){
        Map<String,Object> cartModel = new HashMap<>(16);
        cartModel.put("product",productService.getById(cartItem.getPid()));
        cartModel.put("imgList",imgService.getMain(cartItem.getPid()));
        cartModel.put("num",cartItem.getNum());
        cartModel.put("cartItemId",cartItem.getId());
        return cartModel;
    }

    /**
     * 根据购物车id获取购物车详情
     */
    @GetMapping("{id}")
    public R<?> getByCartItemId(@RequestHeader String token,@PathVariable("id") int id){
        int uid = TokenUtil.getUID(token);
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid).eq("id",id);
        CartItem cartItem = cartService.getBaseMapper().selectOne(queryWrapper);
        if (cartItem==null){
            return R.error(404,"无此项");
        }
        return R.ok(getCartModel(cartItem));
    }

    /**
     * 更新购物车某项
     */
    @PatchMapping("/{id}")
    public R<?> update(@RequestHeader(required = false) String token,
                    @PathVariable("id") int id, int num){
        int uid = (token == null || token.isEmpty()) ? 0 : TokenUtil.getUID(token);
        if (uid==0){
            return R.error("用户未登录！");
        }
        CartItem cartItem = cartService.getById(id);
        if (cartItem!=null){
            cartItem.setNum(num);
            if (num>productService.getById(cartItem.getPid()).getStock()){
                return R.error("库存不足！");
            }
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id",id);
            return cartService.getBaseMapper().update(cartItem,updateWrapper)>0?R.ok(cartItem):R.error("更新失败");
        }else {
            return R.error("没有此记录");
        }
    }
    /**
     * 清空购物车
     */
    @DeleteMapping("")
    public R<?> delete(@RequestHeader String token){
        int uid = (token == null || token.isEmpty()) ? 0 : TokenUtil.getUID(token);
        if (uid==0){
            return R.error("用户未登录！");
        }
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid",uid);
        return cartService.getBaseMapper().delete(updateWrapper)>0?R.ok("清空成功"):R.error("清空失败");
    }
    /**
     * 从购物车删除某项
     */
    @DeleteMapping("/{id}")
    public R<?> deleteProduct(@RequestHeader String token,@PathVariable("id") int id){
        int uid = (token == null || token.isEmpty()) ? 0 : TokenUtil.getUID(token);
        if (uid==0){
            return R.error("用户未登录！");
        }
        CartItem cartItem = new CartItem().setId(id).setUid(uid);
        return cartService.deleteCartItem(cartItem)?R.ok("删除成功！"):R.error("删除失败！");
    }
}
