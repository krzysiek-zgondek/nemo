package com.sourceone.nemo.nemo.sgine.connections;

import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;

/**
 * Created by SourceOne - Krzysztof Zgondek on 22.09.2016.
 */
public interface SgineInput<Signal extends SgineSignal>{
    void onSignal(Signal signal);
}
