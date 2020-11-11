package com.github.ph0t0shop.mcradio.audio;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormatTools;
import com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Environment(EnvType.CLIENT)
public class PCMAudioStream implements AudioStream {
    private static AudioFormat format = AudioDataFormatTools.toAudioFormat(new Pcm16AudioDataFormat(1, 88200, 960, false));
    private AudioInputStream is;
    private int maxBuffSize = 176400;
    private byte[] tempBuff = new byte[maxBuffSize];
    private ByteBuffer byteBuff = ByteBuffer.allocateDirect(maxBuffSize).order(ByteOrder.LITTLE_ENDIAN);

    public PCMAudioStream(AudioInputStream is) {
        this.is = is;
    }

    @Override
    public AudioFormat getFormat() {
        return format;
    }


    @Override
    public ByteBuffer getBuffer(int size) throws IOException {
        if (size > maxBuffSize) {
            maxBuffSize = size;
            this.tempBuff = new byte[size];
            byteBuff = ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN);
        }
        // size = Math.min(is.available(), size);
        is.read(this.tempBuff, 0, size); // discard
        byteBuff.clear();
        byteBuff.put(this.tempBuff, 0, size);
        byteBuff.flip();
//        byte[] buff = new byte[size];
        return byteBuff;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
