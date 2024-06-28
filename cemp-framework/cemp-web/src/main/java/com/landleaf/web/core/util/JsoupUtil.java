package com.landleaf.web.core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * Xss过滤工具
 *
 * @author eason
 */
public class JsoupUtil {
    private static final Safelist SAFELIST = Safelist.basicWithImages();
    private static final Document.OutputSettings OUTPUT_SETTINGS = new Document.OutputSettings().prettyPrint(false);

    static {
        /*
         * 富文本编辑时一些样式是使用style来进行实现的 比如红色字体 style="color:red;" 所以需要给所有标签添加style属性
         */
        SAFELIST.addAttributes(":all", "style");
    }

    public static String clean(String content) {
        return Jsoup.clean(content, "", SAFELIST, OUTPUT_SETTINGS);
    }
}
