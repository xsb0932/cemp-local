package com.landleaf.comm.util.object;

import com.landleaf.comm.base.pojo.PageParam;

/**
 * {@link  com.landleaf.comm.base.pojo.PageParam} 工具类
 *
 * @author 张力方
 */
public class PageUtils {

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

}
