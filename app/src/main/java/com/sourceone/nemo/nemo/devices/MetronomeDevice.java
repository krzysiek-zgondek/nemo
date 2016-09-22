package com.sourceone.nemo.nemo.devices;

import android.util.Log;

import com.sourceone.nemo.nemo.Settings;
import com.sourceone.nemo.nemo.devices.timer.OnTimerEvent;
import com.sourceone.nemo.nemo.devices.timer.SamplerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerThread;
import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineOutput;
import com.sourceone.nemo.nemo.sgine.devices.SgineDevice;
import com.sourceone.nemo.nemo.sgine.devices.SgineRouter;
import com.sourceone.nemo.nemo.signals.SamplerSignal;
import com.sourceone.nemo.nemo.signals.TimerSignal;

/**
 * Created by SourceOne - Krzysztof Zgondek on 21.09.2016.
 */

public class MetronomeDevice extends SgineDevice {
    private SgineOutput<SamplerSignal> samplerOutput;

    public MetronomeDevice(){
        initialize();
    }

    private void initialize() {
        samplerOutput = createNewOutput(SamplerSignal.class);
    }


    @Override
    public void wire(SgineRouter router) {
        samplerOutput.connect(router.getOrCreateInput(SamplerSignal.class));

        router.getOrCreateOutput(TimerSignal.class).connect(new SgineInput<TimerSignal>() {
            int counter = 0;
            @Override
            public void onSignal(TimerSignal signal) {
                samplerOutput.write(new SamplerSignal(SamplerDevice.SPECIAL_METRONOME_TRACK, true, SamplerEvent.PLAY));
                if(counter%2==0)
                    samplerOutput.write(new SamplerSignal(1, false, SamplerEvent.PLAY));
                if(counter == 7)
                    samplerOutput.write(new SamplerSignal(0, false, SamplerEvent.PLAY));
                counter = (counter + 1)%8;
            }
        });
    }
}
