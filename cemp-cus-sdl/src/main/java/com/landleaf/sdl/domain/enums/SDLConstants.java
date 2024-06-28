package com.landleaf.sdl.domain.enums;

/**
 * 绥德路定制大屏相关
 *
 * @author xushibai
 * @since 2023/11/29
 **/
public class SDLConstants {
    private SDLConstants() {
    }

    /**
     * 项目业务id
     */
    public static final String PROJECT_BIZ_ID = "PJ00001203";

    /**
     * 直流电表产品
     */
    public static final String PRODUCT_DIR_ID = "PK00001215";

    /**
     * 交流电表产品
     */
    public static final String PRODUCT_AT_ID = "PK00001214";

    /**
     * 租户ID
     */
    public static final Long SDL_TENANT_ID = 100004L;

    /**
     * 储能-产品id
     */
    public static final String STORAGE_PRODUCT_BIZ_ID = "PK00001068";

    // ----- 直流电表
    /** 一层直流配电直流电表(一层) */	public static final String ELE_DIR_DEVICE_DBDC1FDIS = "D000000001301";
    /** 风机回路直流电表(一层) */	public static final String ELE_DIR_DEVICE_DBDCFAN = "D000000001298";
    /** 一层地插配电直流电表(一层) */	public static final String ELE_DIR_DEVICE_DBDC1FSOCK = "D000000001305";
    /** 三层直流配电直流电表(三层) */	public static final String ELE_DIR_DEVICE_DBDC3FDIS = "D000000001303";
    /** 二层直流配电直流电表(二层) */	public static final String ELE_DIR_DEVICE_DBDC2FDIS = "D000000001302";
    /** 四层直流配电直流电表(四层) */	public static final String ELE_DIR_DEVICE_DBDC4FDIS = "D000000001304";
    /** 五层照明直流电表(屋面) */	public static final String ELE_DIR_DEVICE_DBDC5FLIGHT = "D000000001306";
    /** 空调回路直流电表(屋面) */	public static final String ELE_DIR_DEVICE_DBDC5FAIRCON = "D000000001300";
    /** DC220母线回路直流电表() */	public static final String ELE_DIR_DEVICE_DBDC220BUS = "D000000001299";
    /** FCS母线电表() */	public static final String ELE_DIR_DEVICE_DBDCFCS = "D000000001294";
    /** 储能系统直流电表() */	public static final String ELE_DIR_DEVICE_DBDCCN = "D000000001296";
    /** 充电系统直流电表() */	public static final String ELE_DIR_DEVICE_DBDCCD = "D000000001297";
    /** 光伏系统直流电表() */	public static final String ELE_DIR_DEVICE_DBDCPV = "D000000001295";

