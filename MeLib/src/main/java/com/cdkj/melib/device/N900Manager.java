package com.cdkj.melib.device;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.ExModuleType;
import com.newland.mtype.ModuleType;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.cardreader.CardReader;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.iccard.ICCardModule;
import com.newland.mtype.module.common.light.IndicatorLight;
import com.newland.mtype.module.common.pin.K21Pininput;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.rfcard.RFCardModule;
import com.newland.mtype.module.common.scanner.BarcodeScanner;
import com.newland.mtype.module.common.scanner.BarcodeScannerManager;
import com.newland.mtype.module.common.security.SecurityModule;
import com.newland.mtype.module.common.serialport.SerialModule;
import com.newland.mtype.module.common.storage.Storage;
import com.newland.mtype.module.common.swiper.K21Swiper;
import com.newland.mtypex.nseries.NSConnV100ConnParams;

/**
 * Created by Nick on 2017/9/21.
 */

public class N900Manager extends AbstractManager{
    private static final String TAG = "N900Manager";
    private Context context;
    private Handler mainHandler;
    private ConnectionCallback callback;
    private DeviceManager mDeviceManager;

    public N900Manager(Context context, ConnectionCallback callback){
        this.context = context;
        this.callback = callback;
        this.mainHandler = new Handler();
    }

    private DeviceEventListener<ConnectionCloseEvent> closeListener = new DeviceEventListener<ConnectionCloseEvent>() {
        boolean isClosedHasError = false;
        @Override
        public void onEvent(ConnectionCloseEvent closeEvent, Handler handler) {
            if (closeEvent.isSuccess()){
                Log.i(TAG, "onEvent: 设备连接正常关闭.");
                isClosedHasError = false;
            }
            if (closeEvent.isFailed()){
                Log.w(TAG, "onEvent: 设备连接异常关闭.");
                isClosedHasError = true;

            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isClosedHasError){
                        callback.onError("设备异常断开!");
                    }else{
                        callback.onDisconnected();
                    }
                }
            });
        }

        @Override
        public Handler getUIHandler() {
            return mainHandler;
        }
    };


    @Override
    public void connect() {
            callback.onConnecting();
            mDeviceManager = ConnUtils.getDeviceManager();
            NSConnV100ConnParams params = new NSConnV100ConnParams();
            mDeviceManager.init(context, AvailableDrivers.K21_DRIVER, params, closeListener);
            Log.i(TAG, "connect: 连接管理器初始化成功.");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mDeviceManager.connect();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "connect: 设备已连接.");
                                callback.onConnected();
                            }
                        });
                    } catch (Exception e){
                        Log.e(TAG, "连接异常,请检查设备或重新连接.", e);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("连接异常,请检查设备或重新连接.");
                            }
                        });
                    }
                }
            }).start();
    }

    @Override
    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mDeviceManager != null) {
                        mDeviceManager.disconnect();
                        mDeviceManager = null;
                        Log.i(TAG, "disconnect: 设备已正常断开.");
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDisconnected();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "disconnect: 设备断开异常.", e);
                    /*mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("设备断开异常.");
                        }
                    });*/
                }
            }
        }).start();
    }

    @Override
    public Device getCurrentDevice() {
        return mDeviceManager.getDevice();
    }

    @Override
    public boolean isAlive() {
        boolean ifConnected = ( mDeviceManager== null ? false : mDeviceManager.getDevice().isAlive());
        return ifConnected;
    }

    @Override
    public CardReader getCardReaderModule() {
        CardReader cardReader=(CardReader) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_CARDREADER);
        return cardReader;
    }

    @Override
    public EmvModule getEmvModule() {
        EmvModule emvModule=(EmvModule) mDeviceManager.getDevice().getExModule("EMV_INNERLEVEL2");
        return emvModule;
    }

    @Override
    public ICCardModule getICCardModule() {
        ICCardModule iCCardModule=(ICCardModule) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_ICCARDREADER);
        return iCCardModule;
    }

    @Override
    public IndicatorLight getIndicatorModule() {
        IndicatorLight indicatorLight=(IndicatorLight) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_INDICATOR_LIGHT);
        return indicatorLight;
    }

    @Override
    public K21Pininput getK21PinInputModule() {
        K21Pininput k21Pininput=(K21Pininput) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
        return k21Pininput;
    }

    @Override
    public Printer getPrinterModule() {
        Printer printer=(Printer) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_PRINTER);
        printer.init();
        return printer;
    }

    @Override
    public RFCardModule getRFCardModule() {
        RFCardModule rFCardModule=(RFCardModule) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_RFCARDREADER);
        return rFCardModule;
    }

    @Override
    public BarcodeScanner getBarcodeScanner() {
        BarcodeScannerManager barcodeScannerManager=(BarcodeScannerManager) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_BARCODESCANNER);
        BarcodeScanner scanner = barcodeScannerManager.getDefault();
        return scanner;
    }

    @Override
    public SecurityModule getSecurityModule() {
        SecurityModule securityModule=(SecurityModule) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_SECURITY);
        return securityModule;
    }

    @Override
    public Storage getStorageModule() {
        Storage storage=(Storage) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_STORAGE);
        return storage;
    }

    @Override
    public K21Swiper getK21SwiperModule() {
        K21Swiper k21Swiper=(K21Swiper) mDeviceManager.getDevice().getStandardModule(ModuleType.COMMON_SWIPER);
        return k21Swiper;
    }

    @Override
    public SerialModule getUsbSerialModule() {
        SerialModule k21Serial=(SerialModule) mDeviceManager.getDevice().getExModule(ExModuleType.USBSERIAL);
        return k21Serial;
    }
}
