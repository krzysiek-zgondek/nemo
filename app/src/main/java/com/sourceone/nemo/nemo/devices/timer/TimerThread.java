package com.sourceone.nemo.nemo.devices.timer;

/**
 * Created by SourceOne on 21.09.2016.
 */

public class TimerThread extends Thread{
    private OnTimerEvent onTimerEventListener;

    private long tickTime;
    private long nowTime;
    private long lastTime;

    public TimerThread(long tickTime){
        this.tickTime = tickTime;
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

        lastTime = System.nanoTime() - tickTime;
        while (!Thread.interrupted()) {
            nowTime = System.nanoTime();
            if (nowTime - lastTime >= tickTime) {
                onTimerEventListener.onTimerEvent(TimerEvent.TICK);
                lastTime = nowTime;
            }
        }
    }

    public void setOnTimerEventListener(OnTimerEvent onTimerEventListener) {
        this.onTimerEventListener = onTimerEventListener;
    }
}
