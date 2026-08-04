package com.gjq.train.member.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 乘车人
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-02
 */
@Getter
@Setter
@TableName("passenger")
public class PassengerSaveReq {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会员id
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @TableField("`name`")
    private String name;

    /**
     * 身份证
     */
    @NotBlank(message = "身份证不能为空")
    @TableField("id_card")
    private String idCard;

    /**
     * 旅客类型|枚举[PassengerTypeEnum]
     */
    @NotBlank(message = "旅客类型不能为空")
    @TableField("`type`")
    private String type;

    /**
     * 新增时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
