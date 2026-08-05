package com.gjq.train.member.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>
 * 会员
 * </p>
 *
 * @author 郭建泉
 * @since 2026-07-30
 */
@TableName("member")
@Data
public class Member {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;
}
