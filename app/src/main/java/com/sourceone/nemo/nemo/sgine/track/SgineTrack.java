package com.sourceone.nemo.nemo.sgine.track;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaFormat;
import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;

import hugo.weaving.DebugLog;

/**
 * Created by SourceOne on 12.09.2016.
 */

public class SgineTrack {
    private AudioTrack audio;
    private SparseArray<Boolean> ticks = new SparseArray<>();

    public SgineTrack(ByteBuffer sample, MediaFormat format) {
        int channelNum = AudioFormat.CHANNEL_OUT_STEREO;
        if(format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) == 1)
            channelNum = AudioFormat.CHANNEL_OUT_MONO;

        Log.d(getClass().getName(), "CH NUM: " + format.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
        Log.d(getClass().getName(), "SM RATE: " + format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        audio = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                channelNum,
                AudioFormat.ENCODING_PCM_16BIT,
                sample.capacity(),
                AudioTrack.MODE_STATIC
        );

        audio.write(sample, sample.capacity(), AudioTrack.WRITE_BLOCKING);
    }

    public static SgineTrack create(ByteBuffer sample, MediaFormat format){
        return new SgineTrack(sample, format);
    }

    public static void play(SgineTrack track){
        if(track==null)
            return;

        track.play();
    }

    public void play(){
        switch (audio.getPlayState()) {
            case AudioTrack.PLAYSTATE_PAUSED:
                audio.reloadStaticData();
                audio.play();
                break;
            case AudioTrack.PLAYSTATE_PLAYING:
                audio.pause();
                audio.reloadStaticData();
                audio.play();
                break;
            case AudioTrack.PLAYSTATE_STOPPED:
                audio.reloadStaticData();
                audio.play();
                break;
        }
    }

    public static void stop(SgineTrack track) {
        if(track==null)
            return;

        track.stop();
    }

    public void stop(){
        audio.stop();
    }

    public void release() {
        if(audio!=null)
            audio.release();
    }

    public static void record(SgineTrack track, int current_tick) {
        if(track==null)
            return;

        track.record(current_tick);
    }

    @DebugLog
    private void record(int current_tick) {
        ticks.put(current_tick, true);
    }

    public static void tick(SgineTrack track, int current_tick) {
        if(track==null)
            return;
        track.tick(current_tick);
    }

    public void tick(int current_tick) {
        if(ticks.get(current_tick, false))
            play();
    }

    public void volume(float v) {
        audio.setVolume(v);
    }

}
