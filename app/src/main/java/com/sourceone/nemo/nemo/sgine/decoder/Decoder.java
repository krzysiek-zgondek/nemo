package com.sourceone.nemo.nemo.sgine.decoder;

import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by SourceOne on 12.09.2016.
 */
public class Decoder {
    public interface OnDecodeCompleted{
        void onDecodeCompleted(ByteBuffer sample, MediaFormat format);
    }

    private final MediaExtractor extractor;
    private OnDecodeCompleted onDecodeCompleteListener;

    private Decoder(){
        extractor = new MediaExtractor();
    }

    public static void decode(AssetFileDescriptor descriptor, final OnDecodeCompleted listener) throws IOException {
        Decoder decoder = new Decoder();
        decoder.setOnDecodeCompleteListener(listener);
        decoder.setSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
        decoder.proceed();
    }

    public static void decode(String filepath, final OnDecodeCompleted listener) throws IOException {
        Decoder decoder = new Decoder();
        decoder.setOnDecodeCompleteListener(listener);
        decoder.setSource(filepath);
        decoder.proceed();
    }

    public void setOnDecodeCompleteListener(OnDecodeCompleted onDecodeCompleteListener) {
        this.onDecodeCompleteListener = onDecodeCompleteListener;
    }

    private void proceed() {
        try {
            final MediaFormat format = extractor.getTrackFormat(0);
            String mime = format.getString(MediaFormat.KEY_MIME);

            MediaCodec codec = MediaCodec.createDecoderByType(mime);
            codec.setCallback(new MediaCodec.Callback() {

                public boolean hasMoreData = true;
                public ArrayList<byte[]> chunks = new ArrayList<>();

                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    if(!hasMoreData)
                        return;

                    int result = 0;

                    ByteBuffer buffer = codec.getInputBuffer(index);
                    if(buffer!=null)
                        result = extractor.readSampleData(buffer, 0);

                    if(result < 0){
                        codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }else
                        codec.queueInputBuffer(index, 0, result, extractor.getSampleTime(), 0);

                    hasMoreData = extractor.advance();
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    ByteBuffer buffer = codec.getOutputBuffer(index);
                    if(buffer!=null && info.size > 0) {

                        byte[] chunk = new byte[info.size];
                        buffer.get(chunk);
                        buffer.clear( );
                        chunks.add(chunk);

                        Log.d(getClass().getName(), "Written "+info.size+ "bytes");
                    }

                    codec.releaseOutputBuffer(index, false);

                    if(info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        Log.d(getClass().getName(), "EOS. Stopping decoder!");
                        finish(codec, chunks, format, onDecodeCompleteListener);
                    }
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    e.printStackTrace();
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                    Log.d(getClass().getName(), "Format changed" + format.toString());
                }
            });

            codec.configure(format, null, null, 0);
            codec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSource(FileDescriptor fileDescriptor, long startOffset, long length) throws IOException {
        extractor.setDataSource(fileDescriptor, startOffset, length);
        extractor.selectTrack(0);
    }

    private void setSource(String filepath) throws IOException {
        extractor.setDataSource(filepath);
        extractor.selectTrack(0);
    }

    private void finish(MediaCodec codec, ArrayList<byte[]> chunks, MediaFormat format, OnDecodeCompleted listener){
        if(codec != null){
            codec.stop();
            codec.release();
        }

        int size = 0;
        for(byte [] chunk: chunks)
            size = size + chunk.length;

        ByteBuffer sample = ByteBuffer.allocate(size);
        for(byte [] chunk: chunks)
            sample.put(chunk);
        sample.rewind();

        if(listener!=null)
            listener.onDecodeCompleted(sample, format);

        extractor.release();
    }
}
