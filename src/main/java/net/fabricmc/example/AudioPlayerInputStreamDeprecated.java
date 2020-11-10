package net.fabricmc.example;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.io.IOException;
import java.io.InputStream;

public class AudioPlayerInputStreamDeprecated extends InputStream {
    private AudioPlayer ap;
    private byte[] buffer = new byte[]{};
    int currBufferIndex = 0;

    public AudioPlayerInputStreamDeprecated(AudioPlayer ap) {
        this.ap = ap;
    }

    @Override
    public int read() throws IOException {
        if (currBufferIndex > buffer.length - 1) {
            buffer = ap.provide().getData();
            if (buffer.length == 0) return 0;
            currBufferIndex = 0;
        }
        int val = buffer[currBufferIndex];
        currBufferIndex++;
        return val;
    }
}
