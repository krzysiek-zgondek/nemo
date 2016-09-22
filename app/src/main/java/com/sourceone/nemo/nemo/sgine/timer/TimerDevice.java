package com.sourceone.nemo.nemo.sgine.timer;

import android.util.Log;

import com.sourceone.nemo.nemo.Settings;
import com.sourceone.nemo.nemo.sgine.SgineDevice;
import com.sourceone.nemo.nemo.sgine.SgineRouter;
import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineOutput;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;

/**
 * Created by SourceOne - Krzysztof Zgondek on 21.09.2016.
 */

public class TimerDevice extends SgineDevice implements OnTimerEvent {
    private final SgineOutput<TimerSignal> timerOutput;
    private final SgineOutput<TestSignal> testOutput;

    private int steps = Settings.Default.STEPS;
    private int beats = Settings.Default.BEATS;
    private double bpm = Settings.Default.BPM;

    private TimerThread timerThread;

    public TimerDevice(){
        timerThread = createNewTimer(steps, bpm);
        timerOutput = createNewOutput(TimerSignal.class);
        testOutput = createNewOutput(TestSignal.class);
    }

    private TimerThread createNewTimer(int steps, double bpm) {
        TimerThread thread = new TimerThread(steps, bpm);
        thread.setOnTimerEventListener(this);
        return thread;
    }

    @Override
    public void onTimerEvent(TimerEvent event) {
        timerOutput.write(new TimerSignal(event));
        testOutput.write(new TestSignal(event));
    }

    public void start() {
        timerThread.start();
    }

    @Override
    public void wire(SgineRouter router) {
        SgineInput<TimerSignal> timerInput = router.getOrCreateInput(TimerSignal.class);
        SgineInput<TestSignal> testInput = router.getOrCreateInput(TestSignal.class);

        timerOutput.connect(timerInput);
        testOutput.connect(testInput);
    }
}
