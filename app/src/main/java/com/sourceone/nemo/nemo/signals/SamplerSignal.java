package com.sourceone.nemo.nemo.signals;

import com.sourceone.nemo.nemo.devices.timer.SamplerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerEvent;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;

/**
 * Created by SourceOne on 21.09.2016.
 */
public class SamplerSignal extends SgineSignal {
    private int track;
    private boolean special;
    private final SamplerEvent event;

    public SamplerSignal(int track, boolean special, SamplerEvent event) {
        this.track = track;
        this.special = special;
        this.event = event;
    }

    public int getTrack() {
        return track;
    }

    public SamplerEvent getEvent() {
        return event;
    }

    public boolean isSpecial() {
        return special;
    }
}
