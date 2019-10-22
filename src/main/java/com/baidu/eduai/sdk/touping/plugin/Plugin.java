package com.baidu.eduai.sdk.touping.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.baidu.cui.CuiContext;
import com.baidu.cui.CuiMgr;
import com.baidu.cui.dispatcher.eventobserver.DirectiveEventObserver;
import com.baidu.cui.dispatcher.eventobserver.ObserverHandleType;
import com.baidu.cui.dispatcher.message.CuiDirective;
import com.baidu.cui.plugin.CuiBasePlugin;
import com.baidu.eduai.sdk.touping.ForScreenActivity;
import com.baidu.eduai.sdk.touping.data.ForScreenConstants;
import com.baidu.eduai.sdk.touping.direactive.LocalDirectiveIntercept;
import com.baidu.eduai.sdk.touping.plugin.payload.OpenForScreenHomePayload;
import com.baidu.eduai.sdk.touping.utils.Logger;

import utils.StringUtil;

/**
 * Created by tianhouchao on 2019/6/4.
 */

public class Plugin extends CuiBasePlugin {
    private static final String TAG = "forscreen-plugin";
    /**
     * 投屏开关
     * true ： 投屏中
     * false ： 关闭中
     */
    private boolean forscreenSwitch;
    private ForscreenSwitchReceiver forscreenSwitchReceiver;
    private PluginNetWorkChangReceiver netWorkChangReceiver;

    public Plugin(CuiContext context, String pluginName) {
        super(context, pluginName);
    }

    @Override
    public void onCreate() {
        setPluginPriority(CuiBasePlugin.PLUGIN_PRIORITY_HIGH);
        registForscreenSwitchReceiver();
        registNetworkReceiver();
    }

    private void registNetworkReceiver() {
        // 注册网络状态监听广播
        netWorkChangReceiver = new PluginNetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mCuiContext.getContext().registerReceiver(netWorkChangReceiver, filter);
    }

    private void registForscreenSwitchReceiver() {
        forscreenSwitchReceiver = new ForscreenSwitchReceiver();
        IntentFilter filter = new IntentFilter(ForScreenConstants.Action.ACTION_FOR_SCREEN_SERVE_SWITCH_BROADCAST);
        mCuiContext.getContext().registerReceiver(forscreenSwitchReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Logger.e("-------forscreen plugin destory");
        mCuiContext.getContext().unregisterReceiver(forscreenSwitchReceiver);
        mCuiContext.getContext().unregisterReceiver(netWorkChangReceiver);
    }

    @Override
    public DirectiveEventObserver.DirectiveParseConfig onConfigParseDirectiveImpl() {
        DirectiveEventObserver.DirectiveParseConfig cfg = super.onConfigParseDirectiveImpl();
        // insert payload,让cui帮忙解析
        cfg.insertPayload(
                ForScreenConstants.Namespace.FORSCREEN,
                ForScreenConstants.Name.OPEN_FORSCREEN_TO_HOME,
                OpenForScreenHomePayload.class
        );
        return cfg;
    }

    @Override
    public boolean onGoBack() {
        return super.onGoBack();
    }

    @Override
    public boolean onGoHome() {
        return super.onGoHome();
    }

    @Override
    public ObserverHandleType onDirectiveImpl(CuiDirective directive) {
        if (directive == null) {
            return ObserverHandleType.PASS;
        }
        if (LocalDirectiveIntercept.interceptStartCodeOnlineDirective(directive.payloadJson)) {
            try {
                CuiMgr.get().getmCuiDispatcher().cancelVoiceRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startForScreenHome();
            return ObserverHandleType.HANDLE_INTECEPT;
        }
        ObserverHandleType ret = ObserverHandleType.PASS;
        if (!isNameSpaceValid(directive)) {
            return ret;
        }
        if (!isNameValid(directive)) {
            return ret;
        }
        return processDirective(directive);
    }

    @Override
    public ObserverHandleType onAsrFinalImpl(String finalResult) {
        return ObserverHandleType.PASS;
    }

    private boolean isNameSpaceValid(CuiDirective directive) {
        boolean result = false;
        String namespace = directive.header.getNamespace();
        result = result || StringUtil.equals(namespace, ForScreenConstants.Namespace.FORSCREEN);
        return result;
    }

    private boolean isNameValid(CuiDirective directive) {
        boolean result = false;
        String name = directive.header.getName();
        result = result || StringUtil.equals(name, ForScreenConstants.Name.OPEN_FORSCREEN_TO_HOME);
        return result;
    }

    private ObserverHandleType processDirective(CuiDirective directive) {
        String namespace = directive.getHeader().getNamespace();
        String name = directive.getHeader().getName();
        String rawMessage = directive.getRawMessage();
        switch (name) {
            case ForScreenConstants.Name.OPEN_FORSCREEN_TO_HOME: {
                startForScreenHome();
                return ObserverHandleType.HANDLE_INTECEPT;
            }
            default: {
                return ObserverHandleType.PASS;
            }
        }
    }

    private void startForScreenHome() {
        if (forscreenSwitch) {
            Toast.makeText(mCuiContext.getContext(), "正在投屏中", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(mCuiContext.getContext(), ForScreenActivity.class);
            mCuiContext.getContext().startActivity(intent);
        }
    }

    public class ForscreenSwitchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            forscreenSwitch = intent.getBooleanExtra(ForScreenConstants.Extras.EXTRAS_FORSCREEN_SWITCH, false);
            Logger.e("------收到投屏是否关闭的广播 ： " + forscreenSwitch);
        }
    }

    public class PluginNetWorkChangReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                // 获取联网状态的NetworkInfo对象
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    // 如果当前的网络连接成功并且网络连接可用
                    if (!(NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable())) {
                        forscreenSwitch = false;
                    }
                }
            }

        }
    }

}
