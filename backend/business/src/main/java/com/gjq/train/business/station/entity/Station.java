package com.gjq.train.business.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 车站
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Getter
@Setter
@TableName("station")
public class Station {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 站名
     */
    @TableField("`name`")
    private String name;

    /**
     * 站名拼音
     */
    @TableField("name_pinyin")
    private String namePinyin;

    /**
     * 站名拼音首字母
     */
    @TableField("name_py")
    private String namePy;

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
