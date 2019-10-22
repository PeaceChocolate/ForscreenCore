package com.baidu.eduai.sdk.touping.screencapturefeature;

/**
 * Created by tianhouchao on 2019/5/22.
 */

public interface ScreenCaptureContract {

    interface Presenter {

        void startScreenCapture();

        void stopScreenCapture();

        void registConnectListener(IConnectListener iConnectListener);
    }

}
