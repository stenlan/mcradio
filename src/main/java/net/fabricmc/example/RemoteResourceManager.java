package net.fabricmc.example;

import com.sedmelluq.discord.lavaplayer.filter.*;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_LE;

public class RemoteResourceManager extends NamespaceResourceManager {

    private static AudioDataFormat dataFormat = COMMON_PCM_S16_LE;
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private AudioPlayer player;

    public RemoteResourceManager(ResourceType type, String namespace) {
        super(type, namespace);
        playerManager.getConfiguration().setOutputFormat(dataFormat);
        AudioSourceManagers.registerRemoteSources(playerManager);
        player = playerManager.createPlayer();
        player.setFilterFactory(new StereoToMonoFilterFactory(playerManager.getConfiguration()));
    }

    public RemoteResourceManager(){
        this(ResourceType.CLIENT_RESOURCES, "remote");
    }

    @Override
    public Resource getResource(Identifier id) throws IOException {
        playerManager.loadItem("https://www.youtube.com/watch?v=k0dMSDwZj2g", new AudioLoadResultHandler() { // id.getPath
            @Override
            public void trackLoaded(AudioTrack track) {
                System.out.println("Playing!");
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
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
        });
        return new ResourceImpl("", id, AudioPlayerInputStream.createStream(player, dataFormat, 5000L, false), null);
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
            // return Collections.singletonList(new ResamplingPcmAudioFilter(config, 2, output, 44100, 22050 ));
            if (!initialized) {
                // filterList = Collections.singletonList(new StereoToMonoPcmAudioFilter(output));
                // filterList = Collections.singletonList(new ChannelCountPcmAudioFilter(2, 1, output));
//                filterList = new ArrayList<>();
//                filterList.add(new ResamplingPcmAudioFilter(config, 1, output, 88200, 44100));
//                filterList.add(new ChannelCountPcmAudioFilter(2, 1, filterList.get(0)));
//                Collections.reverse(filterList);
                filterList = Collections.singletonList(new MergeChannelsAudioFilter(output));
                initialized = true;
            }
            return filterList;
        }
    }
}
