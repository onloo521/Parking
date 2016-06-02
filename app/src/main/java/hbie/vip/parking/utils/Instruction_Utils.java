package hbie.vip.parking.utils;

import android.content.Context;
import android.content.Intent;

import hbie.vip.parking.service.AppService;

/**
 * Created by mac on 16/6/1.
 */

public class Instruction_Utils {
    /**
     * @param context
     * @param instruction
     * @exception
     */
    public static void sendInstrustion(Context context, Integer instruction) {
        Intent intent = new Intent(context, AppService.class);
        intent.putExtra("instruction", instruction);
        context.startService(intent);
    }
    public static void sendInstrustion(Context context, Integer instruction, String string) {
        Intent intent = new Intent(context, AppService.class);
        intent.putExtra("instruction", instruction);
        intent.putExtra("source", string);
        context.startService(intent);
    }

    /**
     * 1000~~~1999 IS other
     */
    public static final int UPLOAD_ADDRESSBOOK = 1000;// 上传通讯录指令
    public static final int SYNCHRONOUS_USER_DATA = 1001;// 用户数据的同步指令
    public static final int SYNCHRONOUS_USER_TAG = 1002;// 同步用户标签
    public static final int SYNCHRONOUS_USER_EXPERIENCE = 1003;// 同步用户工作经历
    public static final int CHECK_VERSION = 1004;// 检查版本指令
    public static final int SYNCHRONOUS_USER_CHANNAL = 1005;// 用户频道的同步指令
    public static final int SYNCHRONOUS_ALL_CHANNAL = 1006;// 所有频道的同步指令
    public static final int SYNCHRONOUS_ALL_CITY = 1007;// 城市的同步指令

}
