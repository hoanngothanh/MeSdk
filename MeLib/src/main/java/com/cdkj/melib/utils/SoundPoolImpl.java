package com.cdkj.melib.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.cdkj.melib.R;


/**
 * 声音管理器
 * Created by Nick on 2017/9/21.
 */

public class SoundPoolImpl {
	private static SoundPoolImpl INSTANCE;

	private SoundPool soundPool;


	public static SoundPoolImpl getInstance () {
		if (INSTANCE == null) {
			INSTANCE = new SoundPoolImpl ();
		}
		return INSTANCE;
	}

	public void load (Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			soundPool = new SoundPool (3, AudioManager.STREAM_SYSTEM, 5);
		} else {
			AudioAttributes audioAttributes = new AudioAttributes.Builder ()
					.setLegacyStreamType (AudioManager.STREAM_SYSTEM)
					.setContentType (AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.setUsage (AudioAttributes.USAGE_NOTIFICATION_EVENT)
					.setFlags (AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
					.build ();
			soundPool = new SoundPool.Builder ()
					.setMaxStreams (3)
					.setAudioAttributes (audioAttributes)
					.build ();
		}
		soundPool.load (context.getApplicationContext (), R.raw.click1, 1);
	}

	public void play () {
		soundPool.play (1, 0, 1, 1, 0, 1);
	}
}
