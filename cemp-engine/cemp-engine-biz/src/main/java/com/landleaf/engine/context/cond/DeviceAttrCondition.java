package com.landleaf.engine.context.cond;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.engine.domain.vo.RuleConditionVO;
import com.landleaf.engine.enums.ComparatorType;
import com.landleaf.redis.constance.KeyConstance;

import java.math.BigDecimal;
import java.util.Map;

public class DeviceAttrCondition extends BaseCondition {

    @Override
    public boolean check(RuleConditionVO condition, JSONObject obj) {
        // 判断是否当前设备，如果是，则直接使用当前设备信息判断
        if ("0".equals(condition.getCorBizDeviceId()) || obj.getString("bizDeviceId").equals(condition.getCorBizDeviceId())) {
            JSONObject property = obj.getJSONObject("propertys");
            if (property.containsKey(condition.getAttrCode())) {
                // 判断值
                return checkValueLegal(condition.getComparator(), property.getBigDecimal(condition.getAttrCode()), new BigDecimal(condition.getCompareVal()));
            }
            condition.setCorBizDeviceId(obj.getString("bizDeviceId"));
        }
        // 当设备为其它设备，或当前上报信息中并不包含对应的设备的属性时，需要从redis中获取对应的属性。
        // 此处，为了性能，自己执行redis的查询操作，不走dataApi。
        Map<Object, Object> deviceCurrentStatus = redisUtils.hmget(KeyConstance.DEVICE_CURRENT_STATUS_V1 + condition.getCorBizDeviceId());
        // 按照原来的逻辑，这部分应该只取property
        if (null != deviceCurrentStatus && deviceCurrentStatus.containsKey("propertys")) {
            Map<String, Object> propertys = (Map<String, Object>) deviceCurrentStatus.get("propertys");
            if (null != propertys && propertys.containsKey(condition.getAttrCode())) {
                Map<String, Object> temp = (Map<String, Object>) propertys.get(condition.getAttrCode());
                return checkValueLegal(condition.getComparator(), new BigDecimal(String.valueOf(temp.get("val"))), new BigDecimal(condition.getCompareVal()));
            }
            return false;
        }
        return false;
    }

    private boolean checkValueLegal(String comparator, BigDecimal val, BigDecimal targetVal) {
        if (ComparatorType
                .EQ.getCode().equals(comparator)) {
            return val.equals(targetVal);
        } else if (ComparatorType
                .NE.getCode().equals(comparator)) {
            return !val.equals(targetVal);
        } else if (ComparatorType
                .GE.getCode().equals(comparator)) {
            return val.compareTo(targetVal) >= 0;
        } else if (ComparatorType
                .LE.getCode().equals(comparator)) {
            return val.compareTo(targetVal) <= 0;
        } else if (ComparatorType
                .GT.getCode().equals(comparator)) {
            return val.compareTo(targetVal) > 0;
        } else if (ComparatorType
                .LT.getCode().equals(comparator)) {
            return val.compareTo(targetVal) < 0;
        }
        return false;
    }
}
