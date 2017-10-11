package com.cdkj.melib.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nick on 2017/10/11.
 * 二维码对象，去重时使用
 */

public class QrCode implements Parcelable {
	public static final Parcelable.Creator<QrCode> CREATOR = new Parcelable.Creator<QrCode> () {
		@Override
		public QrCode createFromParcel (Parcel source) {
			return new QrCode (source);
		}

		@Override
		public QrCode[] newArray (int size) {
			return new QrCode[size];
		}
	};
	private final String value;
	private final long timestamp;

	public QrCode (String value, long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}

	protected QrCode (Parcel in) {
		this.value = in.readString ();
		this.timestamp = in.readLong ();
	}

	public String getValue () {
		return value;
	}

	public long getTimestamp () {
		return timestamp;
	}

	@Override
	public String toString () {
		return "QrCode{" +
				"value='" + value + '\'' +
				", timestamp=" + timestamp +
				'}';
	}

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel dest, int flags) {
		dest.writeString (this.value);
		dest.writeLong (this.timestamp);
	}
}
