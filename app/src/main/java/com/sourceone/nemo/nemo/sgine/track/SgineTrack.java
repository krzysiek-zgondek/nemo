package com.sourceone.nemo.nemo.sgine.track;

import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.sourceone.nemo.nemo.sgine.SgineSample;
import com.sourceone.nemo.nemo.sgine.decoder.SgineDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.UUID;

import hugo.weaving.DebugLog;

/**
 * Created by SourceOne on 12.09.2016.
 */

public class SgineTrack {
    private SgineSample sample;

    private float gain = 1.0f;
    private long playing = -1;

    public static SgineTrack create(){ return new SgineTrack(); }

    private SgineTrack() {}

    private static SgineSample createSample(ByteBuffer data, MediaFormat format) {
        int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        return new SgineSample(data, sampleRate, channels, UUID.randomUUID());
    }

    private static void printSampleData(SgineSample sample) {
        Log.d(SgineTrack.class.getSimpleName(), "SAMPLE CH NUM: " + sample.getSampleChannels());
        Log.d(SgineTrack.class.getSimpleName(), "SAMPLE SM RATE: " + sample.getSampleRate());
        Log.d(SgineTrack.class.getSimpleName(), "SAMPLE LENGTH: " + sample.getSampleTime()+ "ms");
    }

    public SgineTrack load(String filename, float gain) {
        return load(filename, gain, null);
    }

    public SgineTrack load(String filename, float gain, AssetManager manager) {
        try {
            if(manager == null)
                SgineDecoder.decode(filename, createDecoderListener());
            else SgineDecoder.decode(manager.openFd(filename), createDecoderListener());
            this.gain = gain;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    private SgineDecoder.OnDecodeCompleted createDecoderListener() {
        return new SgineDecoder.OnDecodeCompleted() {
            @Override
            public void onDecodeCompleted(ByteBuffer data, MediaFormat format) {
                sample = createSample(data, format);
                printSampleData(sample);
            }
        };
    }

    public SgineSample getSample() {
        return sample;
    }

    public long getPlayEventTime() {
        return playing;
    }

    public void play() {
        playing = System.currentTimeMillis();
    }

    public void stop() {
        playing = -1;
    }

    public float getGain() {
        return gain;
    }
}
