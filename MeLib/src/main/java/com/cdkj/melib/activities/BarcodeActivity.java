package com.cdkj.melib.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cdkj.melib.R;
import com.cdkj.melib.device.ConnectionCallback;
import com.cdkj.melib.device.N900Manager;
import com.cdkj.melib.utils.SoundPoolImpl;
import com.cdkj.melib.views.ViewfinderView;
import com.newland.mtype.module.common.scanner.BarcodeScanner;
import com.newland.mtype.module.common.scanner.ScannerListener;

import java.util.concurrent.TimeUnit;

public class BarcodeActivity extends AppCompatActivity {
    private static final String TAG = "BarcodeActivity";
    private N900Manager mN900Manager;
    private LinearLayout progressLayout;
    private FrameLayout scannerLayout;
    private SurfaceView surfaceView;
    private ViewfinderView finderView;
    private BarcodeScanner mScanner;
    private boolean scanFinish = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_barcode);
        scannerLayout = (FrameLayout) findViewById(R.id.scanner);
        progressLayout = (LinearLayout) findViewById(R.id.progress);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        finderView = (ViewfinderView)findViewById(R.id.scanview);
        this.mN900Manager = new N900Manager(this, callback);
        mN900Manager.connect();
        SoundPoolImpl.getInstance().load(this);
    }


    private ConnectionCallback callback = new ConnectionCallback() {
        @Override
        public void onConnecting() {
            Toast.makeText(BarcodeActivity.this, R.string.connecting, Toast.LENGTH_SHORT).show();
            progressLayout.setVisibility(View.VISIBLE);
            scannerLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onConnected() {
            Toast.makeText(BarcodeActivity.this, R.string.connected, Toast.LENGTH_SHORT).show();
            progressLayout.setVisibility(View.INVISIBLE);
            scannerLayout.setVisibility(View.VISIBLE);
            startScanBarcode();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(BarcodeActivity.this, R.string.disconnected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(String error) {
            Toast.makeText(BarcodeActivity.this, error, Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    };

    public void startScanBarcode(){
        try {
            mScanner = mN900Manager.getBarcodeScanner();
            mScanner.initScanner(this, surfaceView, 0x00);
            mScanner.startScan(30, TimeUnit.SECONDS, new ScannerListener() {
                @Override
                public void onResponse(String[] barcode) {
                    Log.i(TAG, "二维码扫描结果: " + barcode[0]);
                    SoundPoolImpl.getInstance().play();
                    scanFinish = true;
                    Intent resultData = new Intent();
                    resultData.putExtra("barcode", barcode[0]);
                    setResult(RESULT_OK, resultData);
                    finish();
                }
                @Override
                public void onFinish() {
                    Log.i(TAG, "二维码扫描结束.");
                }
            }, true);
        } catch (Exception e){
            Log.e(TAG, "二维码扫描出错.", e);
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scanFinish){
            startScanBarcode();
        }
    }

    @Override
    public void onPause() {
        scanFinish = true;
        if (mScanner != null){
            mScanner.stopScan();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mN900Manager.disconnect();
        super.onDestroy();
    }
}
