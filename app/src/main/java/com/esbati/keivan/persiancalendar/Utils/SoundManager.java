package com.esbati.keivan.persiancalendar.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.R;

/**
 * Created by Keivan Esbati on 4/8/2017.
 */

public class SoundManager {

    private static SoundManager mSoundManager;
    private SoundPool mSoundPool;
    private int[] mTracks;
    private final int trackCounts = 8;

    SoundManager(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSoundPool = new SoundPool.Builder().setMaxStreams(4).build();
        else
            mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 5);

        loadSounds();
    }

    public static synchronized SoundManager getInstance(){
        if(mSoundManager == null)
            mSoundManager = new SoundManager();

        return mSoundManager;
    }

    private void loadSounds(){
        Context context = ApplicationController.getContext();
        mTracks = new int[trackCounts];
        mTracks[0] = mSoundPool.load(context, R.raw.asia_1, 1);
        mTracks[1] = mSoundPool.load(context, R.raw.asia_2, 1);
        mTracks[2] = mSoundPool.load(context, R.raw.asia_3, 1);
        mTracks[3] = mSoundPool.load(context, R.raw.asia_4, 1);
        mTracks[4] = mSoundPool.load(context, R.raw.asia_5, 1);
        mTracks[5] = mSoundPool.load(context, R.raw.asia_6, 1);
        mTracks[6] = mSoundPool.load(context, R.raw.asia_7, 1);
        mTracks[7] = mSoundPool.load(context, R.raw.asia_8, 1);
    }

    public void playSound(int soundIndex){
        int multiplier = (soundIndex / trackCounts) + 1;
        float frequency = 2.0F / (float)Math.pow(2, multiplier - 1);
        float volume = 0.25F * multiplier;

        mSoundPool.play(mTracks[soundIndex % trackCounts], volume, volume, 0, 0, frequency);

        //Log.e("Frequency" , "" + frequency);
        //Log.e("Volume" , "" + volume);
    }
}
