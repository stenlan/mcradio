package net.fabricmc.example;

import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PositionedRemoteSoundInstance extends PositionedSoundInstance {
    public PositionedRemoteSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, BlockPos pos) {
        this(sound, category, volume, pitch, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
    }

    public PositionedRemoteSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
        this(sound.getId(), category, volume, pitch, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z, false);
    }

    public PositionedRemoteSoundInstance(Identifier id, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, AttenuationType attenuationType, double x, double y, double z, boolean looping) {
        super(id, category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, looping);
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public Sound getSound() {
        return new Sound(this.id.toString(), this.volume, this.pitch, 1, Sound.RegistrationType.FILE, true, true, 16);
    }

    @Override
    public WeightedSoundSet getSoundSet(SoundManager soundManager) {
        return new WeightedSoundSet(this.id, this.id.getPath());
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }
}
