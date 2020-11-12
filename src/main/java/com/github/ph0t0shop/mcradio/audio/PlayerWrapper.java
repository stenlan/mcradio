package com.github.ph0t0shop.mcradio.audio;


import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_LE;


public class PlayerWrapper extends InputStream {
    private static AudioDataFormat dataFormat = COMMON_PCM_S16_LE;
    private AudioPlayer player;
    private AudioPlayerManager manager;
    private AudioLoadResultHandler handler;
    private AudioPlayerInputStream stream;

    public PlayerWrapper() {
        manager = new DefaultAudioPlayerManager();
        manager.getConfiguration().setOutputFormat(dataFormat);
        AudioSourceManagers.registerRemoteSources(manager);
        player = manager.createPlayer();
        player.setFilterFactory(new StereoToMonoFilterFactory(manager.getConfiguration()));
        handler = new AlwaysPlayResultHandler();
        stream = new AudioPlayerInputStream(dataFormat, player, 0, true);
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(@NotNull byte[] buffer, int offset, int length) throws IOException {
        return stream.read(buffer, offset, length);
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public AudioPlayerManager getManager() {
        return manager;
    }

    public void playImmediate(String identifier) {
        this.manager.loadItem(identifier, handler);
    }


    private class AlwaysPlayResultHandler implements AudioLoadResultHandler {
        @Override
        public void trackLoaded(AudioTrack track) {
            System.out.println("Playing from single track");
            player.playTrack(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            System.out.println("Playing first track from playlist");
            player.playTrack(playlist.getTracks().get(0));
        }

        @Override
        public void noMatches() {
            // Notify the user that we've got nothing
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            // Notify the user that everything exploded
        }
    }

    private static class StereoToMonoFilterFactory implements PcmFilterFactory {
        boolean initialized = false;
        private List<AudioFilter> filterList;
        private AudioConfiguration config;

        public StereoToMonoFilterFactory(AudioConfiguration config) {
            this.config = config;
        }

        @Override
        public List<AudioFilter> buildChain(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
            if (!initialized) {
                filterList = Collections.singletonList(new MergeChannelsAudioFilter(output));
                initialized = true;
            }
            return filterList;
        }
    }
}