    //交流电表
    /** 电动排烟窗配申箱ATE-1PC1(一层) */	public static final String ELE_ALT_DEVICE_ATE1PC1 = "D000000001329";
    /** 消防控制室双切箱ATE-1XKS(一层) */	public static final String ELE_ALT_DEVICE_ATE1XKS = "D000000001330";
    /** 一层土工双电源箱AT-1AL3(一层) */	public static final String ELE_ALT_DEVICE_AT1AL3 = "D000000001339";
    /** 一层空调配电箱1KL1(一层) */	public static final String ELE_ALT_DEVICE_1KL1 = "D000000001334";
    /** 一层用电配电箱1AL1(一层) */	public static final String ELE_ALT_DEVICE_1AL1 = "D000000001335";
    /** 一层用电配电箱1AL2(一层) */	public static final String ELE_ALT_DEVICE_1AL2 = "D000000001336";
    /** 一层提升电梯配电第AP-1DT1(一层) */	public static final String ELE_ALT_DEVICE_AP1DT1 = "D000000001337";
    /** 一层土工双电源箱AP-1AL3(一层) */	public static final String ELE_ALT_DEVICE_AP1AL3 = "D000000001338";
    /** 三层用电配电箱3AL2(三层) */	public static final String ELE_ALT_DEVICE_3AL2 = "D000000001319";
    /** 三层用电配电箱3AL1(三层) */	public static final String ELE_ALT_DEVICE_3AL1 = "D000000001322";
    /** 三层空调配电箱3KL1(三层) */	public static final String ELE_ALT_DEVICE_3KL1 = "D000000001321";
    /** 三层用电配电箱3AL3(三层) */	public static final String ELE_ALT_DEVICE_3AL3 = "D000000001318";
    /** 弱电双切箱AT-3RD(三层) */	public static final String ELE_ALT_DEVICE_AT3RD = "D000000001320";
    /** 二层空调配电箱2KL1(二层) */	public static final String ELE_ALT_DEVICE_2KL1 = "D000000001327";
    /** 二层用电配电箱2AL1(二层) */	public static final String ELE_ALT_DEVICE_2AL1 = "D000000001328";
    /** 二层文印双电源箱AP-2WY1(二层) */	public static final String ELE_ALT_DEVICE_AP2WY1 = "D000000001323";
    /** 二层文印双电源箱AT-2WY1(二层) */	public static final String ELE_ALT_DEVICE_AT2WY1 = "D000000001324";
    /** 二层用电配电箱2AL3(二层) */	public static final String ELE_ALT_DEVICE_2AL3 = "D000000001325";
    /** 二层用电配电箱2AL2(二层) */	public static final String ELE_ALT_DEVICE_2AL2 = "D000000001326";
    /** 四层试验双电源箱AP-4SY1(四层) */	public static final String ELE_ALT_DEVICE_AP4SY1 = "D000000001311";
    /** 四层用电配电箱4AL2(四层) */	public static final String ELE_ALT_DEVICE_4AL2 = "D000000001313";
    /** 四层用电配电箱4AL1(四层) */	public static final String ELE_ALT_DEVICE_4AL1 = "D000000001316";
    /** 四层空调配电箱4KL1(四层) */	public static final String ELE_ALT_DEVICE_4KL1 = "D000000001315";
    /** 四层试验双电源箱AT-4SY1(四层) */	public static final String ELE_ALT_DEVICE_AT4SY1 = "D000000001312";
    /** 四层航测双电源箱AT-4HC1(四层) */	public static final String ELE_ALT_DEVICE_AT4HC1 = "D000000001314";
    /** 电梯动力箱AT-4DT1(四层) */	public static final String ELE_ALT_DEVICE_AT4DT1 = "D000000001317";
    /** 四层用电配电箱4AL3(四层) */	public static final String ELE_ALT_DEVICE_4AL3 = "D000000001310";
    /** 屋面景观配电箱RAL1(屋面) */	public static final String ELE_ALT_DEVICE_RAL1 = "D000000001307";
    /** 屋面层空调自控箱RKKL1(屋面) */	public static final String ELE_ALT_DEVICE_RKKL1 = "D000000001308";
    /** 屋面空调电源箱RKL1(屋面) */	public static final String ELE_ALT_DEVICE_RKL1 = "D000000001309";
    /** 一层进线总箱1GP03(整栋办公楼) */	public static final String ELE_ALT_DEVICE_1GP03 = "D000000001333";
    /** 一层进线总箱1GP02(整栋办公楼) */	public static final String ELE_ALT_DEVICE_1GP02 = "D000000001332";
    /** 一层进线总箱1GP01(整栋办公楼) */	public static final String ELE_ALT_DEVICE_1GP01 = "D000000001331";

    /** 四层办公多参数 */	public static final String AIR_DCSGQ4F = "D000000001340";
    /** 一层前厅多参数 */	public static final String AIR_DCSQT1F = "D000000001343";
    /** 二层办公多参数 */	public static final String AIR_DCSBG2F = "D000000001342";
    /** 三层过道多参数 */	public static final String AIR_DCSGD3F = "D000000001341";




    /**
     * 1#储能电池系统 BMS-1
     */
    public static final String STORAGE_DEVICE_BMS1 = "D000000001280";



    /**
     * DC220母线回路直流电表
     */
    public static final String ELE_DIR_DEVICE_DC220 = "D000000001299";

}
