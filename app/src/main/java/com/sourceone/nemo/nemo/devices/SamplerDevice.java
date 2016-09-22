package com.sourceone.nemo.nemo.devices;

import android.content.Context;
import android.media.MediaFormat;

import com.sourceone.nemo.nemo.devices.sampler.OnSampleDecoded;
import com.sourceone.nemo.nemo.devices.timer.SamplerEvent;
import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.decoder.SgineDecoder;
import com.sourceone.nemo.nemo.sgine.devices.SgineDevice;
import com.sourceone.nemo.nemo.sgine.devices.SgineRouter;
import com.sourceone.nemo.nemo.sgine.track.SgineTrack;
import com.sourceone.nemo.nemo.signals.SamplerSignal;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by SourceOne - Krzysztof Zgondek on 22.09.2016.
 */

public class SamplerDevice extends SgineDevice {
    private static final int TEST_TRACKS = 3;
    private static final int SPECIAL_TRACKS = 1;

    public static final int SPECIAL_METRONOME_TRACK = 0;

    private Context context;
    private int maxTracks = 0;

    private SgineTrack tracks[];


    public SamplerDevice(Context context) {
        this(context, TEST_TRACKS);
    }

    public SamplerDevice(Context context, int maxTracks) {
        this.context = context;
        this.maxTracks = maxTracks;

        initialize();
    }

    private void initialize() {
        tracks = new SgineTrack[maxTracks + SPECIAL_TRACKS];

        createTestTracks();
        createSpecialTracks();
    }

    public SgineDecoder.OnDecodeCompleted createTrackHandler(int track, float gain){
        return new OnSampleDecoded(this, track, gain);
    }

    public void createTrack(int track, ByteBuffer sample, MediaFormat format, float gain) {
        if(tracks[track] != null)
            tracks[track].release();
        tracks[track] = SgineTrack.create(sample, format);
        tracks[track].volume(gain);
    }

    public void createSpecialTracks(){
        try {
            SgineDecoder.decode(context.getAssets().openFd("tick.mp3"),
                    createTrackHandler(maxTracks + SPECIAL_METRONOME_TRACK, 1.0f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTestTracks() {
        try {
            SgineDecoder.decode(context.getAssets().openFd("snare.mp3"), createTrackHandler(0, 1.0f));
            SgineDecoder.decode(context.getAssets().openFd("hihat.mp3"), createTrackHandler(1, 1.0f));
            SgineDecoder.decode(context.getAssets().openFd("kick.mp3"), createTrackHandler(2, 1.0f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SgineTrack getTrack(int track, boolean special){
        return tracks[(special?maxTracks:0)+track];
    }

    @Override
    public void wire(SgineRouter router) {
        super.wire(router);
        router.getOrCreateOutput(SamplerSignal.class).connect(new SgineInput<SamplerSignal>() {
            @Override
            public void onSignal(SamplerSignal signal) {
                SgineTrack track = getTrack(signal.getTrack(), signal.isSpecial());
                if(signal.getEvent() == SamplerEvent.PLAY)
                    SgineTrack.play(track);
                else if(signal.getEvent() == SamplerEvent.STOP)
                    SgineTrack.stop(track);
            }
        });
    }
}

