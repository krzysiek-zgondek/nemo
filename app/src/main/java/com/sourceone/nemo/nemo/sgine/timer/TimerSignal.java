package com.sourceone.nemo.nemo.sgine.timer;

import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;

/**
 * Created by SourceOne on 21.09.2016.
 */
public class TimerSignal extends SgineSignal {
    private TimerEvent event;

    public TimerSignal(TimerEvent event) {
        this.event = event;
    }
}
