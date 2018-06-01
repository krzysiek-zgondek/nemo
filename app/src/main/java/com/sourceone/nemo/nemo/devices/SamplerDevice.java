package com.sourceone.nemo.nemo.devices;

import android.content.Context;
import android.media.AudioManager;

import com.sourceone.nemo.nemo.devices.timer.SamplerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerEvent;
import com.sourceone.nemo.nemo.sgine.SgineAudio;
import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.decoder.SgineDecoder;
import com.sourceone.nemo.nemo.sgine.devices.SgineDevice;
import com.sourceone.nemo.nemo.sgine.devices.SgineRouter;
import com.sourceone.nemo.nemo.sgine.track.SgineTrack;
import com.sourceone.nemo.nemo.signals.SamplerSignal;
import com.sourceone.nemo.nemo.signals.TimerSignal;

import java.lang.reflect.Method;

/**
 * Created by SourceOne - Krzysztof Zgondek on 22.09.2016.
 */

public class SamplerDevice extends SgineDevice {
    private static final int TEST_TRACKS = 3;
    private static final int SPECIAL_TRACKS = 1;

    public static final int SPECIAL_METRONOME_TRACK = 0;

    private Context context;
    private int maxTracks = 0;

    private SgineAudio audio;
    private SgineTrack tracks[];

    private AudioManager am;
    private Method m;

    public SamplerDevice(Context context) {
        this(context, TEST_TRACKS);
    }

    public SamplerDevice(Context context, int maxTracks) {
        this.context = context;
        this.maxTracks = maxTracks;

        initialize();
    }

    private void initialize() {
        audio = new SgineAudio();
        tracks = new SgineTrack[maxTracks + SPECIAL_TRACKS];

        tracks[0] = SgineTrack.create().load("snare.mp3", 0.4f, context.getAssets());
        tracks[1] = SgineTrack.create().load("hihat.mp3", 0.4f, context.getAssets());
        tracks[2] = SgineTrack.create().load("kick.mp3", 0.4f, context.getAssets());
        tracks[maxTracks + SPECIAL_METRONOME_TRACK] = SgineTrack.create().load("tick.mp3", 1.0f, context.getAssets());

        audio.setTracks(tracks);
    }

    public SgineTrack getTrack(int track, boolean special){
        return tracks[(special?maxTracks:0)+track];
    }

    @Override
    public void wire(SgineDevice device) {
        super.wire(device);
        device.getOrCreateOutput(SamplerSignal.class).connect(new SgineInput<SamplerSignal>() {
            @Override
            public void onSignal(SamplerSignal signal) {
                SgineTrack track = getTrack(signal.getTrack(), signal.isSpecial());
                if(signal.getEvent() == SamplerEvent.PLAY)
                    track.play();
                else if(signal.getEvent() == SamplerEvent.STOP)
                    track.stop();
            }
        });
        device.getOrCreateOutput(TimerSignal.class).connect(new SgineInput<TimerSignal>() {
            @Override
            public void onSignal(TimerSignal signal) {
                if(signal.getEvent() == TimerEvent.START)
                    audio.start();
                else if(signal.getEvent() == TimerEvent.STOP)
                    audio.stop();

            }
        });
    }
}

