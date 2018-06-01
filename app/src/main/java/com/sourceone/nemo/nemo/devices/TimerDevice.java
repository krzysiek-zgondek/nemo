package com.sourceone.nemo.nemo.devices;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.sourceone.nemo.nemo.Settings;
import com.sourceone.nemo.nemo.devices.timer.OnTimerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerEvent;
import com.sourceone.nemo.nemo.devices.timer.TimerThread;
import com.sourceone.nemo.nemo.sgine.devices.SgineDevice;
import com.sourceone.nemo.nemo.sgine.connections.SgineOutput;
import com.sourceone.nemo.nemo.signals.TimerSignal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import hugo.weaving.DebugLog;

/**
 * Created by SourceOne - Krzysztof Zgondek on 21.09.2016.
 */

public class TimerDevice extends SgineDevice implements OnTimerEvent {
    private final SgineOutput<TimerSignal> timerOutput;
    private AudioManager am;
    private Method m;

    private int steps = Settings.Default.STEPS;
    private int beats = Settings.Default.BEATS;
    private double bpm = Settings.Default.BPM;

    private long tickTime;

    private TimerThread timerThread;

    private Context context;

    public TimerDevice(Context context){
        this.context = context;
        timerOutput = createNewOutput(TimerSignal.class);

        initialize();
    }

    private int getAudioLatency(){
        try {
            Integer latency = (Integer) m.invoke(am, AudioManager.STREAM_MUSIC);
            Log.d("latency", String.valueOf(latency));
            return latency;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void initialize() {
        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        try{
            m = am.getClass().getMethod("getOutputLatency", int.class);
        }catch(Exception e){
        }

        tickTime = (long) (1000000000*(60.0/steps/bpm));

        timerThread = createNewTimer(steps, bpm);
    }

    private TimerThread createNewTimer(int steps, double bpm) {
        Log.d("TimerDevice", "Tick time is: "+String.valueOf(tickTime)+"ns");

        TimerThread thread = new TimerThread(tickTime, getAudioLatency());
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
    public void wire(SgineDevice device) {
        timerOutput.connect(device.getOrCreateInput(TimerSignal.class));
    }
}
