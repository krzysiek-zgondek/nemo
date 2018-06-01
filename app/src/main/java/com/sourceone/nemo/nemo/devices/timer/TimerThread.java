package com.sourceone.nemo.nemo.devices.timer;

/**
 * Created by SourceOne on 21.09.2016.
 */

public class TimerThread extends Thread{
    private OnTimerEvent onTimerEventListener;

    private int latency;
    private long tickTime;
    private long nowTime;
    private long lastTime;

    public TimerThread(long tickTime, int latency){
        this.tickTime = tickTime;
        this.latency = latency*1000000;
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

        trigger();
        lastTime = System.nanoTime()+latency;
        while (!Thread.interrupted()) {
            nowTime = System.nanoTime();
            if (nowTime - lastTime >= tickTime) {
                trigger();
            }
        }
    }

    private void trigger() {
        onTimerEventListener.onTimerEvent(TimerEvent.TICK);
        lastTime = nowTime;
    }

    public void setOnTimerEventListener(OnTimerEvent onTimerEventListener) {
        this.onTimerEventListener = onTimerEventListener;
    }
}
