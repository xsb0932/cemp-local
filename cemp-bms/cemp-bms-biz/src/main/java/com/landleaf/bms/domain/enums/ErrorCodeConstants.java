package com.landleaf.bms.domain.enums;


import com.landleaf.comm.exception.ErrorCode;

/**
 * bms 错误码枚举类
 * <p>
 * bms 系统，使用 1-003-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 管理节点 模块 1003000000 ==========
    ErrorCode ROOT_NODE_NOT_EXIST = new ErrorCode("1003000000", "租户管理节点根节点不存在");
    ErrorCode ROOT_NODE_EXISTED = new ErrorCode("1003000001", "租户管理节点根节点已存在");
    ErrorCode NODE_CODE_NOT_UNIQUE = new ErrorCode("1003000002", "管理节点编码不唯一");
    ErrorCode NODE_NOT_EXIST = new ErrorCode("1003000003", "节点不存在");
    ErrorCode NODE_EXIST_CHILDREN = new ErrorCode("1003000004", "当前节点存在下级节点，不允许删除");

    // ========== 用户管理节点权限 模块 1003001000 ==========
    ErrorCode USER_NODE_TYPE_NOT_UNIQUE = new ErrorCode("1003001000", "用户管理节点权限类型不能是多种共存");
    ErrorCode USER_NODE_PERMISSION_NOT_ENOUGH = new ErrorCode("1003001001", "当前用户角色权限不够");

    // ========== 数据字典 模块 1003002000 ==========
    ErrorCode DICT_IN_USED = new ErrorCode("1003002000", "数据字典已被使用");
    ErrorCode DICT_TYPE_NOT_EXIST = new ErrorCode("1003002001", "数据字典类型不存在");
    ErrorCode DICT_DATA_NOT_EXIST = new ErrorCode("1003002002", "数据字典码值不存在");
    ErrorCode DICT_DATA_VALUE_NOT_UNIQUE = new ErrorCode("1003002003", "数据字典码值不唯一");
    ErrorCode DICT_DATA_DEFAULT_MODIFY_FORBID = new ErrorCode("1003002004", "默认码值不允许修改");
    ErrorCode DICT_DATA_DEFAULT_DELETE_FORBID = new ErrorCode("1003002004", "默认码值不允许删除");

    // ========== 功能管理 模块 1003003000 ==========
    ErrorCode IDENTIFIER_NOT_UNIQUE = new ErrorCode("1003003000", "功能标识符已被使用");

    // ========== 项目管理 模块 1003004000 ==========
    ErrorCode PROJECT_NAME_NOT_UNIQUE = new ErrorCode("1003004000", "项目名称已被使用");
    ErrorCode PROJECT_CODE_NOT_UNIQUE = new ErrorCode("1003004001", "项目编码已被使用");
    ErrorCode PROJECT_NOT_EXIST = new ErrorCode("1003004002", "项目不存在");
    ErrorCode PROJECT_SPACE_NAME_NOT_UNIQUE = new ErrorCode("1003004003", "项目区域名称已被使用");
    ErrorCode PROJECT_SPACE_NOT_EXIST = new ErrorCode("1003004004", "项目区域不存在");

    ErrorCode PROJECT_SPACE_NOT_DELETE = new ErrorCode("1003004005", "项目区域无法删除");

    // ========== 产品管理 模块 1003005000 ==========
    ErrorCode PRODUCT_EDIT_FORBID = new ErrorCode("1003005000", "已发布状态不可修改");
    ErrorCode PRODUCT_DELETE_FORBID = new ErrorCode("1003005001", "被关联的产品不可删除");
    ErrorCode PRODUCT_REF_FORBID = new ErrorCode("1003005002", "产品不可关联");
    ErrorCode PRODUCT_PARAM_MODIFY_FORBID = new ErrorCode("1003005003", "当前产品参数禁止修改");
    ErrorCode PRODUCT_PARAM_NOT_EXISTED = new ErrorCode("1003005004", "当前产品参数不存在");
    ErrorCode PRODUCT_DEVICE_EXIST = new ErrorCode("1003005005", "产品下存在设备");
    ErrorCode NOT_PERMISSION = new ErrorCode("1003005006", "权限不足");
    ErrorCode NOT_PERMISSION_REF = new ErrorCode("1003005007", "未发布的产品不允许被引用");
    ErrorCode ALARM_CODE_NOT_UNIQUE = new ErrorCode("1003005008", "产品告警码已存在");
    ErrorCode ALARM_CODE_DELETE_NOT_PERMISSION = new ErrorCode("1003005009", "产品告警码禁止删除");

    // ========== 品类管理 模块 1003006000 ==========
    ErrorCode CATEGORY_ROOT_NODE_NOT_EXIST = new ErrorCode("1003006000", "品类根节点不存在");
    ErrorCode CATEGORY_NOT_EXIST = new ErrorCode("1003006001", "品类不存在");
    ErrorCode CATEGORY_CATALOGUE_NOT_EXIST = new ErrorCode("1003006002", "品类目录不存在");
    ErrorCode PARENT_CATEGORY_CATALOGUE_NOT_EXIST = new ErrorCode("1003006003", "父级品类目录不存在");
    ErrorCode CATEGORY_CATALOGUE_NOT_DELETE = new ErrorCode("1003006004", "品类目录无法删除");
    ErrorCode CATEGORY_CATALOGUE_NAME_EXIST = new ErrorCode("1003006005", "品类目录名称已存在");
    ErrorCode CATEGORY_NAME_EXIST = new ErrorCode("1003006006", "品类名称已存在");
    ErrorCode CATEGORY_ATTRIBUTE_NOT_EXIST_OR_NOT_EDITABLE = new ErrorCode("1003006007", "目标不存在或不可编辑");

    // ========== 网关管理 模块 1003007000 ==========
    ErrorCode MQTT_HTTP_FAILED = new ErrorCode("1003007000", "调用mqtt，HTTP API 失败");
    ErrorCode GATEWAY_JS_NULL = new ErrorCode("1003007001", "JS脚本还处于初始化状态。现在启用网关，数据处理过程可能异常。是否继续启用？");
    ErrorCode GATEWAY_JS_NOT_RUN = new ErrorCode("1003007002", "JS脚本修改后还未进行模拟运行，或者模拟运行失败。现在启用网关，数据处理过程可能异常。是否继续启用？");
    ErrorCode SCRIPT_HANDLE_ERROR = new ErrorCode("1003007003", "JS脚本处理错误");
}
