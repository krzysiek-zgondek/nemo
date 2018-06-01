package com.sourceone.nemo.nemo.sgine;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.sourceone.nemo.nemo.Settings;
import com.sourceone.nemo.nemo.sgine.track.SgineTrack;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hugo.weaving.DebugLog;

import static android.os.Process.THREAD_PRIORITY_AUDIO;

/**
 * Created by SourceOne - Krzysztof Zgondek on 23.09.2016.
 */

//todo sprawdz wplyw setBufferSizeInFrames na laga (obciecie buffora)

public class SgineAudio {
    private int encoding = Settings.AudioFormat.ENCODING;
    private int channels = Settings.AudioFormat.CHANNELS;
    private int sampleRate = Settings.AudioFormat.SAMPLE_RATE;

    private int bufferChunks = 2;
    private int bufferSize;

    private SgineAudioBuffor renderBuffer;
    private AudioTrack audioDevice;
    private SgineTrack[] tracks;

    private RenderAudioThread renderer;

    private ScheduledExecutorService executor;

    public SgineAudio() {
        bufferSize = AudioTrack.getMinBufferSize(sampleRate, channels, encoding);

        audioDevice = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                channels,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );

        //fixme zaleznosc od ilosci kanalow /2 i bitow /2 (znajdz wszystkie miejsca)
        int buffToFrames = bufferSize / 2 / 2;
        int minimalFrames = bufferSize / 2 / 2;
        while(true){
            if(minimalFrames%2 != 0)
                break;

            minimalFrames = minimalFrames / 2;
        }
        bufferChunks = buffToFrames / minimalFrames;

        renderBuffer = new SgineAudioBuffor(bufferChunks, bufferSize/bufferChunks, minimalFrames);
//
//        audioDevice.setPositionNotificationPeriod(443);
//        audioDevice.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
//            @Override
//            public void onMarkerReached(AudioTrack track) {}
//
//            @Override
//            public void onPeriodicNotification(AudioTrack track) {
//                int actual = track.getPlaybackHeadPosition();
//                Log.d("#DATA", "PNHP:"+actual);
//            }
//        });

        Log.d(getClass().getName(), "CH NUM: " + channels);
        Log.d(getClass().getName(), "SM RATE: " + sampleRate);
        Log.d(getClass().getName(), "BUFFER DEPTH: " + bufferSize);
        Log.d(getClass().getName(), "buffChunkSize: " + bufferSize/bufferChunks);
        Log.d(getClass().getName(), "buffChunks: " + bufferChunks);
        Log.d(getClass().getName(), "buffToFrames: " + buffToFrames);
        Log.d(getClass().getName(), "minimalFrames: " + minimalFrames);
    }

    public void setTracks(SgineTrack[] tracks) {
        this.tracks = tracks;
    }

    public void start(){
        renderer = new RenderAudioThread(audioDevice, renderBuffer, tracks);
        renderer.start();
//
//        executor = Executors.newSingleThreadScheduledExecutor();
//        executor.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                transferAvailableChunks();
//            }
//        }, 1, 1, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executor.shutdown();

        if(renderer == null) return;

        if(renderer.isAlive())
            renderer.interrupt();

        audioDevice.stop();
    }
}


class RenderAudioThread extends Thread{
    private AudioTrack audio;
    private SgineTrack[] tracks;

    private SgineAudioBuffor renderBuffer;

    private ByteBuffer buffers[];
    private long[] tracks_events;
    private UUID[] ids;

    private ByteBuffer currentChunk;

    public RenderAudioThread(AudioTrack audioDevice, SgineAudioBuffor renderBuffer, SgineTrack[] tracks) {
        this.audio = audioDevice;
        this.renderBuffer = renderBuffer;
        this.tracks = tracks;

        createLocalBuffers();
    }

    private void createLocalBuffers() {
        buffers = new ByteBuffer[tracks.length];
        ids = new UUID[tracks.length];
        tracks_events = new long[tracks.length];

        Arrays.fill(tracks_events, -1);

        for(int i = 0; i < buffers.length; i++){
            ByteBuffer buffer;
            UUID id;

            SgineSample sample = tracks[i].getSample();
            if(sample == null) {
                buffer = ByteBuffer.allocate(0);
                id = UUID.randomUUID();
            }else{
                buffer = sample.getDataBuffer();
                id = sample.getId();
            }

            buffers[i] = buffer.asReadOnlyBuffer();
            ids[i] = id;
        }
    }

