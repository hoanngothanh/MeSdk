package com.cdkj.melib.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cdkj.melib.R;
import com.cdkj.melib.device.ConnectionCallback;
import com.cdkj.melib.device.N900Manager;
import com.newland.mtype.module.common.printer.PrintContext;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.printer.PrinterResult;
import com.newland.mtype.module.common.printer.PrinterStatus;

import java.util.concurrent.TimeUnit;

public class PrinterActivity extends Activity {
	private static final String TAG = "PrinterActivity";
	private static final String ARG_SCRIPT = "script";
	private N900Manager mN900Manager;
	private Printer printer;
	private String script;
	private final ConnectionCallback callback = new ConnectionCallback () {
		@Override
		public void onConnecting () {
			//Toast.makeText (PrinterActivity.this, R.string.connecting, Toast.LENGTH_SHORT).show ();
		}

		@Override
		public void onConnected () {
			//Toast.makeText (PrinterActivity.this, R.string.connected, Toast.LENGTH_SHORT).show ();
			printer = mN900Manager.getPrinterModule ();
			printScript (script);
		}

		@Override
		public void onDisconnected () {
			//Toast.makeText (PrinterActivity.this, R.string.disconnected, Toast.LENGTH_SHORT).show ();
		}

		@Override
		public void onError (String error) {
			Toast.makeText (PrinterActivity.this, error, Toast.LENGTH_LONG).show ();
			finish ();
		}
	};

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_printer);
		script = getIntent ().getStringExtra (ARG_SCRIPT);
		if (script == null || "".equals (script.trim ())) {
			Toast.makeText (PrinterActivity.this, "打印数据不能为空.", Toast.LENGTH_LONG).show ();
			finish ();
			return;
		}
		this.mN900Manager = new N900Manager (this, callback);
		mN900Manager.connect ();
	}

	private void printScript (final String script) {
		new Thread (new Runnable () {
			@Override
			public void run () {
				// 初始化打印机模块
				printer.init ();
				if (printer.getStatus () != PrinterStatus.NORMAL) {
					if (printer.getStatus () == PrinterStatus.OUTOF_PAPER) {
						Log.w (TAG, "-------------------------打印机缺纸.");
						showAlert ("您的打印纸不够了, 请重新放入打印纸后再试 !");
						return;
					}
					Log.w (TAG, "-------------------------打印失败！打印机状态不正常");
					showAlert ("打印失败! 打印机状态不正常, 是否重试 ?");
				} else {
					try {
						Log.i (TAG, "-------------------------打印脚本:" + script);
						PrinterResult printScript = printer.printByScript (PrintContext.defaultContext (), script.getBytes ("gbk"), 60, TimeUnit.SECONDS);
						Log.i (TAG, "-------------------------打印结果:" + printScript);
						finish ();
					} catch (Exception e) {
						Log.e (TAG, "-------------------------打印脚本失败.", e);
						showAlert ("打印失败! 是否重试 ?");
					}
				}
			}
		}).start ();
	}

	private void showAlert (final String message) {
		runOnUiThread (new Runnable () {
			@Override
			public void run () {
				new AlertDialog.Builder (PrinterActivity.this)
						.setTitle ("单据打印")
						.setMessage (message)
						.setCancelable (false)
						.setPositiveButton ("重试打印", new DialogInterface.OnClickListener () {
							@Override
							public void onClick (DialogInterface dialogInterface, int i) {
								printScript (script);
							}
						})
						.setNegativeButton ("取消打印", new DialogInterface.OnClickListener () {
							@Override
							public void onClick (DialogInterface dialogInterface, int i) {
								finish ();
							}
						})
						.show ();
			}
		});
	}

	@Override
	protected void onDestroy () {
		mN900Manager.disconnect ();
		super.onDestroy ();
	}
}
