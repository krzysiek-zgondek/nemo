package com.sourceone.nemo.nemo.devices;

import android.util.Log;

import com.sourceone.nemo.nemo.sgine.devices.SgineDevice;
import com.sourceone.nemo.nemo.sgine.devices.SgineRouter;
import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;
import com.sourceone.nemo.nemo.signals.TimerSignal;

/**
 * Created by SourceOne on 22.09.2016.
 */
public class LogDevice extends SgineDevice {
    public LogDevice(){}

    @Override
    public void wire(SgineDevice device) {
        super.wire(device);
        device.getOrCreateOutput(SgineSignal.class).connect(new SgineInput<SgineSignal>() {
            @Override
            public void onSignal(SgineSignal signal) {
                Log.d(LogDevice.class.getName(), signal.toString() );
            }
        });

        device.getOrCreateOutput(TimerSignal.class).connect(new SgineInput<TimerSignal>() {
            @Override
            public void onSignal(TimerSignal signal) {
                Log.d(LogDevice.class.getName(), signal.toString() );
            }
        });

    }
}
