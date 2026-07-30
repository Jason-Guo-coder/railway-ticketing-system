package com.gjq.train.member.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * <p>
 * 会员
 * </p>
 *
 * @author 郭建泉
 * @since 2026-07-30
 */
@TableName("member")
public class Member {

    /**
     * id
     */
    @TableId("id")
    private Long id;

    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "Member{" +
            "id = " + id +
            ", mobile = " + mobile +
        "}";
    }
}
