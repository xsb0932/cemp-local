package com.landleaf.oauth.domain.enums;


import com.landleaf.comm.exception.ErrorCode;

/**
 * auth 错误码枚举类
 * <p>
 * auth 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== AUTH 模块 1002000000 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode("1002000000", "登录失败，账号密码不正确");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode("1002000001", "登录失败，账号被禁用");
    ErrorCode AUTH_LOGIN_CAPTCHA_CODE_ERROR = new ErrorCode("1002000004", "验证码不正确，原因：{}");
    ErrorCode AUTH_TOKEN_EXPIRED = new ErrorCode("1002000006", "Token 已经过期");

    // ========== 菜单模块 1002001000 ==========
    ErrorCode MENU_NAME_DUPLICATE = new ErrorCode("1002001000", "已经存在该名字的菜单");
    ErrorCode MENU_PARENT_NOT_EXISTS = new ErrorCode("1002001001", "父菜单不存在");
    ErrorCode MENU_PARENT_ERROR = new ErrorCode("1002001002", "不能设置自己为父菜单");
    ErrorCode MENU_NOT_EXISTS = new ErrorCode("1002001003", "菜单不存在");
    ErrorCode MENU_EXISTS_CHILDREN = new ErrorCode("1002001004", "存在子菜单，无法删除");
    ErrorCode MENU_PARENT_NOT_DIR_OR_MENU = new ErrorCode("1002001005", "父菜单的类型必须是目录或者菜单");
    ErrorCode MENU_PATH_CANNOT_BE_MODIFIED = new ErrorCode("1002001006", "菜单路径无法进行修改");

    // ========== 角色模块 1002002000 ==========
    ErrorCode ROLE_NOT_EXISTS = new ErrorCode("1002002000", "角色不存在");
    ErrorCode ROLE_NAME_DUPLICATE = new ErrorCode("1002002001", "已经存在名为【{}】的角色");
    ErrorCode ROLE_CODE_DUPLICATE = new ErrorCode("1002002002", "已经存在编码为【{}】的角色");
    ErrorCode ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE = new ErrorCode("1002002003", "不能操作类型为系统内置的角色");
    ErrorCode ROLE_IS_DISABLE = new ErrorCode("1002002004", "名字为【{}】的角色已被禁用");
    ErrorCode ROLE_ADMIN_CODE_ERROR = new ErrorCode("1002002005", "编码【{}】不能使用");
    ErrorCode USER_ROLE_NOT_EXISTS = new ErrorCode("1002002006", "当前用户角色权限不够");
    ErrorCode USER_ROLE_EXISTS = new ErrorCode("1002002007", "当前角色下存在用户");

    // ========== 用户模块 1002003000 ==========
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode("1002003000", "用户账号已经存在");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode("1002003001", "手机号已经存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode("1002003002", "邮箱已经存在");
    ErrorCode USER_NOT_EXISTS = new ErrorCode("1002003003", "用户不存在");
    ErrorCode USER_PASSWORD_FAILED = new ErrorCode("1002003005", "用户密码校验失败");
    ErrorCode USER_NEW_PASSWORD_FAILED = new ErrorCode("1002003005", "用户新密码校验失败");
    ErrorCode USER_IS_DISABLE = new ErrorCode("1002003006", "名字为【{}】的用户已被禁用");
    ErrorCode USER_CANNOT_BE_DELETED = new ErrorCode("1002003007", "用户无法删除");


    // ========== 租户模块 1002004000 ==========
    ErrorCode USER_NOT_PERMISSION = new ErrorCode("1002004000", "没有权限查看其他租户的数据");

}
