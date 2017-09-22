package com.cdkj.melib.device;

/**
 * Created by Administrator on 2017/9/20.
 */

public interface ConnectionCallback {
    void onConnecting();
    void onConnected();
    void onDisconnected();
    void onError(String error);
}
