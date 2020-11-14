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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_LE;


public class PlayerWrapper extends InputStream {
    private static AudioDataFormat dataFormat = COMMON_PCM_S16_LE;
    private AudioPlayer player;
    private AudioPlayerManager manager;
    private AudioLoadResultHandler handler;
    private AudioPlayerInputStream stream;
    private ArrayList<PCMAudioStream> pcmStreams = new ArrayList<>();
    private boolean ignoreComingTrackStop = false;

    public PlayerWrapper() {
        manager = new DefaultAudioPlayerManager();
        manager.getConfiguration().setOutputFormat(dataFormat);
        AudioSourceManagers.registerRemoteSources(manager);
        player = manager.createPlayer();
        player.setFilterFactory(new StereoToMonoFilterFactory());
        handler = new AlwaysPlayResultHandler();
        stream = new AudioPlayerInputStream(dataFormat, player, 0, true);
    }

    public void addListener(PCMAudioStream pcmStream) {
        this.player.addListener(pcmStream);
        this.pcmStreams.add(pcmStream);
    }

    public void removeListener(PCMAudioStream pcmStream) {
        this.player.removeListener(pcmStream);
        this.pcmStreams.remove(pcmStream);
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

    public boolean hasStreams() {
        return this.pcmStreams.size() > 0;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public AudioPlayerManager getManager() {
        return manager;
    }

    public void playImmediate(String identifier, BlockPos pos, float volume, int attenuation, SoundInstance.AttenuationType attenuationType) {
        // player = manager.createPlayer();
//      player.setFilterFactory(new StereoToMonoFilterFactory(manager.getConfiguration()));
        if (!this.hasStreams()) {
            MinecraftClient.getInstance().getSoundManager().play(new RadioSoundInstance(new RadioIdentifier(pos), volume, attenuation, attenuationType));
        } else {
            ignoreComingTrackStop = true;
        }
        this.manager.loadItem(identifier, handler);
    }

    public boolean ignoreStop() {
        return this.ignoreComingTrackStop;
    }

    public void ignoredStop() {
        this.ignoreComingTrackStop = false;
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
        @Override
        public List<AudioFilter> buildChain(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
            return Collections.singletonList(new MergeChannelsAudioFilter(output));
        }

    }
}
