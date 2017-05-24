package com.common.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.socks.library.KLog;

import java.util.HashMap;

/**
 * Created by JamesP949 on 2017/2/20.
 * Function:音效播放控制类
 */

public class SoundPoolUtil {
    private Context mContext;
    private SoundPool mSPool;
    public HashMap<Integer, Integer> musicId = new HashMap<>();

    public SoundPoolUtil(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder sb = new SoundPool.Builder();
            sb.setMaxStreams(5); //传入音频的数量
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM);//设置音频流的合适的属性
//            attrBuilder.setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE);
//            attrBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION); // 设置音频的功能类型
            sb.setAudioAttributes(attrBuilder.build());//加载一个AudioAttributes
            mSPool = sb.build();
        } else {
            mSPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 100);
        }
    }

    public void loadSafeRes(int key, int rawRes) {
        musicId.put(key, mSPool.load(mContext, rawRes, 1));
        KLog.e("加载音效文件：" + musicId.get(key));
    }

    public void playSound(final int id) {
                int soundId = mSPool.play(id,
                        1, //左声道音量 0.0 to 1.0
                        1, // 右声道音量 0.0 to 1.0
                        0, // 优先级
                        0, // 循环播放次数 0 不循环 -1 无限循环
                        1 // 播放速率 0.5-2 1 正常速率
                );
                KLog.e("soundId：" + soundId);
    }


}
