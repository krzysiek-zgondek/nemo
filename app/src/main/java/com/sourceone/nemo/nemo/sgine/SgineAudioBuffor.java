package com.sourceone.nemo.nemo.sgine;

import android.media.AudioTrack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by SourceOne - Krzysztof Zgondek on 23.09.2016.
 */
public class SgineAudioBuffor {
    private int chunkCount;
    private int chunkSize;
    private int bufferChunkFrames;
    private int size;

    private Queue<ByteBuffer> availableChunks;
    private Queue<ByteBuffer> lockedChunks;

    public SgineAudioBuffor(int chunkCount, int chunkSize, int bufferChunkFrames) {
        this.chunkCount = chunkCount;
        this.chunkSize = chunkSize;
        this.bufferChunkFrames = bufferChunkFrames;
        this.size = chunkSize * chunkCount;

        this.lockedChunks = createBuffersHolder(chunkCount);
        this.availableChunks = createChunkBuffers(chunkCount, chunkSize);
    }

    private static Queue<ByteBuffer> createChunkBuffers(int bufferChunkCount, int bufferChunkSize) {
        Queue <ByteBuffer> chunks = createBuffersHolder(bufferChunkCount);
        for(int i = 0; i < bufferChunkCount; i++) {
            ByteBuffer allocate = ByteBuffer.allocateDirect(bufferChunkSize);
            allocate.order(ByteOrder.nativeOrder());
            chunks.add(allocate);
        }

        return chunks;
    }

    private static Queue<ByteBuffer> createBuffersHolder(int chunkCount) {
        return new ArrayDeque<>(chunkCount);
    }

    public int getBufferChunkFrames() {
        return bufferChunkFrames;
    }

    public int getBufferSize() {
        return size;
    }

    public ByteBuffer getCurrentChunk() {
        return availableChunks.peek();
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void lockCurrentChunk() {
        ByteBuffer chunk = availableChunks.poll();
        chunk.clear();
        lockedChunks.add(chunk);
    }

    public ByteBuffer getCurrentLockedChunk() {
        return lockedChunks.peek();
    }

    public void unlockCurrentLockedChunk() {
        ByteBuffer chunk = lockedChunks.poll();
        chunk.clear();
        availableChunks.add(chunk);
    }

}
