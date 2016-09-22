package com.sourceone.nemo.nemo;

import android.media.MediaFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.channguyen.rsv.RangeSliderView;
import com.shawnlin.numberpicker.NumberPicker;
import com.sourceone.nemo.nemo.sgine.decoder.SgineDecoder;
import com.sourceone.nemo.nemo.sgine.track.SgineTrack;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.pads) GridView pads;
    @BindView(R.id.tempo_picker) NumberPicker tempoPicker;
    @BindView(R.id.progress) RangeSliderView progress;

    private SgineTrack tracks[] = new SgineTrack[Settings.MAX_TRACKS];

    private boolean loadingMode;
    private long tick;
    private double BPM = Settings.Default.BPM;
    private int steps = 4;
    private boolean isRecordMode = false;
    private double beat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        tempoPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setBPM(newVal);
            }
        });

        setBPM(Settings.Default.BPM);
        tempoPicker.setValue((int)getBPM());

        initialize();
    }

    private void initialize() {
        pads.setAdapter(new PadsAdapter(this, new PadsAdapter.OnPadHitListener(){
            @Override
            public void onPadHit(final int position, final PadsAdapter.Holder holder) {
                if(loadingMode){
                    DialogProperties properties=new DialogProperties();
                    properties.selection_mode=DialogConfigs.SINGLE_MODE;
                    properties.selection_type=DialogConfigs.FILE_SELECT;
                    properties.root=new File(DialogConfigs.DEFAULT_DIR);
                    properties.error_dir=new File(DialogConfigs.DEFAULT_DIR);
                    properties.extensions=null;

                    FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
                    dialog.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            if(files.length > 0) {
                                try {
                                    SgineDecoder.decode(files[0], createPadHandler(position, 1.0f));
                                    int index = files[0].lastIndexOf("/");
                                    String fileName = files[0].substring(index + 1);
                                    holder.name.setText(fileName);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    dialog.show();
                }else if(isRecordMode){
//                    SgineTrack.record(tracks[position], timer.current_tick);
                }else SgineTrack.play(tracks[position]);
            }
        }));
    }

    public SgineDecoder.OnDecodeCompleted createPadHandler(final int pad, final float gain){
        return new SgineDecoder.OnDecodeCompleted() {
            @Override
            public void onDecodeCompleted(ByteBuffer sample, MediaFormat format) {
                if(tracks[pad] != null)
                    tracks[pad].release();
                tracks[pad] = SgineTrack.create(sample, format);
                tracks[pad].volume(gain);
                Toast.makeText(MainActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void stop() {
    }

    private void start() {
    }

    private void setBPM(double value){
        BPM = value;
        tick = (long) (1000000000*(60.0/steps/BPM));
        beat = tick*steps;
    }

    @OnCheckedChanged(R.id.play)
    public void toggleRun(CompoundButton view, boolean isChecked){
        if(isChecked)
            start();
        else stop();
    }

    @OnCheckedChanged(R.id.record)
    public void toggleRecord(CompoundButton view, boolean isChecked){
        isRecordMode = isChecked;
    }

    @OnClick(R.id.load)
    public void setLoadingMode(){
        loadingMode = !loadingMode;
    }

    @OnClick(R.id.rewind)
    public void onRewindPressed(){
    }

    public double getBPM() {
        return BPM;
    }

}