    public void checkIfBuffersChanged() {
        for(int i = 0; i < buffers.length; i++){
            SgineSample sample = tracks[i].getSample();
            if(sample == null || ids[i]==sample.getId())
                continue;

            buffers[i] = sample.getDataBuffer().asReadOnlyBuffer();
            ids[i] = sample.getId();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    int sample_to_process = 32;
    int sample_to_process_min = 16;
    int leftOver = 0;

    double time_over_delayed = 0;
    //todo synchro do czasu
    @Override
    public void run() {
        super.run();
        Process.setThreadPriority(THREAD_PRIORITY_AUDIO);

        double last_time = System.nanoTime() * 1e-6;

        int uploaded_frames = 0;
        int frames_played = 0;

        int upload_buffer = renderBuffer.getChunkCount();
        for(int i = 0; i < renderBuffer.getChunkCount(); i++){
            transferInitialChunk();
        }

        audio.play();

        while (!Thread.interrupted()) {
            final double current_time = System.nanoTime() * 1e-6;

            frames_played = frames_played + (int) ((current_time - last_time)/sample_time_ms);
            if(uploaded_frames - frames_played < renderBuffer.getBufferChunkFrames()){
                if (getFirstFreeBuffer()) {
                    Log.d(getClass().getSimpleName(), "uploaded_frames "+uploaded_frames+ ", frames_played "+frames_played+", frames_left "+(uploaded_frames-frames_played));
                    uploaded_frames = uploaded_frames + processBuffers(renderBuffer.getBufferChunkFrames(), 0.0);
                    upload_buffer--;
                }

                transferAvailableChunks();

                last_time = current_time;
            }
//            final int head_position = audio.getPlaybackHeadPosition();
//            if(uploaded_frames - head_position < renderBuffer.getBufferChunkFrames() || head_position == 0 || head_position2 != head_position){
//                Log.d(getClass().getSimpleName(), "head position "+head_position);
//                head_position2 = head_position;
//                currentChunk = null;
//                if (getFirstFreeBuffer()) {
//                    final int toProcess = renderBuffer.getBufferChunkFrames();
//                    uploaded_frames = uploaded_frames + processBuffers(toProcess, 0.0);
//                    Log.d(getClass().getSimpleName(), "regenerate buffer " + 10 + "ms, processed: " + uploaded_frames +" toProcess:"+toProcess);
//                    transferAvailableChunks();
//                }
//            }
//                if(uploaded_buffer_time - sample_time_ms*head_position <= sample_time_ms*renderBuffer.getBufferChunkFrames()){
        }
//
//        for (int i = 0; i < renderBuffer.getChunkCount(); i++) {
//            currentChunk = null;
//            if (getFirstFreeBuffer()) {
//                final int toProcess = renderBuffer.getBufferChunkFrames();
//                int processed = processBuffers(toProcess, 0.0);
//                Log.d(getClass().getSimpleName(), "regenerate buffer " + 10 + "ms, processed: " + processed +" toProcess:"+toProcess);
//            }
//        }
//        for (int i = 0; i < renderBuffer.getChunkCount(); i++) {
//            transferAvailableChunks();
//        }

//
//            while (!Thread.interrupted()) {
//                transferAvailableChunks();
//
//                double timeTaken = 0;
//                final int head_position = audio.getPlaybackHeadPosition();
//                if(uploaded_buffer_time - sample_time_ms*head_position <= sample_time_ms*renderBuffer.getBufferChunkFrames()){
//                    double buffer_chunk_time_stored = buffer_chunk_time;
//                    if(getFirstFreeBuffer()) {
//                        final double time_underbuffer = System.nanoTime()*1e-6;
//                        final int toProcess = renderBuffer.getBufferChunkFrames();
//                        int processed = processBuffers(toProcess, 0.0);
//
//                        final double time_underbuffer_after = System.nanoTime() * 1e-6;
//                        timeTaken = (time_underbuffer_after - time_underbuffer);
//                        Log.d(getClass().getSimpleName(), "uploaded_buffer_time " + uploaded_buffer_time + "ms, head_position: "+head_position*sample_time_ms+"ms");
//                        Log.d(getClass().getSimpleName(), "regenerate buffer " + 10 + "ms, processed: " + processed +" toProcess:"+toProcess+", timeTaken "+ timeTaken);
//                        buffer_chunk_time = buffer_chunk_time_stored;
//
//                        transferAvailableChunks();
//                    }
//                }
//
//                final double time_enter = System.nanoTime()*1e-6;
//                final double time_delayed = time_enter - time_exit;
//                final double chunk_over_delayed = time_delayed - buffer_chunk_time;
//
//                if( chunk_over_delayed >= 0 ){
//                    time_over_delayed = time_over_delayed + chunk_over_delayed;
//
//                    if(!getFirstFreeBuffer())
//                        continue;
//
//                    int samples_over_delayed = 0;
//                    int samples_restore = (int)(time_over_delayed / sample_time_ms);
//                    if( samples_restore > 0 ){
//                        samples_over_delayed = samples_restore;
//                        time_over_delayed = time_over_delayed - samples_over_delayed * sample_time_ms;
//                    }
//
//                    final int toProcess = sample_to_process + samples_over_delayed + leftOver;
//                    final int processed = processBuffers(toProcess, 0.0);
//
//                    leftOver = toProcess - processed;
//                    if(leftOver > 0){
//                        if(getFirstFreeBuffer()) {
//                            int processed2 = processBuffers(leftOver, buffer_chunk_time);
//                            leftOver = leftOver - processed2;
//                        }
//                    }
//
//                    time_exit = System.nanoTime()*1e-6;
//
//                    final double time_taken = time_exit - time_enter;
//
//                    if(time_taken > buffer_chunk_time){
//                        sample_to_process = (int) (sample_to_process*1.5);
//                        if(sample_to_process % 2 != 0)
//                            sample_to_process = sample_to_process + 1;
//                    }else{
//                        int sample_to_process_temp = (int) (sample_to_process * 0.5);
//                        if(sample_to_process_temp % 2 != 0)
//                            sample_to_process_temp = sample_to_process_temp + 1;
//
//                        if( sample_to_process_temp * sample_time_ms > time_taken ) {
//                            sample_to_process = sample_to_process_temp;
//                            if (sample_to_process < sample_to_process_min)
//                                sample_to_process = sample_to_process_min;
//                        }
//                    }
//                }
//        }
    }

    double uploaded_buffer_time = 0.0f;
    public void transferAvailableChunks() {
        ByteBuffer currentLockedChunk = renderBuffer.getCurrentLockedChunk();
        if(currentLockedChunk != null) {
            long bytes = writeChunkToAudioBuffer(currentLockedChunk);

            uploaded_buffer_time = uploaded_buffer_time + (bytes / 2 / 2)*sample_time_ms;
            if(bytes > 0)
                Log.d(getClass().getName(), "Written "+bytes+ "bytes");

            if(currentLockedChunk.remaining() == 0) {
                Log.d(this.getClass().getSimpleName(), "fully uploaded chunk");
                renderBuffer.unlockCurrentLockedChunk();
                checkIfBuffersChanged();
            }
        }

    }

    private long writeChunkToAudioBuffer(ByteBuffer currentLockedChunk) {
        return audio.write(currentLockedChunk, currentLockedChunk.remaining(), AudioTrack.WRITE_NON_BLOCKING);
    }
    private long lastTimeFree = 0;
    private boolean getFirstFreeBuffer() {
        if(currentChunk == null) {
            currentChunk = renderBuffer.getCurrentChunk();
            if(currentChunk == null) {
                if(lastTimeFree == 0)
                    lastTimeFree = System.currentTimeMillis();
                return false;
            }else{
                if(lastTimeFree > 0) {
                    Log.d(getClass().getSimpleName(), "Buffer delayed " + (System.currentTimeMillis() - lastTimeFree) + "ms");
                    lastTimeFree = 0;
                }
            }
        }
        return true;

    }

    double sample_time_ms = 1000.0 / Settings.AudioFormat.SAMPLE_RATE;
    double buffer_all_time = 0;
    double buffer_chunk_time = 0;

    private int processBuffers(int toProcess, double over_time) {
        buffer_chunk_time = over_time;

        int processed;
        for(processed = 0; processed < toProcess; processed++) {
            int sampleSumL;
            int sampleSumR;
            int index;

            for (index = 0, sampleSumL = 0, sampleSumR = 0; index < buffers.length; index++) {
                final ByteBuffer buffer = buffers[index];
                final SgineTrack track = tracks[index];

                final long eventTime = track.getPlayEventTime();
                if(eventTime != tracks_events[index]){
                    if(eventTime != -1) {
                        final long delay = System.currentTimeMillis() - eventTime;
                        Log.d(getClass().getSimpleName(), "TRIGGERED " + index + " [time:" + System.currentTimeMillis() + ", " + eventTime + ", delay: "+ delay+"]");
                    }else if(tracks_events[index] > 0){
                        Log.d(getClass().getSimpleName(), "STOPPED " + index + " [time:" + System.currentTimeMillis() +"]");
                    }
                    tracks_events[index] = eventTime;
                    buffer.rewind();
                }

                if (track.getPlayEventTime() == -1 || buffer.remaining()==0)
                    continue;

                final int singelSampleR = getSingleSample(buffer);
                final int singelSampleL = getSingleSample(buffer);

                sampleSumL = (int) (sampleSumL + singelSampleL*track.getGain());
                sampleSumR = (int) (sampleSumR + singelSampleR*track.getGain());
            }

            currentChunk.putShort((short) (sampleSumL));
            currentChunk.putShort((short) (sampleSumR));

            buffer_chunk_time = buffer_chunk_time + sample_time_ms;

            if (currentChunk.remaining() == 0) {
                Log.d(getClass().getSimpleName(), "Buffer Completed " + currentChunk.hashCode());
                renderBuffer.lockCurrentChunk();
                currentChunk = null;
                processed = processed + 1;

                break;
            }
        }
        buffer_all_time = buffer_all_time + buffer_chunk_time;

        return processed;
    }

    private static int getSingleSample(ByteBuffer buffer) {
        byte[] bytes = {0,0};
        buffer.get(bytes);

        return ((bytes[1] << 8) + (bytes[0] & 0xff));
    }

    private void transferInitialChunk() {
        Log.d(getClass().getSimpleName(), "WRITTING INITIAL CHUNK");

        renderBuffer.lockCurrentChunk();
        writeChunkToAudioBuffer(renderBuffer.getCurrentLockedChunk());
        renderBuffer.unlockCurrentLockedChunk();
    }
}