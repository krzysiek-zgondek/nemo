package com.sourceone.nemo.nemo;

import android.util.Log;

import com.sourceone.nemo.nemo.sgine.SgineDevice;
import com.sourceone.nemo.nemo.sgine.SgineRouter;
import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineOutput;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;
import com.sourceone.nemo.nemo.sgine.timer.TestSignal;

/**
 * Created by SourceOne on 22.09.2016.
 */
public class LogDevice extends SgineDevice {
    public LogDevice(){}

    @Override
    public void wire(SgineRouter router) {
        super.wire(router);
        router.getOrCreateOutput(SgineSignal.class).connect(new SgineInput<SgineSignal>() {
            @Override
            public void onSignal(SgineSignal signal) {
                Log.d(LogDevice.class.getName(), signal.toString() );
            }
        });

    }
}
