package com.landleaf.jjgj.domain.response;

import com.landleaf.jjgj.domain.entity.ProjectCnfChargeStationEntity;
import com.landleaf.jjgj.domain.entity.ProjectCnfPvEntity;
import com.landleaf.jjgj.domain.entity.ProjectCnfStorageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 能源子系统配置-返回值
 *
 * @author Tycoon
 * @since 2023/8/11 17:36
 **/
@Data
public class EnergySubSystemCnfResponse {

    /**
     * 光伏配置
     */
    @Schema(description = "光伏配置")
    private Pv pv;

    /**
     * 是否有光伏
     */
    @Schema(description = "是否有光伏")
    private Boolean hasPv;

    /**
     * 储能配置
     */
    @Schema(description = "储能配置")
    private Storage storage;

    /**
     * 是否有储能
     */
    @Schema(description = "是否有储能")
    private Boolean hasStorage;

    /**
     * 充电桩配置
     */
    @Schema(description = "充电桩配置")
    private ChargeStation chargeStation;

    /**
     * 是否有充电站
     */
    @Schema(description = "是否有充电站")
    private Boolean hasChargeStation;

    public static EnergySubSystemCnfResponse from(String projectBizId,boolean hasPv, ProjectCnfPvEntity pvEntity,
                                                  boolean hasStorage, ProjectCnfStorageEntity storageEntity,
                                                  boolean hasChargeStation, ProjectCnfChargeStationEntity cnfChargeStationEntity) {

        EnergySubSystemCnfResponse response = new EnergySubSystemCnfResponse();
        response.setHasPv(hasPv);
        response.setHasStorage(hasStorage);
        response.setHasChargeStation(hasChargeStation);
        if (hasPv) {
            Pv pv = new Pv();
            if (pvEntity == null) {
                pv.setProjectBizId(projectBizId);
            } else {
                pv.setProjectBizId(pvEntity.getProjectId());
                pv.setOnlineMode(pvEntity.getOnlineMode());
                pv.setPrice(pvEntity.getPrice());
                pv.setCapacity(pvEntity.getTotalRp());
            }
            response.setPv(pv);
        }

        if (hasStorage) {
            Storage storage = new Storage();
           if (storageEntity == null) {
               storage.setProjectBizId(projectBizId);
           } else {
               storage.setProjectBizId(storageEntity.getProjectId());
               storage.setRatedPower(storageEntity.getTotalRp());
               storage.setStorageTotal(storageEntity.getTotalStorage());
           }
            response.setStorage(storage);
        }

        if (hasChargeStation) {
            ChargeStation chargeStation = new ChargeStation();
            if (cnfChargeStationEntity == null) {
                chargeStation.setProjectBizId(projectBizId);
            } else {
                chargeStation.setProjectBizId(cnfChargeStationEntity.getProjectId());
                chargeStation.setBillingMode(cnfChargeStationEntity.getBillingMode());
                chargeStation.setPrice(cnfChargeStationEntity.getPrice());
                chargeStation.setRatedPower(cnfChargeStationEntity.getRp());
                chargeStation.setAcStationNum(cnfChargeStationEntity.getAcStationNum());
                chargeStation.setDcStationNum(cnfChargeStationEntity.getDcStationNum());
                if (response.getHasPv() != null) {
                    chargeStation.setElectricityPrice(response.getPv().getPrice());
                } else {
                    chargeStation.setElectricityPrice(null);
                }

            }
            response.setChargeStation(chargeStation);
        }

        return response;
    }


    @Data
    public static class Pv {

        /**
         * 项目业务ID
         */
        private String projectBizId;

        /**
         * 上网模式
         */
        private String onlineMode;

        /**
         * 价格
         */
        private BigDecimal price;

        /**
         * 装机容量
         */
        private BigDecimal capacity;
    }

    @Data
    public static class Storage {

        /**
         * 项目业务ID
         */
        private String projectBizId;

        /**
         * 额定功率
         */
        private BigDecimal ratedPower;

        /**
         * 最大电量
         */
        private BigDecimal storageTotal;

    }

    @Data
    public static class ChargeStation {

        /**
         * 项目业务ID
         */
        private String projectBizId;

        /**
         * 计费模式
         */
        private String billingMode;

        /**
         * 电费
         */
        private BigDecimal electricityPrice;

        /**
         * 服务费
         */
        private BigDecimal price;

        /**
         * 额定功率
         */
        private BigDecimal ratedPower;

        /**
         * 交流桩数量
         */
        private Long acStationNum;

        /**
         * 直流桩数量
         */
        private Long dcStationNum;

    }

}
