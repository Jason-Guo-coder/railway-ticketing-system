package com.gjq.train.common.resp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共接口返回结果。
 *
 * @param <T> 返回内容类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private boolean success = true;

    private String message;

    private T content;

    /**
     * 创建不包含返回内容的成功结果。
     */
    public static <T> Result<T> success() {
        return new Result<>(true, null, null);
    }

    /**
     * 创建包含返回内容的成功结果。
     */
    public static <T> Result<T> success(T content) {
        return new Result<>(true, null, content);
    }

    /**
     * 创建失败结果。
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(false, message, null);
    }
}
