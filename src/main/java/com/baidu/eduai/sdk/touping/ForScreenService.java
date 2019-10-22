package com.baidu.eduai.sdk.touping;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baidu.eduai.sdk.touping.data.ForScreenConstants;
import com.baidu.eduai.sdk.touping.screencapturefeature.IConnectListener;
import com.baidu.eduai.sdk.touping.screencapturefeature.ScreenCaptuerPresenter;
import com.baidu.eduai.sdk.touping.utils.Logger;

/**
 * Created by tianhouchao on 2019/6/3.
 */
public class ForScreenService extends Service {

    private ScreenCaptuerPresenter mPresenter;

    public ForScreenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("----->>service oncreate");
        mPresenter = new ScreenCaptuerPresenter(this);
        mPresenter.registConnectListener(new IConnectListener() {
            @Override
            public void onConnectedSuccess() {
                Intent i = new Intent(ForScreenConstants.Action.ACTION_OPEN_FOR_SCREEN_BROADCAST);
                sendBroadcast(i);
                Logger.e("------发送打开投屏的广播");
                Intent forscreenSwitch = new Intent(ForScreenConstants.Action.ACTION_FOR_SCREEN_SERVE_SWITCH_BROADCAST);
                forscreenSwitch.putExtra(ForScreenConstants.Extras.EXTRAS_FORSCREEN_SWITCH, true);
                sendBroadcast(forscreenSwitch);
            }

            @Override
            public void onConnectedError() {
                Logger.e("------发送关闭投屏的广播");
                Intent i = new Intent(ForScreenConstants.Action.ACTION_FOR_SCREEN_SERVE_SWITCH_BROADCAST);
                i.putExtra(ForScreenConstants.Extras.EXTRAS_FORSCREEN_SWITCH, false);
                sendBroadcast(i);
            }

            @Override
            public void onServiceClose() {
                Logger.e("------发送关闭投屏的广播");
                Intent i = new Intent(ForScreenConstants.Action.ACTION_FOR_SCREEN_SERVE_SWITCH_BROADCAST);
                i.putExtra(ForScreenConstants.Extras.EXTRAS_FORSCREEN_SWITCH, false);
                sendBroadcast(i);
            }
        });
        mPresenter.startScreenCapture();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e("----->>service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("----->>service onDestory");
        mPresenter.stopScreenCapture();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
