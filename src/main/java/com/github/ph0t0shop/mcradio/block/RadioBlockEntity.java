package com.github.ph0t0shop.mcradio.block;

import com.github.ph0t0shop.mcradio.MCRadio;
import com.github.ph0t0shop.mcradio.audio.PlayerWrapper;
import com.github.ph0t0shop.mcradio.audio.RadioSoundInstance;
import com.github.ph0t0shop.mcradio.audio.RadioIdentifier;
import com.github.ph0t0shop.mcradio.audio.RadioResourceManager;
import io.netty.util.internal.StringUtil;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;

public class RadioBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private String url = "";
    private PlayerWrapper player;

    public RadioBlockEntity() {
        super(MCRadio.RADIO_BLOCK_ENTITY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putString("url", url);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.url = tag.getString("url");
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        System.out.println("fromClientTag called");
        this.fromTag(MCRadio.RADIO_BLOCK.getDefaultState(), tag);
        player = RadioResourceManager.getInstance().registerRadio(this.pos);
        this.onUrlChanged();
    }

    private void onUrlChanged() {
        if (!StringUtil.isNullOrEmpty(this.url)) {
            if (!this.world.isClient) {
                LogManager.getLogger().warn("fromClientTag called on server, this shouldn't happen!");
                return;
            }
            player.playImmediate(this.url);
            MinecraftClient.getInstance().getSoundManager().play(new RadioSoundInstance(new RadioIdentifier(this.pos), 4.0F, 16, SoundInstance.AttenuationType.LINEAR));
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    public void setUrl(String url, boolean sync) {
        this.url = url;
        if (sync) {
            this.markDirty();
            this.sync();
        }
    }

    public String getUrl(){
        return this.url;
    }
}
