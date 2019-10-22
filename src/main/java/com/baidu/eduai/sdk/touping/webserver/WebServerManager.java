package com.baidu.eduai.sdk.touping.webserver;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by tianhouchao on 2019/6/4.
 */

public class WebServerManager {

    private static WebServerManager manager;
    private WebServer server;

    private WebServerManager() {
        server = new WebServer();
    }

    public static WebServerManager getWebServerManager() {
        if (manager == null) {
            synchronized (WebServerManager.class) {
                if (manager == null) {
                    manager = new WebServerManager();
                }
            }
        }
        return manager;
    }

    public void startWebServer(Context context) {
        try {
            // 因为程序模拟的是html放置在asset目录下，
            // 所以在这里存储一下AssetManager的指针。
            server.asset_mgr = context.getAssets();
            // 启动web服务
            server.start();
            Log.i("Httpd", "The server started.");
        } catch (IOException ioe) {
            Log.w("Httpd", "The server could not start.");
        }
    }

    public void stopWebServer() {
        if (server != null) {
            // 在程序退出时关闭web服务器
            server.stop();
        }
        Log.w("Httpd", "The server stopped.");
    }

}
