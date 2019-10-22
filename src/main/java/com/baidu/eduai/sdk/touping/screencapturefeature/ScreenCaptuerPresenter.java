package com.baidu.eduai.sdk.touping.screencapturefeature;

import android.content.Context;

import com.baidu.eduai.sdk.touping.screencaptureserver.ScreenServerManager;
import com.baidu.eduai.sdk.touping.webserver.WebServerManager;

import java.io.InputStream;

/**
 * Created by tianhouchao on 2019/6/3.
 */

public class ScreenCaptuerPresenter implements ScreenCaptureModel.OnPostScreenCaptureResult, ScreenCaptureContract.Presenter {

    private ScreenCaptureModel screenCaptureModel;
    private final ScreenServerManager screenServerManager;
    private final WebServerManager webServerManager;

    public ScreenCaptuerPresenter(Context context) {
        webServerManager = WebServerManager.getWebServerManager();
        webServerManager.startWebServer(context);
        screenServerManager = ScreenServerManager.getScreenServerManager();
        screenCaptureModel = new ScreenCaptureModel(context, this);
        screenServerManager.registConnectListener(screenCaptureModel);
    }

    @Override
    public void onPostResult(InputStream inputStream) {
        screenServerManager.pushScreenShot(inputStream);
    }

    @Override
    public void startScreenCapture() {
        screenCaptureModel.startScreenCapture();
        screenServerManager.bindServer();
    }

    @Override
    public void stopScreenCapture() {
        screenServerManager.unBindServer();
        screenCaptureModel.stopScreenCapture();
        webServerManager.stopWebServer();
    }

    @Override
    public void registConnectListener(IConnectListener iConnectListener) {
        screenServerManager.registConnectListener(iConnectListener);
    }
}
