package com.rehab.managerv2.common;

import lombok.Data;

/**
 * 统一 API 响应结果封装
 */
@Data // 咱们刚刚调通的 Lombok 派上大用场了！
public class Result<T> {
    private Integer code; // 状态码：200代表成功，500代表失败等
    private String msg;   // 提示信息
    private T data;       // 真正要返回的数据

    // 成功时的快捷方法 (带数据)
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 成功时的快捷方法 (不带数据，比如删除成功)
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败时的快捷方法
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}