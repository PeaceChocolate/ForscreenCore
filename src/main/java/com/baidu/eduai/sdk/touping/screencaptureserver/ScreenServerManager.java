package com.baidu.eduai.sdk.touping.screencaptureserver;

import android.util.Log;

import com.baidu.eduai.sdk.touping.screencapturefeature.IConnectListener;

import java.io.InputStream;

/**
 * Created by tianhouchao on 2019/6/3.
 */

public class ScreenServerManager {

    private ScreenServer mServer = null;
    private static ScreenServerManager manager;

    private ScreenServerManager() {
        int port = 9010; //端口
        mServer = new ScreenServer(port);
        mServer.setReuseAddr(true);
    }

    public static ScreenServerManager getScreenServerManager() {
        if (manager == null) {
            synchronized (ScreenServerManager.class) {
                if (manager == null) {
                    manager = new ScreenServerManager();
                }
            }
        }
        return manager;
    }

    public void bindServer() {
        Log.e("socket","------>>开启server");
        mServer.start();
    }

    public void unBindServer() {
        if (mServer != null) {
            mServer = null;
        }
    }

    public void pushScreenShot(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        mServer.pushScreenShow(inputStream);
    }

    public void registConnectListener(IConnectListener listener) {
        if (listener == null) {
            return;
        }
        mServer.registConnectListener(listener);
    }

}
