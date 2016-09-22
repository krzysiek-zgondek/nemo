package com.sourceone.nemo.nemo.sgine;

import android.support.annotation.NonNull;

import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;
import com.sourceone.nemo.nemo.sgine.connections.SgineSocket;

/**
 * Created by SourceOne on 22.09.2016.
 */
public class SgineRouter extends SgineDevice{
    private final SgineSocket<SgineSignal> global;

    public SgineRouter(){
        super();
        global = attachNewSocket(SgineSignal.class);
    }

    @NonNull
    @Override
    protected <Signal extends SgineSignal> SgineSocket<Signal> createNewSocket() {
        return new SgineRouterSocket<>(global);
    }
}

class SgineRouterSocket<Signal extends SgineSignal> extends SgineSocket<Signal> {
    private SgineInput<SgineSignal> input;

    public SgineRouterSocket(SgineInput<SgineSignal> input) {
        this.input = input;
    }

    @Override
    public void onSignal(Signal signal) {
        super.onSignal(signal);
        input.onSignal(signal);
    }
}