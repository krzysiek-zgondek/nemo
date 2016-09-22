package com.sourceone.nemo.nemo.sgine.timer;

/**
 * Created by SourceOne on 21.09.2016.
 */

public class TimerThread extends Thread{

    private OnTimerEvent onTimerEventListener;

    private long stepTick;
    private long nowTick;
    private long lastTick;

    public TimerThread(int steps, double BPM){
        this.stepTick = (long) (1000000000*(60.0/steps/BPM));
    }

    @Override
    public synchronized void start() {
        super.start();
        onTimerEventListener.onTimerEvent(TimerEvent.START);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        onTimerEventListener.onTimerEvent(TimerEvent.STOP);
    }

    @Override
    public void run() {
        super.run();

        lastTick = System.nanoTime() - stepTick;
        while (!Thread.interrupted()) {
            nowTick = System.nanoTime();
            if (nowTick - lastTick >= stepTick) {
                onTimerEventListener.onTimerEvent(TimerEvent.TICK);
                lastTick = nowTick;
            }
        }
    }

    public void setOnTimerEventListener(OnTimerEvent onTimerEventListener) {
        this.onTimerEventListener = onTimerEventListener;
    }
}
