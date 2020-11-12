package com.github.ph0t0shop.mcradio.audio;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RadioIdentifier extends Identifier {
    private BlockPos speakerPos;

    public RadioIdentifier(BlockPos pos) {
        super("mcradio_remote", Long.toString(pos.asLong()));
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

    public BlockPos getSpeakerPos() {
        return this.speakerPos;
    }

}
