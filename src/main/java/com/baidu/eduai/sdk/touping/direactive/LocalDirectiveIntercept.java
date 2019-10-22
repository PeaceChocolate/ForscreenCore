package com.baidu.eduai.sdk.touping.direactive;

import android.text.TextUtils;

import com.baidu.eduai.sdk.touping.utils.Logger;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tianhouchao on 2019/6/4.
 */
public class LocalDirectiveIntercept {
    private static ArrayList<String> sInterceptList = new ArrayList<String>();

    static {
        sInterceptList.add("投屏");
        sInterceptList.add("我要投屏");
        sInterceptList.add("打开投屏");
        sInterceptList.add("开始投屏");
    }

    public static boolean interceptStartCodeOnlineDirective(String directiveContent) {
        Logger.e("---->directiveContent:" + directiveContent);
        // {"text":"放一首皇后乐队的歌","type":"FINAL"}
        if (TextUtils.isEmpty(directiveContent)) {
            return false;
        }
        if (!directiveContent.contains("FINAL")) {
            return false;
        }
        JSONObject jsonObject = null;
        String dirValue = "";
//        String type = "";
        try {
            jsonObject = new JSONObject(directiveContent);
            dirValue = jsonObject.optString("text");
//            type = jsonObject.optString("type");
            if (TextUtils.isEmpty(dirValue)) {
                return false;
            }
        } catch (Exception e) {
            Logger.e("---->>解析指令获取最终指令报错" + e);
            return false;
        }

        for (int i = 0; i < 4; i++) {
            String directive = sInterceptList.get(i);
            Logger.e("----->>列表定义指令 ：" + directive);
            if (i == 0) {
                Logger.e("----->>equals dirValue ：" + dirValue);
                if (directive.equals(dirValue)) {
                    return true;
                }
            } else {
                if (dirValue.contains(directive)) {
                    Logger.e("----->>contains dirValue ：" + dirValue);
                    return true;
                }
            }
        }

        return false;
    }
}
