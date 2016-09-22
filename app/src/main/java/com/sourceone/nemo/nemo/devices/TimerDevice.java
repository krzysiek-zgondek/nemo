package com.sourceone.nemo.nemo.devices;

import android.util.Log;

import com.sourceone.nemo.nemo.Settings;
import com.sourceone.nemo.nemo.devices.timer.OnTimerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerThread;
import com.sourceone.nemo.nemo.sgine.devices.SgineDevice;
import com.sourceone.nemo.nemo.sgine.devices.SgineRouter;
import com.sourceone.nemo.nemo.sgine.connections.SgineOutput;
import com.sourceone.nemo.nemo.signals.TimerSignal;

/**
 * Created by SourceOne - Krzysztof Zgondek on 21.09.2016.
 */

public class TimerDevice extends SgineDevice implements OnTimerEvent {
    private final SgineOutput<TimerSignal> timerOutput;

    private int steps = Settings.Default.STEPS;
    private int beats = Settings.Default.BEATS;
    private double bpm = Settings.Default.BPM;

    private TimerThread timerThread;

    private long tickTime;

    public TimerDevice(){
        timerOutput = createNewOutput(TimerSignal.class);

        initialize();
    }

    private void initialize() {
        tickTime = (long) (1000000000*(60.0/steps/bpm));

        timerThread = createNewTimer(steps, bpm);
    }

    private TimerThread createNewTimer(int steps, double bpm) {
        Log.d("TimerDevice", "Tick time is: "+String.valueOf(tickTime)+"ns");

        TimerThread thread = new TimerThread(tickTime);
        thread.setOnTimerEventListener(this);
        return thread;
    }

    public void start() {
        timerThread.start();
    }

    @Override
    public void onTimerEvent(TimerEvent event) {
        timerOutput.write(new TimerSignal(event));
    }

    @Override
    public void wire(SgineRouter router) {
        timerOutput.connect(router.getOrCreateInput(TimerSignal.class));
    }
}
