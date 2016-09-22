package com.sourceone.nemo.nemo.sgine.connections;

/**
 * Created by SourceOne - Krzysztof Zgondek on 22.09.2016.
 */
public interface SgineOutput<Signal extends SgineSignal>{
    void connect(SgineInput<Signal> input);
    void write(Signal signal);
}
