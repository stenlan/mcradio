package net.fabricmc.example;

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
            this.tempBuff = new byte[size];
            maxBuffSize = size;
        }
        // size = Math.min(is.available(), size);
        System.out.println(is.read(this.tempBuff, 0, size));
        ByteBuffer buff = ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN);
        buff.put(this.tempBuff, 0, size);
        buff.flip();
//        byte[] buff = new byte[size];
        return buff;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }
}
