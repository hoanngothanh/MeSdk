package com.cdkj.melib.device;

import com.newland.mtype.Device;
import com.newland.mtype.module.common.cardreader.CardReader;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.iccard.ICCardModule;
import com.newland.mtype.module.common.light.IndicatorLight;
import com.newland.mtype.module.common.pin.K21Pininput;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.rfcard.RFCardModule;
import com.newland.mtype.module.common.scanner.BarcodeScanner;
import com.newland.mtype.module.common.security.SecurityModule;
import com.newland.mtype.module.common.serialport.SerialModule;
import com.newland.mtype.module.common.storage.Storage;
import com.newland.mtype.module.common.swiper.K21Swiper;

/**
 * Created by Administrator on 2017/9/21.
 */

public abstract class AbstractManager {

    /**
     * 连接设备
     */
    public abstract void connect();

    /**
     * 断开连接
     */
    public abstract void disconnect();


    /**
     * 获取设备
     * @return Device
     */
    public abstract Device getCurrentDevice();

    /**
     * 设备是否连接
     * @return true: 已连接 false:未连接
     */
    public abstract boolean isAlive();

    /**
     * 获取读卡器模块
     * @return CardReader
     */
    public abstract CardReader getCardReaderModule();
    /**
     * 获取emv模块
     * @return EmvModule
     */
    public abstract EmvModule getEmvModule();
    /**
     * 获取ic卡模块
     * @return ICCardModule
     */
    public abstract ICCardModule getICCardModule();
    /**
     * 获取指示灯模块
     * @return IndicatorLight
     */
    public abstract IndicatorLight getIndicatorModule();

    /**
     * 密码输入模块
     * @return K21Pininput
     */
    public abstract K21Pininput getK21PinInputModule();
    /**
     * 打印模块
     * @return Printer
     */
    public abstract Printer getPrinterModule();
    /**
     * 非接卡模块
     * @return RFCardModule
     */
    public abstract RFCardModule getRFCardModule();
    /**
     * 扫描模块
     * @return BarcodeScanner
     */
    public abstract BarcodeScanner getBarcodeScanner();
    /**
     * 设备安全认证模块
     * @return SecurityModule
     */
    public abstract SecurityModule getSecurityModule();
    /**
     * 存储模块
     * @return Storage
     */
    public abstract Storage getStorageModule();
    /**
     * 刷卡模块
     * @return K21Swiper
     */
    public abstract K21Swiper getK21SwiperModule();

    /**
     * 串口通信
     * @return SerialModule
     */
    public abstract SerialModule getUsbSerialModule();
}
