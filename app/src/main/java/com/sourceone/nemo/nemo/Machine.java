package com.sourceone.nemo.nemo;

import android.content.Context;

import com.sourceone.nemo.nemo.devices.LogDevice;
import com.sourceone.nemo.nemo.devices.MetronomeDevice;
import com.sourceone.nemo.nemo.devices.SamplerDevice;
import com.sourceone.nemo.nemo.sgine.devices.SgineRouter;
import com.sourceone.nemo.nemo.devices.TimerDevice;

/**
 * Created by SourceOne - Krzysztof Zgondek on 21.09.2016.
 */

public class Machine {
    private Context context;

    SgineRouter Router;
    TimerDevice Timer;
    SamplerDevice Sampler;
    LogDevice Logger;
    MetronomeDevice Metronome;

    public Machine(Context context){
        this.context = context;

        Router = new SgineRouter();

//        Logger = new LogDevice();
        Timer = new TimerDevice(context);
        Sampler = new SamplerDevice(context);
        Metronome = new MetronomeDevice();

//        Logger.wire(Router);
        Timer.wire(Router);
        Sampler.wire(Router);
        Metronome.wire(Router);
    }

    public void start() {
        Timer.start();
    }
}
