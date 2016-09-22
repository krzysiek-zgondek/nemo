package com.sourceone.nemo.nemo.devices.sampler;

import android.media.MediaFormat;

import com.sourceone.nemo.nemo.devices.SamplerDevice;
import com.sourceone.nemo.nemo.sgine.decoder.SgineDecoder;

import java.nio.ByteBuffer;

/**
 * Created by SourceOne - Krzysztof Zgondek on 22.09.2016.
 */
public class OnSampleDecoded implements SgineDecoder.OnDecodeCompleted{
    private final SamplerDevice device;
    private final int track;
    private final float gain;

    public OnSampleDecoded(SamplerDevice device, int track, float gain) {
        this.device = device;
        this.track = track;
        this.gain = gain;
    }

    @Override
    public void onDecodeCompleted(ByteBuffer sample, MediaFormat format) {
        device.createTrack(track, sample, format, gain);
    }
}
