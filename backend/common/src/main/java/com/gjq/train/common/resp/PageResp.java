package com.gjq.train.common.resp;

import lombok.Data;

import java.util.List;

/**
 * 分页响应结果。
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageResp<T> {

    private Long total;

    private List<T> list;
}
