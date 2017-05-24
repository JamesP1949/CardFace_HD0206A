package com.wis.utils;

import cn.com.aratek.idcard.IDCardReader;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public class GlobalConstant {
    /* *****************************读卡器操作状态信息*************************/
    public static final String MSG_RESULT_OK = "操作成功";
    public static final String MSG_RESULT_FAIL = "操作失败";
    public static final String MSG_WRONG_CONNECTION = "设备连接错误";
    public static final String MSG_DEVICE_BUSY = "设备正忙";
    public static final String MSG_DEVICE_NOT_OPEN = "设备未打开";
    public static final String MSG_TIMEOUT = "超时";
    public static final String MSG_NO_PERMISSION = "未授权或无权限";
    public static final String MSG_WRONG_PARAMETER = "参数错误";
    public static final String MSG_DECODE_ERROR = "解码错误";
    public static final String MSG_INIT_FAIL = "初始化失败";
    public static final String MSG_UNKNOWN_ERROR = "未知错误";
    public static final String MSG_NOT_SUPPORT = "不支持";
    public static final String MSG_NOT_ENOUGH_MEMORY = "内存不足";
    public static final String MSG_DEVICE_NOT_FOUND = "未找到支持的设备";
    public static final String MSG_DEVICE_REOPEN = "设备重复打开";
    public static final String MSG_NO_CARD = "没有检测到身份证";
    public static final String MSG_INVALID_CARD = "无法识别的身份证";
    public static final String MSG_INVALID_DECODE_LIB = "身份证解码库错误";

    /**
     * @param errCode
     * @return
     */
    public static String getMsg(int errCode) {
        switch (errCode) {
            case IDCardReader.RESULT_OK:
                return MSG_RESULT_OK;
            case IDCardReader.RESULT_FAIL:
                return MSG_RESULT_FAIL;
            case IDCardReader.WRONG_CONNECTION:
                return MSG_WRONG_CONNECTION;
            case IDCardReader.DEVICE_BUSY:
                return MSG_DEVICE_BUSY;
            case IDCardReader.DEVICE_NOT_OPEN:
                return MSG_DEVICE_NOT_OPEN;
            case IDCardReader.TIMEOUT:
                return MSG_TIMEOUT;
            case IDCardReader.NO_PERMISSION:
                return MSG_NO_PERMISSION;
            case IDCardReader.WRONG_PARAMETER:
                return MSG_WRONG_PARAMETER;
            case IDCardReader.DECODE_ERROR:
                return MSG_DECODE_ERROR;
            case IDCardReader.INIT_FAIL:
                return MSG_INIT_FAIL;
            case IDCardReader.UNKNOWN_ERROR:
                return MSG_UNKNOWN_ERROR;
            case IDCardReader.NOT_SUPPORT:
                return MSG_NOT_SUPPORT;
            case IDCardReader.NOT_ENOUGH_MEMORY:
                return MSG_NOT_ENOUGH_MEMORY;
            case IDCardReader.DEVICE_NOT_FOUND:
                return MSG_DEVICE_NOT_FOUND;
            case IDCardReader.DEVICE_REOPEN:
                return MSG_DEVICE_REOPEN;
            case IDCardReader.NO_CARD:
                return MSG_NO_CARD;
            case IDCardReader.INVALID_CARD:
                return MSG_INVALID_CARD;
            case IDCardReader.INVALID_DECODE_LIB:
                return MSG_INVALID_DECODE_LIB;
            default:
                return "";
        }
    }
    /* *****************************读卡器操作状态信息*************************/

    public static final String Action_Module_Init = "com.xs.attendance.Action_module_init"; // 比对模型初始化
    public static final String Action_Init_MSg = "com.xs.attendance.Action_init_msg"; // 比对模型初始化

    // 监控相机状态
    public static final String Action_Camera_Status = "com.xs.attendance.Action_Camera_Status";
    public static final String Action_Camera_Preview = "com.xs.attendance.Action_Camera_Preview";

    public static final String Action_READ = "com.xs.attendance.Action_Read";
    public static final String Action_MSG = "com.xs.attendance.Action_Msg";
    public static final String Action_READ_SUC = "com.xs.attendance.Action_read_succeed";
    public static final String Action_RESTART_READ = "com.xs.attendance.Action_restart_read";

    public static final int OPEN_SUC = 0x9999; // 身份证读卡器打开成功
    public static final int PREVIEW_START = 0x9998; // 相机开始预览

    public static final int REQUEST_PHOTO_CUT = 0x3; // 剪裁
    public static final int REQUEST_PHOTO_TAKE = 0x1;// 拍照
    public static final int REQUEST_PHOTO_GALLERY = 0x2;// 从相册中选择

    public static final int C_THRESHOLD_SUC = 1;  //比对成功标志

    public static boolean timeReadFlag = false;
    public static boolean countdownFlag = false;  // 倒计时改变标志
    public static boolean thresholdFlag = false;  // 比对阈值改变标志

    /* 闪光灯控制****/
    public static final String FLASH_LIGHT_FILENAME = "/sys/class/rk29-keypad/gpio2";
    public static final String FLASH_LIGHT_OPEN = "1";
    public static final String FLASH_LIGHT_CLOSE = "0";
}
