package com.sourceone.nemo.nemo.sgine.devices;

import android.support.annotation.NonNull;

import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;
import com.sourceone.nemo.nemo.sgine.connections.SgineSocket;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import hugo.weaving.DebugLog;

/**
 * Created by SourceOne - Krzysztof Zgondek on 22.09.2016.
 */

//global moze zostac usuniety jesli nie znajdzie sie potrzeba na tracking wszystkiego na raz
public class SgineRouter extends SgineDevice{
    private final SgineSocket<SgineSignal> global;

    public SgineRouter(){
        super();
        global = attachSocket(SgineSignal.class, super.createNewSocket());
    }

    @NonNull
    @Override
    protected <Signal extends SgineSignal> SgineSocket<Signal> createNewSocket() {
        return new SgineRouterSocket<>(global);
    }

    class SgineRouterSocket<Signal extends SgineSignal> extends SgineSocket<Signal> {
        private SgineInput<SgineSignal> input;

        public SgineRouterSocket(SgineInput<SgineSignal> input) { this.input = input; }

        @Override
        public void onSignal(final Signal signal) {
            super.onSignal(signal);
            input.onSignal(signal);
        }

    }
}
