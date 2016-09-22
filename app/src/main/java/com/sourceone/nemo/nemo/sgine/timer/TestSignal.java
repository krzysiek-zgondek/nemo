package com.sourceone.nemo.nemo.sgine.timer;

import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;

/**
 * Created by SourceOne on 21.09.2016.
 */
public class TestSignal extends SgineSignal {
    private TimerEvent event;

    public TestSignal(TimerEvent event) {
        this.event = event;
    }
}
