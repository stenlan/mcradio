package com.github.ph0t0shop.mcradio.audio;

import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.ShortPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import org.apache.commons.lang3.NotImplementedException;

import java.nio.ShortBuffer;

public class MergeChannelsAudioFilter implements FloatPcmAudioFilter {
    private final UniversalPcmAudioFilter downstream;

    public MergeChannelsAudioFilter(UniversalPcmAudioFilter downstream) {
        this.downstream = downstream;
    }
//
//    @Override
//    public void process(short[] input, int offset, int length) throws InterruptedException {
//        for (int i = 0; i < length; i+=2) {
//            int j = offset + (i >> 1);
//            int k = offset + i;
//            input[j] = (short) (((int)input[k] + input[k + 1]) >> 1);
//        }
//        downstream.process(input, offset, length >> 1);
//    }
//
//    @Override
//    public void process(ShortBuffer buffer) throws InterruptedException {
//        throw new NotImplementedException("");
//        // downstream.process(buffer);
//    }


    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        // pass
    }

    @Override
    public void flush() throws InterruptedException {
        // Nothing to do.
    }

    @Override
    public void close() {
        // Nothing to do.
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        for (int i = offset; i < offset+length; i++) {
            input[0][i] = (input[0][i] + input[1][i]) / 2;
        }
        downstream.process(new float[][]{input[0]}, offset, length);
    }

    private static float[] interleave(float[] a, float[] b) {
        float[] out = new float[a.length + b.length];
        int j = 0;
        int maxLength = Math.max(a.length, b.length);
        for (int i = 0; i < maxLength; i++) {
            if (i < a.length) {
                out[j++] = a[i];
            }
            if (i < b.length) {
                out[j++] = b[i];
            }
        }
        return out;
    }
}
