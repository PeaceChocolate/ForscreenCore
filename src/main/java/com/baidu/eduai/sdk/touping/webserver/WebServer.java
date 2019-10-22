package com.baidu.eduai.sdk.touping.webserver;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by tianhouchao on 2019/6/3.
 */

public class WebServer extends NanoHTTPD {
    public AssetManager asset_mgr;
    private static final String INDEX = "forscreenindex.html";


    public WebServer() {
        // http://192.168.232.2:8088/index.html
        // 端口是8088，也就是说要通过http://127.0.0.1:8088来访当问
        super(8088);
    }

    public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {
        int len = 0;
        byte[] buffer = null;

        // 默认传入的url是以“/”开头的，需要删除掉，否则就变成了绝对路径
        String file_name = uri.substring(1);
        if (INDEX.equals(file_name)) {
            // 默认的页面名称设定为index.html
            if (file_name.equalsIgnoreCase("")) {
                file_name = INDEX;
            }
            try {
                //通过AssetManager直接打开文件进行读取操作
                InputStream in = asset_mgr.open(file_name, AssetManager.ACCESS_BUFFER);
                //假设单个网页文件大小的上限是1MB
                buffer = new byte[1024 * 1024];
                int temp = 0;
                while ((temp = in.read()) != -1) {
                    buffer[len] = (byte) temp;
                    len++;
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new NanoHTTPD.Response(new String(buffer, 0, len));
        }
        return new NanoHTTPD.Response("Wellcome 小度在校");
    }

}