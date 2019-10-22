package com.baidu.eduai.sdk.touping;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.cui.card.CuiBaseFragmentActivity;
import com.baidu.eduai.plugin_educloud_touping.R;

/**
 * Created by tianhouchao on 2019/6/10.
 */
public class ForScreenActivity extends CuiBaseFragmentActivity implements ForScreenConstract.View, View.OnClickListener {


    private ForScreenPresenter mPresenter;
    private TextView tvForscreenCode;
    private ImageView ivCloseBtn;
    private Button btnRetryConnectNetwork;
    private TextView tvNetworkTips;
    private ProgressBar pbNetworkLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_screen);
        initView();
        mPresenter = new ForScreenPresenter(this, this);
        mPresenter.handleService();
        mPresenter.handleIp();
    }

    private void initView() {
        ivCloseBtn = (ImageView) findViewById(R.id.iv_eduai_close_forscreenpage);
        tvNetworkTips = (TextView) findViewById(R.id.tv_eduai_network_tips);
        btnRetryConnectNetwork = (Button) findViewById(R.id.btn_eduai_retry_btn);
        tvForscreenCode = (TextView) findViewById(R.id.tv_eduai_forscreen_code);
        pbNetworkLoading = (ProgressBar) findViewById(R.id.pb_eduai_checknetwork_loading);

        ivCloseBtn.setOnClickListener(this);
        btnRetryConnectNetwork.setOnClickListener(this);
    }

    public void check(View view) {
        mPresenter.checkNetworkState();
    }

    @Override
    public void updateIpValue(String value) {
        tvForscreenCode.setText(value);
    }

    @Override
    public void updateRealIpValue(String value) {
        TextView tvIp = (TextView) findViewById(R.id.tv_temp_complete_ip);
        tvIp.setText(value);
    }

    @Override
    public void updateNetworkChanged(boolean wifiIsConnected) {
        if (wifiIsConnected) {
            btnRetryConnectNetwork.setVisibility(View.INVISIBLE);
            tvNetworkTips.setVisibility(View.INVISIBLE);
            tvForscreenCode.setVisibility(View.VISIBLE);
        } else {
            btnRetryConnectNetwork.setVisibility(View.VISIBLE);
            tvNetworkTips.setVisibility(View.VISIBLE);
            tvForscreenCode.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showCheckNetworkLoading() {
        pbNetworkLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCheckNetworkLoading() {
        pbNetworkLoading.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbNetworkLoading.setVisibility(View.GONE);
            }
        },500);
    }

    @Override
    public void closePage() {
        runOnUiThread(() -> ForScreenActivity.this.finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onViewDestory();
    }


    @Override
    public void onClick(View v) {
        if (v == ivCloseBtn) {
            closePage();
        } else if (v == btnRetryConnectNetwork) {
            mPresenter.checkNetworkState();
        }
    }
}
