package com.sourceone.nemo.nemo.sgine.connections;

import com.sourceone.nemo.nemo.sgine.connections.SgineInput;
import com.sourceone.nemo.nemo.sgine.connections.SgineOutput;
import com.sourceone.nemo.nemo.sgine.connections.SgineSignal;

import java.util.ArrayList;

/**
 * Created by SourceOne - Krzysztof Zgondek on 22.09.2016.
 */
public class SgineSocket<Signal extends SgineSignal> implements SgineInput<Signal>, SgineOutput<Signal> {
    private ArrayList<SgineInput<Signal>> outputs;

    public SgineSocket(){
        outputs = new ArrayList<>();
    }

    public void write(Signal signal){
        for(SgineInput<Signal> output: outputs)
            output.onSignal(signal);
    }

    @Override
    public void onSignal(Signal signal) { write(signal); }

    public void connect(SgineInput<Signal> input) { outputs.add(input); }
}
