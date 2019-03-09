package com.esbati.keivan.persiancalendar.components

import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import com.esbati.keivan.persiancalendar.R

object SoundManager {

    private val soundPool by lazy {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            SoundPool.Builder().setMaxStreams(4).build()
        else
            SoundPool(4, AudioManager.STREAM_MUSIC, 5)
    }
    private val tracks: IntArray
    private const val TRACKS_SIZE = 8

    init {
        val context = ApplicationController.getContext()
        tracks = IntArray(TRACKS_SIZE)
        tracks[0] = soundPool.load(context, R.raw.asia_1, 1)
        tracks[1] = soundPool.load(context, R.raw.asia_2, 1)
        tracks[2] = soundPool.load(context, R.raw.asia_3, 1)
        tracks[3] = soundPool.load(context, R.raw.asia_4, 1)
        tracks[4] = soundPool.load(context, R.raw.asia_5, 1)
        tracks[5] = soundPool.load(context, R.raw.asia_6, 1)
        tracks[6] = soundPool.load(context, R.raw.asia_7, 1)
        tracks[7] = soundPool.load(context, R.raw.asia_8, 1)
    }

    //An Empty call to initialize object eagerly
    fun init(){
        Log.d("SoundManager", "Initialized!")
    }

    fun playSound(soundIndex: Int){
        val multiplier = soundIndex / TRACKS_SIZE + 1
        val frequency = 2.0f / Math.pow(2.0, (multiplier - 1).toDouble()).toFloat()
        val volume = 0.25f * multiplier

        soundPool.play(tracks[soundIndex % TRACKS_SIZE], volume, volume, 0, 0, frequency)
    }
}