package com.cgp.common.entity;

import com.cgp.common.enums.ResultCode;
import com.cgp.common.utils.StringUtils;
import com.github.pagehelper.PageInfo;

import java.util.HashMap;
import java.util.List;

/**
 * api的结果
 *
 * @author Manaphy chen
 * @date 2020/3/16 16:42
 */
@SuppressWarnings("unused")
public class ApiResult extends HashMap<String, Object> {
    /**
     * 状态码
     */
    public static final String CODE = "code";

    /**
     * 返回内容
     */
    public static final String MSG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA = "data";
    /**
     * 分页查询时返回的总数
     */
    private static final String TOTAL = "total";

    /**
     * 初始化一个新创建的 ApiResult 对象，使其表示一个空消息。
     */
    public ApiResult() {
    }

    /**
     * 初始化一个新创建的 ApiResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public ApiResult(int code, String msg) {
        super.put(CODE, code);
        super.put(MSG, msg);
    }

    /**
     * 初始化一个新创建的 ApiResult 对象
     *
     * @param resultCode 结果枚举类
     */
    public ApiResult(ResultCode resultCode) {
        super.put(CODE, resultCode.getCode());
        super.put(MSG, resultCode.getMessage());
    }

    /**
     * 初始化一个新创建的 ApiResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public ApiResult(int code, String msg, Object data) {
        super.put(CODE, code);
        super.put(MSG, msg);
        if (StringUtils.isNotNull(data)) {
            super.put(DATA, data);
        }
    }

    /**
     * 初始化一个新创建的 ApiResult 对象
     *
     * @param resultCode 结果枚举类
     */
    public ApiResult(ResultCode resultCode, Object data) {
        super.put(CODE, resultCode.getCode());
        super.put(MSG, resultCode.getMessage());
        if (StringUtils.isNotNull(data)) {
            super.put(DATA, data);
        }
    }

    public int getCode() {
        return (Integer) this.get(CODE);
    }

    public String getMsg() {
        return (String) this.get(MSG);
    }


    /**
     * 返回成功消息
     */
    public static ApiResult success() {
        return new ApiResult(ResultCode.SUCCESS);
    }

    /**
     * 成功
     * 返回成功数据
     *
     * @param data 数据对象
     */
    public static ApiResult success(Object data) {
        return new ApiResult(ResultCode.SUCCESS, data);
    }

    /**
     * 成功
     * 返回成功自定义消息
     *
     * @param msg 返回信息
     */
    public static ApiResult success(String msg) {
        return ApiResult.success(msg, null);
    }

    /**
     * 成功返回结果
     *
     * @param msg  返回信息
     * @param data 数据对象
     */
    public static ApiResult success(String msg, Object data) {
        return new ApiResult(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败返回结果
     */
    public static ApiResult failed() {
        return new ApiResult(ResultCode.FAILED);
    }

    /**
     * 失败返回结果
     *
     * @param msg 返回信息
     */
    public static ApiResult failed(String msg) {
        return ApiResult.failed(msg, null);
    }

    /**
     * 失败返回结果
     *
     * @param msg  返回信息
     * @param data 数据对象
     */
    public static ApiResult failed(String msg, Object data) {
        return new ApiResult(ResultCode.FAILED.getCode(), msg, data);
    }

    /**
     * 自定义返回信息
     *
     * @param code 状态码
     * @param msg  返回信息
     */
    public static ApiResult result(Integer code, String msg) {
        return new ApiResult(code, msg);
    }

    /**
     * 自定义返回结果
     *
     * @param code 代码
     * @param msg  返回信息
     * @param data 数据对象
     */
    public static ApiResult result(Integer code, String msg, Object data) {
        return new ApiResult(code, msg, data);
    }

    /**
     * JSR303参数验证失败返回结果
     *
     * @param data 数据
     * @return {@link ApiResult}
     */
    public static ApiResult validateFailed(Object data) {
        return new ApiResult(ResultCode.VALIDATE_FAILED, data);
    }

    /**
     * 系统异常
     *
     * @param msg 错误信息
     */
    public static ApiResult exception(String msg) {
        return new ApiResult(ResultCode.EXCEPTION.getCode(), msg);
    }

    /**
     * 用户未登录
     *
     * @param msg 错误信息
     * @return {@link ApiResult}
     */
    public static ApiResult unauthorized(String msg) {
        return new ApiResult(ResultCode.UNAUTHORIZED.getCode(), msg);
    }

    /**
     * 用户没有操作权限
     *
     * @param msg 错误信息
     * @return {@link ApiResult}
     */
    public static ApiResult forbidden(String msg) {
        return new ApiResult(ResultCode.FORBIDDEN.getCode(), msg);
    }

    /**
     * 空指针异常
     *
     * @param msg 错误信息
     * @return {@link ApiResult}
     */
    public static ApiResult nullPointer(String msg) {
        return new ApiResult(ResultCode.NULL_POINTER.getCode(), msg);
    }

    /**
     * 根据状态返回正确或错误信息
     *
     * @param row 受影响的行数
     */
    public static ApiResult boolResult(int row) {
        return row > 0 ? ApiResult.success() : ApiResult.failed();
    }

    /**
     * 根据状态返回正确或错误信息
     *
     * @param flag true or false
     */
    public static ApiResult boolResult(boolean flag) {
        return flag ? ApiResult.success() : ApiResult.failed();
    }

    /**
     * 响应请求分页数据
     *
     * @param list 分页数据
     */
    public static ApiResult pageHelper(List<?> list) {
        ApiResult success = ApiResult.success();
        success.put("rows", list);
        success.put(TOTAL, new PageInfo<>(list).getTotal());
        return success;
    }


}
