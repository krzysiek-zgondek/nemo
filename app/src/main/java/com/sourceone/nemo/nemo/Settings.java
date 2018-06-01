package com.sourceone.nemo.nemo;

/**
 * Created by SourceOne on 12.09.2016.
 */
public class Settings {
    public static final int MAX_TRACKS = 16;

    public class Default {
        public static final double BPM = 120;
        public static final int STEPS = 4;
        public static final int BEATS = 16;
    }

    public class AudioFormat {
        public static final int SAMPLE_RATE = 44100;
        public static final int ENCODING = android.media.AudioFormat.ENCODING_PCM_16BIT;
        public static final int CHANNELS = android.media.AudioFormat.CHANNEL_OUT_STEREO;
    }
}
