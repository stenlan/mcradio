package com.github.ph0t0shop.mcradio.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class RadioResourceManager extends NamespaceResourceManager {
    private HashMap<BlockPos, PlayerWrapper> radios = new HashMap<>();
    private static RadioResourceManager instance = new RadioResourceManager();

    public static RadioResourceManager getInstance() {
        return instance;
    }

    private RadioResourceManager(){
        super(ResourceType.CLIENT_RESOURCES, "mcradio_remote");
    }

    @Override
    public Resource getResource(Identifier id) throws IOException {
        return this.getResource((RadioIdentifier) id);
    }

    private Resource getResource(RadioIdentifier id) throws IOException {
        BlockPos speakerPos = id.getSpeakerPos();
        if (!radios.containsKey(speakerPos)) {
            System.out.println("not returning resource");
            throw new IOException("Radio not registered");
        }
        PlayerWrapper player = radios.get(speakerPos);
        return new ResourceImpl("", id, player, null);
    }

    public PlayerWrapper registerRadio(BlockPos pos) {
        if (this.radios.containsKey(pos)) {
            return this.radios.get(pos);
        } else {
            PlayerWrapper player = new PlayerWrapper();
            this.radios.put(pos, player);
            return player;
        }
    }
}
