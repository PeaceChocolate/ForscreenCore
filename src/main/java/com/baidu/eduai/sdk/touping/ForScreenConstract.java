package com.baidu.eduai.sdk.touping;

/**
 * Created by tianhouchao on 2019/6/13.
 */

public class ForScreenConstract {
    interface Presenter {
        void handleService();

        void onViewDestory();

        void handleIp();

    }

    interface View {
        void updateIpValue(String value);

        void updateRealIpValue(String value);

        void updateNetworkChanged(boolean wifiIsConnected);

        void showCheckNetworkLoading();

        void hideCheckNetworkLoading();

        void closePage();
    }
}
