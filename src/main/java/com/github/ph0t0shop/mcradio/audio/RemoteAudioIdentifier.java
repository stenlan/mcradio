package com.github.ph0t0shop.mcradio.audio;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class RemoteAudioIdentifier extends Identifier {
    private BlockPos speakerPos;

    public RemoteAudioIdentifier(String url, BlockPos pos) {
        super("mcradio_remote", url);
        this.speakerPos = pos;
    }

    public double getSpeakerX() {
        return speakerPos.getX() + 0.5;
    }

    public double getSpeakerY() {
        return speakerPos.getY() + 0.5;
    }

    public double getSpeakerZ() {
        return speakerPos.getZ() + 0.5;
    }

}
