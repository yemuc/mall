package com.yemu.mallportal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author yemuc
 * @date 2020/4/29
 */
@Data
@TableName("user_tag")
public class UserTag {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private int uid;
    private String tag;

}
