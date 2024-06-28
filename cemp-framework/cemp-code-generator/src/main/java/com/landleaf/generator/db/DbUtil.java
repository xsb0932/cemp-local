package com.landleaf.generator.db;

import com.landleaf.generator.domain.EntityItem;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbUtil {

    private static Connection conn;

    public static String url;

    public static String username;

    public static String password;

    public static Map<String, String> typeConstantMap;

    public static void init() throws SQLException {
        if (null == conn || conn.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(url, username, password);
            } catch (Exception e) {
//                System.out.println("连接db失败。重新连接");
                init();
            }
        }
    }

    public static void close() throws SQLException {
        conn.close();
    }

    public static String getTableComment(String sql) throws SQLException {
        Statement stat;
        try {
            stat = conn.createStatement();
        } catch (Exception e) {
            init();
            stat = conn.createStatement();
        }
        ResultSet rs = stat.executeQuery(sql);
        String tableComment = null;
        // 4.处理数据库的返回结果(使用ResultSet类)
        while (rs.next()) {
            tableComment = rs.getString("TABLE_COMMENT");
        }
        // 关闭资源
        conn.close();
        rs.close();
        return getDesc(tableComment);
    }


    public static List<EntityItem> getColumns(String sql) throws SQLException {
        Statement stat;
        try {
            stat = conn.createStatement();
        } catch (Exception e) {
            init();
            stat = conn.createStatement();
        }
        List<EntityItem> colums = new ArrayList<EntityItem>();
        ResultSet rs = stat.executeQuery(sql);
        // 4.处理数据库的返回结果(使用ResultSet类)
        while (rs.next()) {
            // 跳过deleted,create_time,update_time
            if (rs.getString("COLUMN_NAME").equalsIgnoreCase("creator")) {
                continue;
            }
            if (rs.getString("COLUMN_NAME").equalsIgnoreCase("create_time")) {
                continue;
            }
            if (rs.getString("COLUMN_NAME").equalsIgnoreCase("updater")) {
                continue;
            }
            if (rs.getString("COLUMN_NAME").equalsIgnoreCase("update_time")) {
                continue;
            }
            if (rs.getString("COLUMN_NAME").equalsIgnoreCase("deleted")) {
                continue;
            }


            colums.add(EntityItem.builder().columnName(rs.getString("COLUMN_NAME"))
                    .columnType(typeConstantMap.get(rs.getString("DATA_TYPE").toUpperCase()))
                    .columnDesc(getDesc(rs.getString("COLUMN_COMMENT"))).isPriKey(rs.getString("COLUMN_KEY"))
                    .isAutoIncr(rs.getString("EXTRA"))
                    .columnNameNew(formatName(rs.getString("COLUMN_NAME").toLowerCase())).build());
        }
        return colums;
    }
//
//    public static List<Device> queryDeviceByFamily(String buildingCode, String unitCode, String doorplate, long realestate_id) throws SQLException {
//
//        String sql = "select htd.*,hap.protocol from house_template_device htd left join project_house_template pht on htd.house_template_id=pht.\"id\"\n" +
//                "left join home_auto_family haf on haf.template_id=pht.\"id\"\n" +
//                "left join home_auto_product hap on htd.product_id=hap.\"id\"\n" +
//                "where \n" +
//                "haf.building_code='" + buildingCode + "' and haf.unit_code='" + unitCode + "' and haf.doorplate='" + doorplate + "' and\n" +
//                "pht.realestate_id=" + realestate_id + " order by htd.sn;";
//        Statement stat;
//        try {
//            stat = conn.createStatement();
//        } catch (Exception e) {
//            init();
//            stat = conn.createStatement();
//        }
//        ResultSet rs = stat.executeQuery(sql);
//        List<Device> deviceList = new ArrayList<>();
//        int panelIndex = 100;
//        while (rs.next()) {
//            Device device = new Device();
//            device.setSn(rs.getInt("sn"));
//            device.setProtocol(rs.getInt("protocol"));
//            Object addr = rs.getObject("address_code");
//            device.setAddr(null == addr && "".equals(addr) ? 0 : Integer.valueOf("".equals(String.valueOf(addr)) ? String.valueOf(panelIndex++) : String.valueOf(addr)));
//            device.setRoomId(rs.getLong("room_id"));
//            device.setProdCode(rs.getInt("product_code"));
//            deviceList.add(device);
//        }
//        return deviceList;
//    }
//
//    public static List<Room> queryRoomByFamily(String buildingCode, String unitCode, String doorplate, long realestate_id) throws SQLException {
//        String sql = "select htr.* from home_auto_family haf left join house_template_room htr on haf.template_id=htr.house_template_id \n" +
//                "left join project_house_template pht on htr.house_template_id=pht.\"id\"\n" +
//                "where " +
//                "haf.building_code='" + buildingCode + "' and haf.unit_code='" + unitCode + "' and haf.doorplate='" + doorplate + "' and\n" +
//                "pht.realestate_id=" + realestate_id + " order by htr.id;";
//
//
//        Statement stat;
//        try {
//            stat = conn.createStatement();
//        } catch (Exception e) {
//            init();
//            stat = conn.createStatement();
//        }
//        ResultSet rs = stat.executeQuery(sql);
//        List<Room> roomList = new ArrayList<>();
//        while (rs.next()) {
//            roomList.add(Room.builder().id(rs.getLong("id")).floor(rs.getInt("floor")).name(rs.getString("name")).type(rs.getInt("type")).build());
//        }
//        return roomList;
//    }

    /**
     * 去除desc中的@desc
     *
     * @return
     */
    private static String getDesc(String desc) {
        if (null == desc) {
            return desc;
        }
        if (!desc.startsWith("@desc")) {
            return desc;
        }
        desc = desc.replace("@desc", "");
        desc = desc.replaceAll("\r\n", " ");
        desc = desc.replaceAll("\n", " ");

        if (desc.contains("@values")) {
            desc = desc.replace("@values", ":");
        }
        return desc.trim();
    }

    /**
     * 将包含下划线的名称，去掉下划线，大写
     *
     * @param entityName
     * @return
     */
    public static String formatName(String entityName) {
        if (entityName.contains("_")) {
            // 取消下划线，并且，字母大写
            String[] names = entityName.split("_");
            StringBuilder nameBuilder = new StringBuilder(names[0]);
            for (int i = 1; i < names.length; i++) {
                nameBuilder.append(StringUtils.capitalize(names[i]));
            }
            entityName = nameBuilder.toString();
            return entityName;
        }
        return entityName;
    }
}
