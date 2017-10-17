package com.cdkj.mesdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cdkj.melib.activities.BarcodeActivity;
import com.cdkj.melib.model.QrCodeStats;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final int QR_SCANNER = 1;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
	}

	public void onButtonClick (View view) {
		Intent barcodeScannerIntent = new Intent (this, BarcodeActivity.class);
		startActivityForResult (barcodeScannerIntent, QR_SCANNER);

		//Intent intent = new Intent("android.intent.action.NEWLAND.PAYMENT");
		/*Intent intent = new Intent();
		intent.setClassName("com.boc.zjls", "com.boc.spos.bocpay.ui.activity.MainActivity");
        intent.putExtra("transType", 444);
        startActivityForResult(intent, 444);*/
		/*
		StringBuffer scriptBuffer = new StringBuffer ();
		scriptBuffer.append ("!hz l\n !asc l\n");// 设置标题字体为大号
		scriptBuffer.append ("!yspace 20\n !gray 7\n");// 设置行间距
		// ,取值【0,60】，默认6
		scriptBuffer.append ("*text c 销售清单 \n");
		scriptBuffer.append ("*line" + "\n");// 打印虚线
		scriptBuffer.append ("!hz n\n !asc n\n");// 设置内容字体为中号
		scriptBuffer.append ("!yspace 10\n !gray 5\n ");// 设置内容行间距
		scriptBuffer.append ("*text l 客户名称:" + "王梁" + "\n");
		scriptBuffer.append ("*text l 农保卡号:" + "6222021402021010677" + "\n");
		scriptBuffer.append ("*text l 销售单号:" + "XS201709290101" + "\n");
		scriptBuffer.append ("*text l 销售日期:" + "2017-09-29 11:45:22" + "\n");
		scriptBuffer.append ("*line" + "\n");// 打印虚线
		scriptBuffer.append ("!hz n\n !asc n\n");
		scriptBuffer.append ("!yspace 5\n !gray 6\n");
		scriptBuffer.append ("*text l 名称        单价    数量    金额\n");
		scriptBuffer.append ("*line" + "\n");// 打印虚线

		scriptBuffer.append ("!hz n\n !asc n\n");
		scriptBuffer.append ("!yspace 5\n !gray 6\n");
		scriptBuffer.append ("*text l 甲氰菊酯1测试测试测试测试测试    0.10    100    10.00\n");
		scriptBuffer.append ("*text l 甲氰菊酯2    0.10    100   10.00\n");
		scriptBuffer.append ("*text l 甲氰菊酯3    0.10    100   10.00\n");
		scriptBuffer.append ("*text l 甲氰菊酯4    0.10    100   10.00\n");
		scriptBuffer.append ("*text l 甲氰菊酯5    0.10    100   10.00\n");
		scriptBuffer.append ("*text l 甲氰菊酯6    0.10    100   10.00\n");
		scriptBuffer.append ("*text l 甲氰菊酯7    0.10    100   10.00\n");
		scriptBuffer.append ("*line" + "\n");// 打印虚线
		scriptBuffer.append ("*text l 总数量: 700\n");
		scriptBuffer.append ("*text l 原价: 70.00    应收: 70.00\n");
		scriptBuffer.append ("*text l 现金: 30.00    转账: 20.00\n");
		scriptBuffer.append ("*text l 补贴支付: 20.00  补贴余额: 20.00\n");
		scriptBuffer.append ("*line" + "\n");// 打印虚线
		scriptBuffer.append ("*qrcode l XS201709290101\n");
		scriptBuffer.append ("*text l 销售单位: 丽水市化肥零售商店\n");
		scriptBuffer.append ("*text l 服务热线: 0571-56077609\n");
		scriptBuffer.append ("*text l 备注: 溯源详情请扫上方二维码\n");
		scriptBuffer.append ("*line" + "\n");// 打印虚线
		Intent intent = new Intent (this, PrinterActivity.class);
		intent.putExtra ("script", scriptBuffer.toString ());
		startActivity (intent);*/
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult (requestCode, resultCode, data);
		switch (requestCode) {
			case QR_SCANNER:
				if (resultCode == RESULT_OK) {
					ArrayList<QrCodeStats> qrCodeStatses = data.getParcelableArrayListExtra (BarcodeActivity.QR_CODES);
					Log.d (TAG, "扫描二维码返回: " + new Gson ().toJson (qrCodeStatses));
				} else {
					Log.d (TAG, "取消扫描二维码");
				}
				break;
		}

	}
}
