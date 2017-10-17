package com.cdkj.melib.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nick on 2017/10/11.
 * 二维码扫描去重统计对象
 */
public class QrCodeStats implements Parcelable{

	public static final Parcelable.Creator<QrCodeStats> CREATOR = new Parcelable.Creator<QrCodeStats> () {
		@Override
		public QrCodeStats createFromParcel (Parcel source) {
			return new QrCodeStats (source);
		}

		@Override
		public QrCodeStats[] newArray (int size) {
			return new QrCodeStats[size];
		}
	};
	private final String qrcode;
	private final int count;

	public QrCodeStats (String qrcode, int count) {
		this.qrcode = qrcode;
		this.count = count;
	}

	protected QrCodeStats (Parcel in) {
		this.qrcode = in.readString ();
		this.count = in.readInt ();
	}

	public String getQrcode () {
		return qrcode;
	}

	public int getCount () {
		return count;
	}

	@Override
	public String toString () {
		return "QrCodeStats{" +
				"qrcode='" + qrcode + '\'' +
				", count=" + count +
				'}';
	}

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel dest, int flags) {
		dest.writeString (this.qrcode);
		dest.writeInt (this.count);
	}
}
