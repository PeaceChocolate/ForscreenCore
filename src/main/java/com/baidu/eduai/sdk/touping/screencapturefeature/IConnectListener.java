package com.baidu.eduai.sdk.touping.screencapturefeature;

/**
 * Created by tianhouchao on 2019/6/10.
 */

public interface IConnectListener {
    void onConnectedSuccess();

    void onConnectedError();

    void onServiceClose();
}
