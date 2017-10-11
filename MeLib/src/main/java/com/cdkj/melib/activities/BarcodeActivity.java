package com.cdkj.melib.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.cdkj.melib.R;
import com.cdkj.melib.device.ConnectionCallback;
import com.cdkj.melib.device.N900Manager;
import com.cdkj.melib.model.QrCode;
import com.cdkj.melib.model.QrCodeStats;
import com.cdkj.melib.utils.SoundPoolImpl;
import com.newland.mtype.module.common.scanner.BarcodeScanner;
import com.newland.mtype.module.common.scanner.ScannerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

public class BarcodeActivity extends AppCompatActivity {
	public static final String QR_CODES = "qrCodes";
	private static final String TAG = "BarcodeActivity";
	private N900Manager mN900Manager;
	private SurfaceView mSurfaceView;
	private BarcodeScanner mScanner;
	private boolean scanFinish = false;
	private final Intent barcodeIntent = new Intent ();
	private ArrayList<QrCode> qrCodes = new ArrayList<> ();
	private ArrayList<String> qrCodeValues = new ArrayList<> ();
	private PublishSubject<QrCode> publishSubject;
	private final ConnectionCallback callback = new ConnectionCallback () {
		@Override
		public void onConnecting () {
		}

		@Override
		public void onConnected () {
			startScanBarcode ();
		}

		@Override
		public void onDisconnected () {
		}

		@Override
		public void onError (String error) {
			Toast.makeText (BarcodeActivity.this, error, Toast.LENGTH_SHORT).show ();
			setResult (RESULT_CANCELED);
			finish ();
		}
	};

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_barcode);
		mSurfaceView = (SurfaceView) findViewById (R.id.surfaceView);
		initObservable ();
		this.mN900Manager = new N900Manager (this, callback);
		mN900Manager.connect ();
		SoundPoolImpl.getInstance ().load (this);
		findViewById (R.id.button).setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View view) {
				if (mScanner != null) {
					mScanner.stopScan ();
				}
			}
		});
	}

	private void startScanBarcode () {
		try {
			qrCodes = new ArrayList<> ();
			qrCodeValues = new ArrayList<> ();
			mScanner = mN900Manager.getBarcodeScanner ();
			mScanner.initScanner (this, mSurfaceView, 0x00);
			mScanner.startScan (60, TimeUnit.SECONDS, new ScannerListener () {
				@Override
				public void onResponse (final String[] barcode) {
					publishSubject.onNext (new QrCode (barcode[0], System.currentTimeMillis ()));
				}

				@Override
				public void onFinish () {
					Log.i (TAG, "二维码扫描结束.");
					runOnUiThread (new Runnable () {
						@Override
						public void run () {
							Toast.makeText (BarcodeActivity.this, "扫描结束", Toast.LENGTH_LONG).show ();
							final String[] qrCodeArray = qrCodeValues.toArray (new String[]{});
							Log.d (TAG, "---------------------------------" + Arrays.toString (qrCodeArray));
							Observable.fromArray (qrCodeArray).map (new Function<String, QrCodeStats> () {
								@Override
								public QrCodeStats apply (@NonNull String s) throws Exception {
									int count = 0;
									for (String qrCodeValue : qrCodeArray) {
										count = qrCodeValue.equals (s) ? count + 1 : count;
									}
									return new QrCodeStats (s, count);
								}
							}).distinct (new Function<QrCodeStats, String> () {
								@Override
								public String apply (@NonNull QrCodeStats qrCodeStats) throws Exception {
									return qrCodeStats.getQrcode ();
								}
							}).toList ().subscribe (new Consumer<List<QrCodeStats>> () {
								@Override
								public void accept (List<QrCodeStats> qrCodeStatses) throws Exception {
									barcodeIntent.putParcelableArrayListExtra (QR_CODES, new ArrayList<Parcelable> (qrCodeStatses));
									setResult (RESULT_OK, barcodeIntent);
									finish ();
								}
							});
						}
					});
				}
			}, false);
		} catch (Exception e) {
			Log.e (TAG, "二维码扫描出错.", e);
			setResult (RESULT_CANCELED);
			finish ();
		}
	}

	private void initObservable () {
		publishSubject = PublishSubject.create ();
		DisposableObserver<QrCode> disposableObserver = new DisposableObserver<QrCode> () {
			@Override
			public void onNext (@NonNull QrCode qrcode) {
				qrCodes.add (qrcode);
				qrCodeValues.add (qrcode.getValue ());
			}

			@Override
			public void onError (@NonNull Throwable e) {
			}

			@Override
			public void onComplete () {
			}
		};
		publishSubject.filter (new Predicate<QrCode> () {
			@Override
			public boolean test (@NonNull QrCode qrCode) throws Exception {
				QrCode lastQrCode = qrCodes.isEmpty () ? null : qrCodes.get (qrCodes.size () - 1);
				if (lastQrCode == null) {
					SoundPoolImpl.getInstance ().play ();
					return true;
				} else {
					if (!lastQrCode.getValue ().equals (qrCode.getValue ())) {
						SoundPoolImpl.getInstance ().play ();
						return true;
					} else {
						long timeout = qrCode.getTimestamp () - lastQrCode.getTimestamp ();
						if (timeout > 1000L) {
							SoundPoolImpl.getInstance ().play ();
						}
						return timeout > 2000L;
					}
				}
			}
		}).subscribe (disposableObserver);
	}

	@Override
	protected void onResume () {
		super.onResume ();
		if (scanFinish) {
			startScanBarcode ();
		}
	}

	@Override
	public void onPause () {
		scanFinish = true;
		if (mScanner != null) {
			mScanner.stopScan ();
		}
		super.onPause ();
	}

	@Override
	protected void onDestroy () {
		mN900Manager.disconnect ();
		super.onDestroy ();
	}
}
