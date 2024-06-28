package com.landleaf.operatelog.core.dal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志
 *
 * @author 粒方
 */
@Data
@TableName(value = "tb_operate_log")
@EqualsAndHashCode(callSuper = true)
public class OperateLogEntity extends BaseEntity {
    /**
     * 日志主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 用户编号
     * <p>
     * 关联 MemberUserDO 的 id 属性，或者 AdminUserDO 的 id 属性
     */
    private Long userId;
    /**
     * 操作模块
     */
    private String module;
    /**
     * 操作名
     */
    private String name;
    /**
     * 操作分类
     * <p>
     * 枚举 {@link OperateTypeEnum}
     */
    private Integer type;
    /**
     * 操作内容，记录整个操作的明细
     * 例如说，修改编号为 1 的用户信息，将性别从男改成女，将姓名从芋道改成源码。
     */
    private String content;
    /**
     * 请求方法名
     */
    private String requestMethod;
    /**
     * 请求地址
     */
    private String requestUrl;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 用户 MAC 地址
     */
    private String userMac;
    /**
     * 浏览器 UA
     */
    private String userAgent;
    /**
     * Java 方法名
     */
    private String javaMethod;
    /**
     * Java 方法的参数
     * <p>
     * 如果是对象，则使用 JSON 格式化
     */
    private String javaMethodArgs;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 执行时长，单位：毫秒
     */
    private Integer duration;
    /**
     * 结果码
     * <p>
     * 目前使用的 {@link Response#getErrorCode()}属性
     */
    private String resultCode;
    /**
     * 结果提示
     * <p>
     * 目前使用的 {@link Response#getErrorMsg()} 属性
     */
    private String resultMsg;
    /**
     * 结果数据
     * <p>
     * 如果是对象，则使用 JSON 格式化
     */
    private String resultData;

}
