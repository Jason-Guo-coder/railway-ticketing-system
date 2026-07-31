package com.gjq.train.common.resq;

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
public class CommonResq<T> {

    private boolean success = true;

    private String message;

    private T content;

    /**
     * 创建不包含返回内容的成功结果。
     */
    public static <T> CommonResq<T> success() {
        return new CommonResq<>(true, null, null);
    }

    /**
     * 创建包含返回内容的成功结果。
     */
    public static <T> CommonResq<T> success(T content) {
        return new CommonResq<>(true, null, content);
    }

    /**
     * 创建失败结果。
     */
    public static <T> CommonResq<T> fail(String message) {
        return new CommonResq<>(false, message, null);
    }
}
