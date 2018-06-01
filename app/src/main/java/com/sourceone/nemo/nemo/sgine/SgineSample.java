package com.sourceone.nemo.nemo.sgine;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by SourceOne - Krzysztof Zgondek on 23.09.2016.
 */

public class SgineSample {
    private ByteBuffer buffer;

    private int sampleRate;
    private int channels;
    private UUID uuid;

    public SgineSample(ByteBuffer sample, int sampleRate, int channels, UUID uuid) {

        this.buffer = sample;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.uuid = uuid;
    }

    public float getSampleTime() {
        return 1000.0f* buffer.capacity() / (channels * sampleRate);
    }

    public int getSampleChannels() {
        return channels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public ByteBuffer getDataBuffer() {
        return buffer;
    }

    public UUID getId() {
        return uuid;
    }
}
