package com.sourceone.nemo.nemo.sgine.devices;

import android.support.annotation.NonNull;

import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineOutput;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;
import com.sourceone.nemo.nemo.sgine.connections.SgineSocket;

import java.util.HashMap;

/**
 * Created by SourceOne on 21.09.2016.
 */
public abstract class SgineDevice{
    protected HashMap<Class<? extends SgineSignal>, SgineSocket<? extends SgineSignal>> sockets;

    public SgineDevice() {
        sockets = new HashMap<>();
    }

    protected  <Signal extends SgineSignal> SgineOutput<Signal> createNewOutput(Class<Signal> cls) {
        return attachNewSocket(cls);
    }

    protected <Signal extends SgineSignal> SgineInput<Signal> createNewInput(Class<Signal> cls) {
        return attachNewSocket(cls);
    }

    protected <Signal extends SgineSignal> SgineSocket<Signal> attachNewSocket(Class<Signal> cls) {
        SgineSocket<Signal> socket = createNewSocket();
        return attachSocket(cls, socket);
    }

    protected <Signal extends SgineSignal> SgineSocket<Signal> attachSocket(Class<Signal> cls,
                                                                            SgineSocket<Signal> socket) {
        sockets.put(cls, socket);
        return socket;
    }

    @NonNull
    protected <Signal extends SgineSignal> SgineSocket<Signal> createNewSocket() {
        return new SgineSocket<>();
    }

    @SuppressWarnings("unchecked")
    public <Signal extends SgineSignal> SgineOutput<Signal> getOrCreateOutput(Class<Signal> cls){
        SgineOutput<Signal> output = (SgineOutput<Signal>) sockets.get(cls);
        if(output==null)
            output = createNewOutput(cls);
        return output;
    }

    @SuppressWarnings("unchecked")
    public <Signal extends SgineSignal> SgineInput<Signal> getOrCreateInput(Class<Signal> cls){
        SgineInput<Signal> input = (SgineInput<Signal>) sockets.get(cls);
        if(input==null)
            input = createNewInput(cls);
        return input;
    }

    public void wire(SgineRouter router){}
}
