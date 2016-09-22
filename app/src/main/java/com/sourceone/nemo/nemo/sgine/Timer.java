//package com.sourceone.nemo.nemo.sgine.timer;
//
//import com.sourceone.nemo.nemo.MainActivity;
//import com.sourceone.nemo.nemo.Settings;
//import com.sourceone.nemo.nemo.sgine.track.SgineTrack;
//
//
////todo remove
//
///**
// * Created by SourceOne on 13.09.2016.
// */
//public class Timer extends Thread {
//    private final OnTimerTick listener;
//
//    private long tick;
//    private int steps = 4;
//    private int beats = 16;
//
//    public int current_tick = 0;
//    public int current_beat = 0;
//
//    public Timer(OnTimerTick listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public void run() {
//        super.run();
//
//        long lastTick = System.nanoTime() - tick;
//        long startTick = lastTick;
//
//        while (!Thread.interrupted()) {
//            long nowTick = System.nanoTime();
//            if (nowTick - lastTick >= tick) {
////                for (SgineTrack track : activity.tracks) {
////                    SgineTrack.tick(track, current_tick);
////                }
//
//                if ((current_tick + 1) % steps == 0) {
////                    activity.tracks[Settings.METRONOME_TACK].play();
//
//                    if (current_beat == beats - 1) {
//                        startTick = nowTick;
//                        current_beat = -1;
//                        current_tick = -1;
//                    }
//
//                    current_beat++;
//                }
////                    activity.runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            activity.progress.setInitialIndex(current_beat);
////                        }
////                    });
////                } else
////                    activity.tracks[Settings.SPECIAL_METRONOME_TRACK].play();
//
//                current_tick++;
//                lastTick = nowTick;
//            }
//        }
//    }
//}
