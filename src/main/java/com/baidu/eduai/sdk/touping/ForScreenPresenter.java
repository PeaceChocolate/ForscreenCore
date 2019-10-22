package com.baidu.eduai.sdk.touping;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.baidu.eduai.sdk.touping.data.ForScreenConstants;
import com.baidu.eduai.sdk.touping.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by tianhouchao on 2019/6/13.
 */

public class ForScreenPresenter implements ForScreenConstract.Presenter {

    private Context mContext;
    private ForScreenConstract.View mViewProxy;
    private LocalBroadcastReceiver localBroadcastReceiver;
    private NetWorkChangReceiver netWorkChangReceiver;

    public ForScreenPresenter(Context context, ForScreenConstract.View view) {
        this.mContext = context;
        this.mViewProxy = view;
        registClosePageCallback();
        registNetworkListener();
        checkNetworkState();
    }

    private void registNetworkListener() {
        //注册网络状态监听广播
        netWorkChangReceiver = new NetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(netWorkChangReceiver, filter);
    }

    @Override
    public void handleService() {
        killRemoteServer();
        startRemoteServer();
    }

    public void registClosePageCallback() {
        localBroadcastReceiver = new LocalBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(ForScreenConstants.Action.ACTION_OPEN_FOR_SCREEN_BROADCAST);
        mContext.registerReceiver(localBroadcastReceiver, intentFilter);
    }

    @Override
    public void onViewDestory() {
        mContext.unregisterReceiver(localBroadcastReceiver);
        mContext.unregisterReceiver(netWorkChangReceiver);
    }

    @Override
    public void handleIp() {
        String localIpStr = NetworkUtils.getLocalIpStr(mContext);
        String[] split = localIpStr.split("\\.");
        if (split.length == 4) {
            mViewProxy.updateIpValue(resetIp(Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3])));
            mViewProxy.updateRealIpValue(localIpStr + ":8088");
        }
    }

    public void checkNetworkState() {
        mViewProxy.showCheckNetworkLoading();
        mViewProxy.updateNetworkChanged(NetworkUtils.isWifiConnected(mContext.getApplicationContext()));
        mViewProxy.hideCheckNetworkLoading();
    }

    /**
     * 进制转化
     */
    private String resetIp(int a, int b, int c, int d) {
        String value = addZeroInFront(d) + addZeroInFront(c) + addZeroInFront(b) + addZeroInFront(a);
        long ip = Long.parseLong(value, 2);
        String result = getFileAddSpace(ip + "");
        return result;
    }

    /**
     * 每4位添加空格
     */
    private String getFileAddSpace(String replace) {
        String regex = "(.{4})";
        replace = replace.replaceAll(regex, "$1 ");
        return replace;
    }

    /**
     * 8位补0
     */
    private String addZeroInFront(int result) {
        String value = String.format("%08d", Integer.parseInt(Integer.toBinaryString(result)));
        return value;
    }

    private void killRemoteServer() {
        killProcess("com.baidu.eduai.sdk.forscreen.remote");
    }

    private void startRemoteServer() {
        Intent intent = new Intent(mContext, ForScreenService.class);
        mContext.startService(intent);
    }


    public void killProcess(String killName) {
        // 获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取系统中所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager
                .getRunningAppProcesses();
        // 对系统中所有正在运行的进程进行迭代，如果进程名所要杀死的进程，则Kill掉
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            String processName = appProcessInfo.processName;
            if (processName.equals(killName)) {
                killProcessByPid(appProcessInfo.pid);
            }
        }
    }

    /**
     * 根据要杀死的进程id执行Shell命令已达到杀死特定进程的效果
     *
     * @param pid
     */
    private void killProcessByPid(int pid) {
        String command = "kill -9 " + pid + "\n";
        Runtime runtime = Runtime.getRuntime();
        Process proc;
        try {
            proc = runtime.exec(command);
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    public class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mViewProxy.closePage();
        }
    }

    public class NetWorkChangReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI ||
                                info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            mViewProxy.updateNetworkChanged(true);
                            mViewProxy.hideCheckNetworkLoading();
                        }
                    } else {
                        mViewProxy.updateNetworkChanged(false);
                        mViewProxy.hideCheckNetworkLoading();
                    }
                }
            }

        }
    }

}
