package com.cdkj.melib.device;

/**
 * 设备连接管理回调
 * Created by Nick on 2017/9/20.
 */

public interface ConnectionCallback {
	void onConnecting ();

	void onConnected ();

	void onDisconnected ();

	void onError (String error);
}
