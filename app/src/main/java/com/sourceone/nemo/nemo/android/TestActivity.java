package com.sourceone.nemo.nemo.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sourceone.nemo.nemo.Machine;
import com.sourceone.nemo.nemo.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Machine machine = new Machine(this);
        machine.start();
    }
}
