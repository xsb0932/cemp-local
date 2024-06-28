package com.landleaf.gw.service.impl;


import com.landleaf.gw.conf.LgcConstance;
import com.landleaf.gw.service.LgcDeviceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class LgcDeviceServiceImpl implements LgcDeviceService {

    @Resource


    private static Map<String, String> gaugesIdRelationMap = new HashMap<>();
    private static Map<String, String> airConditionIdRelationMap = new HashMap<>();
    private static Map<String, String> airConditionIdRelationRevertMap = new HashMap<>();

    private static Map<String, Map<String, String>> idRelationMap = new HashMap<>();

    private static Map<String, String> bizProdIdRelationMap = new HashMap<>();

    static {
        gaugesIdRelationMap.put("B01-ZPD-WM-001", "D000000000001");
        gaugesIdRelationMap.put("B01-ZPD-EM-3F4F", "D000000000002");
        gaugesIdRelationMap.put("B01-ZPD-EM-1F2F", "D000000000003");
        gaugesIdRelationMap.put("B01-ZPD-EM-DT", "D000000000004");
        gaugesIdRelationMap.put("B01-ZPD-EM-SB", "D000000000005");
        gaugesIdRelationMap.put("B01-KCPD-EM-2F", "D000000000006");
        gaugesIdRelationMap.put("B01-KCPD-EM-2FKT1", "D000000000061");
        gaugesIdRelationMap.put("B01-KCPD-EM-2FKT2", "D000000000062");
        gaugesIdRelationMap.put("B01-KCPD-EM-3F", "D000000000008");
        gaugesIdRelationMap.put("B01-KCPD-EM-3FKT1", "D000000000063");
        gaugesIdRelationMap.put("B01-KCPD-EM-3FKT2", "D000000000064");
        gaugesIdRelationMap.put("B01-KCPD-EM-4F", "D000000000010");
        gaugesIdRelationMap.put("B01-KCPD-EM-4FKT1", "D000000000065");
        gaugesIdRelationMap.put("B01-KCPD-EM-4FKT2", "D000000000066");
        gaugesIdRelationMap.put("RSPD-EM", "D000000000012");
        gaugesIdRelationMap.put("RSPD-EM-RB", "D000000000013");
        gaugesIdRelationMap.put("RSPD-EM-RB-B1", "D000000000014");
        gaugesIdRelationMap.put("RSPD-EM-RB-B2", "D000000000015");
        gaugesIdRelationMap.put("RSPD-EM-RB-B3", "D000000000016");
        gaugesIdRelationMap.put("B02-ZPD1-EM-CF", "D000000000017");
        gaugesIdRelationMap.put("B02-ZPD1-EM-1F", "D000000000018");
        gaugesIdRelationMap.put("B02-ZPD1-EM-DT", "D000000000019");
        gaugesIdRelationMap.put("B02-ZPD2-EM-3F4F", "D000000000020");
        gaugesIdRelationMap.put("B02-ZPD2-EM-2F", "D000000000021");
        gaugesIdRelationMap.put("B02-ZPD2-EM-BF", "D000000000022");
        gaugesIdRelationMap.put("B02-ZPD2-EM-KT", "D000000000023");
        gaugesIdRelationMap.put("B02-KCPD-EM-2F", "D000000000024");
        gaugesIdRelationMap.put("B02-KCPD-EM-2FKT", "D000000000025");
        gaugesIdRelationMap.put("B02-KCPD-EM-3F", "D000000000026");
        gaugesIdRelationMap.put("B02-KCPD-EM-3FKT", "D000000000027");
        gaugesIdRelationMap.put("B02-KCPD-EM-4F", "D000000000028");
        gaugesIdRelationMap.put("B02-KCPD-EM-4FKT", "D000000000029");
        gaugesIdRelationMap.put("B02-1F-EM-KT01", "D000000000030");
        gaugesIdRelationMap.put("B02-1F-EM-KT02", "D000000000031");
        gaugesIdRelationMap.put("B02-1F-EM-KT03", "D000000000032");
        gaugesIdRelationMap.put("B02-1F-EM-KT04", "D000000000033");
        gaugesIdRelationMap.put("B02-1F-EM-KT05", "D000000000034");
        gaugesIdRelationMap.put("B02-1F-EM-KT06", "D000000000035");
        gaugesIdRelationMap.put("B02-1F-EM-ZM01", "D000000000036");
        gaugesIdRelationMap.put("B02-1F-EM-ZM02", "D000000000037");
        gaugesIdRelationMap.put("B02-1F-EM-ZM03", "D000000000038");
        gaugesIdRelationMap.put("B02-4F-EM-505", "D000000000039");
        gaugesIdRelationMap.put("B02-4F-EM-503", "D000000000040");
        gaugesIdRelationMap.put("B02-3F-EM-303", "D000000000041");
        gaugesIdRelationMap.put("B02-3F-EM-312", "D000000000042");
        gaugesIdRelationMap.put("B02-2F-EM-203", "D000000000043");
        gaugesIdRelationMap.put("SHLL30525S1", "D000000000044");
        gaugesIdRelationMap.put("SHLL30525S4", "D000000000045");
        gaugesIdRelationMap.put("SHLL30525S3", "D000000000046");
        gaugesIdRelationMap.put("SHLL30525S2", "D000000000047");
        airConditionIdRelationMap.put("68799", "D000000000048");
        airConditionIdRelationMap.put("78015", "D000000000049");
        airConditionIdRelationMap.put("78084", "D000000000050");
        airConditionIdRelationMap.put("80670", "D000000000051");
        airConditionIdRelationMap.put("80607", "D000000000052");
        airConditionIdRelationMap.put("80602", "D000000000053");
        airConditionIdRelationMap.put("80599", "D000000000054");
        airConditionIdRelationMap.put("80603", "D000000000055");
        airConditionIdRelationMap.put("80600", "D000000000056");
        airConditionIdRelationMap.put("80601", "D000000000057");
        airConditionIdRelationMap.put("80598", "D000000000058");
        airConditionIdRelationMap.put("80604", "D000000000059");
        airConditionIdRelationMap.put("80605", "D000000000060");

        airConditionIdRelationRevertMap.put("D000000000048", "68799");
        airConditionIdRelationRevertMap.put("D000000000049", "78015");
        airConditionIdRelationRevertMap.put("D000000000050", "78084");
        airConditionIdRelationRevertMap.put("D000000000051", "80670");
        airConditionIdRelationRevertMap.put("D000000000052", "80607");
        airConditionIdRelationRevertMap.put("D000000000053", "80602");
        airConditionIdRelationRevertMap.put("D000000000054", "80599");
        airConditionIdRelationRevertMap.put("D000000000055", "80603");
        airConditionIdRelationRevertMap.put("D000000000056", "80600");
        airConditionIdRelationRevertMap.put("D000000000057", "80601");
        airConditionIdRelationRevertMap.put("D000000000058", "80598");
        airConditionIdRelationRevertMap.put("D000000000059", "80604");
        airConditionIdRelationRevertMap.put("D000000000060", "80605");

        idRelationMap.put(LgcConstance.GAUGES_SUPPLIER_ID, gaugesIdRelationMap);
        idRelationMap.put(LgcConstance.AIR_CONDITION_SUPPLIER_ID, airConditionIdRelationMap);

        bizProdIdRelationMap.put("D000000000044", "PK00000002");
        bizProdIdRelationMap.put("D000000000045", "PK00000001");
        bizProdIdRelationMap.put("D000000000046", "PK00000001");
        bizProdIdRelationMap.put("D000000000047", "PK00000001");
    }

    @Override
    public String getBizDeviceIdBySupplierAndOuterId(String supplierId, String outerDeviceId) {
        return idRelationMap.get(supplierId).containsKey(outerDeviceId) ? idRelationMap.get(supplierId).get(outerDeviceId) : outerDeviceId;
    }

    @Override
    public String getBizProdIdByBizDeviceId(String bizDeviceId) {
        return bizProdIdRelationMap.containsKey(bizDeviceId) ? bizProdIdRelationMap.get(bizDeviceId) : bizDeviceId;
    }

    @Override
    public String getOuterIdByBizDeviceId(String bizDeviceId) {
        return airConditionIdRelationRevertMap.get(bizDeviceId);
    }
}
