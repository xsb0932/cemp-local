package com.landleaf.generator.db.pgsql;

import java.util.HashMap;
import java.util.Map;

public class TypeConstance {
    public static Map<String, String> map = new HashMap<String, String>() {{
        put("INT2", "Integer");
        put("INT4", "Integer");
        put("INT8", "Long");
        put("VARCHAR", "String");
        put("CHAR", "String");
        put("NUMERIC", "BigDecimal");
        put("TIMESTAMP", "Timestamp");
        put("BIT", "Boolean");
        put("BOOL", "Boolean");
    }};
}
