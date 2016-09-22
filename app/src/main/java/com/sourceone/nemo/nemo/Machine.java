package com.sourceone.nemo.nemo;

import com.sourceone.nemo.nemo.sgine.SgineRouter;
import com.sourceone.nemo.nemo.sgine.timer.TimerDevice;

/**
 * Created by SourceOne - Krzysztof Zgondek on 21.09.2016.
 */

public class Machine {
    SgineRouter Router;
    TimerDevice Timer;
    LogDevice Logger;

    public Machine(){
        Router = new SgineRouter();
        Timer = new TimerDevice();
        Logger = new LogDevice();

        Timer.wire(Router);
        Logger.wire(Router);
    }

    public void start() {
        Timer.start();
    }
}
