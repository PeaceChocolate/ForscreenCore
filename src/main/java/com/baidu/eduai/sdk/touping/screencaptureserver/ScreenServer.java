package com.baidu.eduai.sdk.touping.screencaptureserver;

import com.baidu.eduai.sdk.touping.screencapturefeature.IConnectListener;
import com.baidu.eduai.sdk.touping.utils.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 在Android 设备起WebSocket Server 和 Web通信
 */
public class ScreenServer extends WebSocketServer {

    public String TAG = "ScreenServer";

    public WebSocket mClientSession = null;
    private List<IConnectListener> iConnectListeners;

    public ScreenServer(int port) {
        super(new InetSocketAddress(port));
        iConnectListeners = new ArrayList<>();
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake arg1) {
        if (mClientSession != null) {
            mClientSession.close();
        }
        mClientSession = conn;
        if (iConnectListeners.size() > 0) {
            for (IConnectListener iConnectListeners : iConnectListeners) {
                iConnectListeners.onConnectedSuccess();
            }
        }
    }

    public void pushScreenShow(InputStream inputStream) {
        if (mClientSession == null) {
            return;
        }
        try {
            byte[] content = new byte[inputStream.available()];
            inputStream.read(content);
            ByteBuffer byteBuffer = ByteBuffer.wrap(content);
            mClientSession.send(byteBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage(WebSocket conn, String arg1) {
        Logger.e("----------Server onMessage msg:" + arg1);
    }

    @Override
    public void onClose(WebSocket conn, int arg1, String arg2, boolean arg3) {
        Logger.e("----------Server onClose :" + arg1);
        if (iConnectListeners.size() > 0) {
            for (IConnectListener iConnectListeners : iConnectListeners) {
                iConnectListeners.onServiceClose();
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception arg1) {
        Logger.e("------onError-----------" + arg1);
        if (iConnectListeners.size() > 0) {
            for (IConnectListener iConnectListeners : iConnectListeners) {
                iConnectListeners.onConnectedError();
            }
        }
    }

    @Override
    public void onStart() {
        Logger.e("Server client onStart");
    }

    public void registConnectListener(IConnectListener listener) {
        iConnectListeners.add(listener);
    }
}
